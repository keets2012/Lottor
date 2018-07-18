package com.blueskykong.tm.server.netty.handler;


import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.NettyResultEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.config.Address;
import com.blueskykong.tm.server.service.TxManagerService;
import com.blueskykong.tm.server.service.TxTransactionExecutor;
import com.blueskykong.tm.server.socket.SocketManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
@Component
public class NettyServerMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerMessageHandler.class);

    private final ThreadLocal<TxManagerService> txManagerService;

    private final TxTransactionExecutor txTransactionExecutor;

    private ConcurrentHashMap<String, Integer> clients = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, List<ChannelHandlerContext>> clientContext = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, List<ChannelHandlerContext>> getClientContext() {
        return clientContext;
    }

    public List<ChannelHandlerContext> getCtxByName(String service) {
        List<ChannelHandlerContext> contexts = new ArrayList<>();
        if (StringUtils.isNotBlank(service)) {
            contexts = clientContext.get(service);
        }
        return contexts;
    }

    @Autowired
    public NettyServerMessageHandler(TxManagerService txManagerService, TxTransactionExecutor txTransactionExecutor) {
        this.txManagerService = ThreadLocal.withInitial(() -> txManagerService);
        this.txTransactionExecutor = txTransactionExecutor;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LottorRequest hb = (LottorRequest) msg;
        TxTransactionGroup txTransactionGroup = hb.getTxTransactionGroup();
        String clientCtx = ctx.channel().remoteAddress().toString();
        // initial with zero
        clients.putIfAbsent(clientCtx, 0);
        TxTransactionItem item = null;
        try {
            final NettyMessageActionEnum actionEnum = NettyMessageActionEnum.acquireByCode(hb.getAction());
            LogUtil.debug(LOGGER, "接收到客户端 {} 事件，执行的动作为:{}", () -> clientCtx, actionEnum::getDesc);
            Boolean success;
            if (Objects.nonNull(txTransactionGroup)) {
                item = txTransactionGroup.getItem();
            }
            switch (actionEnum) {
                case HEART:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    if (clients.get(clientCtx) < 1 && txTransactionGroup != null && txTransactionGroup.getSource() != null) {
                        SocketManager.getInstance().completeClientInfo(ctx.channel(), hb.getMetaInfo(), hb.getSerialProtocol());
                        clients.computeIfPresent(clientCtx, (clientValue, val) -> clients.get(clientValue) + 1);
                        String source = txTransactionGroup.getSource();
                        List<ChannelHandlerContext> contexts;
                        contexts = getCtxByName(source);
                        if (contexts != null) {
                            contexts.add(ctx);
                            clientContext.put(source, contexts);
                        } else {
                            contexts = new ArrayList<>();
                            contexts.add(ctx);
                            clientContext.putIfAbsent(source, contexts);
                        }
                        LogUtil.debug(LOGGER, "heart set {} : {}", () -> clientCtx, () -> clients.get(clientCtx));
                    }
                    ctx.writeAndFlush(hb);
                    break;
                case CREATE_GROUP:
                    //预提交，并创建事务组
                    if (Objects.nonNull(item)) {
                        item.setTmDomain(Address.getInstance().getDomain());
                        txTransactionGroup.setItem(item);
                    }
                    success = txManagerService.get().saveTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), success));
                    break;
                case GET_TRANSACTION_GROUP_STATUS:
                    Boolean updateGroup = false;
                    if (Objects.nonNull(txTransactionGroup)) {
                        updateGroup = txManagerService.get().updateTxTransactionItemStatus(hb.getKey(), null,
                                txTransactionGroup.getStatus(), null);
                    }
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), updateGroup));
                    break;
                case GET_TRANSACTION_MSG_STATUS:
                    Boolean updateMsg = false;
                    TransactionMsg consumeMsg = hb.getTransactionMsg();
                    if (Objects.nonNull(consumeMsg)) {
                        updateMsg = txManagerService.get().updateTxTransactionMsgStatus(consumeMsg);
                    }
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), updateMsg));
                    break;
                case FIND_TRANSACTION_GROUP_INFO:
                    final List<TxTransactionItem> txTransactionItems = txManagerService.get().listByTxGroupId(txTransactionGroup.getId());
//                    txTransactionGroup.setItemList(txTransactionItems);
                    hb.setTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(hb);
                    break;
                case ROLLBACK:
                case COMPLETE_COMMIT:
                    if (Objects.nonNull(item)) {
                        txManagerService.get().updateTxTransactionItemStatus(txTransactionGroup.getId(),
                                item.getTaskKey(),
                                item.getStatus(), item.getMessage());
                    }
                    break;
                case CONSUMED:
                    TransactionMsg transactionMsg = hb.getTransactionMsg();
                    if (transactionMsg != null) {
                        txManagerService.get().updateTxTransactionMsgStatus(transactionMsg);
                    }
                    break;
                case SYNC_TX_STATUS:

                    break;
                default:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    ctx.writeAndFlush(hb);
                    break;
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //是否到达最大上线连接数
        if (SocketManager.getInstance().isAllowConnection()) {
            SocketManager.getInstance().addClient(ctx.channel());
        } else {
            ctx.close();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        SocketManager.getInstance().removeClient(ctx.channel());
        clients.remove(ctx.channel().remoteAddress().toString());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
        }
    }

    private LottorRequest buildSendMessage(String key, Boolean success) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setKey(key);
        lottorRequest.setAction(NettyMessageActionEnum.RECEIVE.getCode());
        if (success) {
            lottorRequest.setResult(NettyResultEnum.SUCCESS.getCode());
        } else {
            lottorRequest.setResult(NettyResultEnum.FAIL.getCode());
        }
        return lottorRequest;

    }

}