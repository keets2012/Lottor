package com.blueskykong.tm.server.netty.handler;


import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.NettyResultEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.HeartBeat;
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
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ChannelHandler.Sharable
@Component
public class NettyServerMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerMessageHandler.class);

    private final ThreadLocal<TxManagerService> txManagerService;

    private final TxTransactionExecutor txTransactionExecutor;

    @Autowired
    public NettyServerMessageHandler(TxManagerService txManagerService, TxTransactionExecutor txTransactionExecutor) {
        this.txManagerService = ThreadLocal.withInitial(() -> txManagerService);
        this.txTransactionExecutor = txTransactionExecutor;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HeartBeat hb = (HeartBeat) msg;
        TxTransactionGroup txTransactionGroup = hb.getTxTransactionGroup();
        List<TxTransactionItem> items = null;
        try {
            final NettyMessageActionEnum actionEnum = NettyMessageActionEnum.acquireByCode(hb.getAction());
            LogUtil.debug(LOGGER, "接收到客户端数据，执行的动作为:{}", actionEnum::getDesc);
            Boolean success;
            if (txTransactionGroup != null) {
                items = txTransactionGroup.getItemList();
            }
            switch (actionEnum) {
                case HEART:
                    hb.setAction(NettyMessageActionEnum.HEART.getCode());
                    ctx.writeAndFlush(hb);
                    break;
                case CREATE_GROUP:
                    //预提交，并创建事务组
                    if (CollectionUtils.isNotEmpty(items)) {
                        String modelName = ctx.channel().remoteAddress().toString();
                        //这里创建事务组的时候，事务组也作为第一条数据来存储
                        //第二条数据才是发起方 因此是get(1)
                        final TxTransactionItem item = items.get(1);
                        item.setModelName(modelName);
                        item.setTmDomain(Address.getInstance().getDomain());
                        txTransactionGroup.setItemList(Collections.singletonList(item));
                    }
                    success = txManagerService.get().saveTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(buildSendMessage(hb.getKey(), success));
                    break;
                case GET_TRANSACTION_GROUP_STATUS:
                    final int status = txManagerService.get().findTxTransactionGroupStatus(txTransactionGroup.getId());
                    txTransactionGroup.setStatus(status);
                    hb.setTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(hb);
                    break;
                case FIND_TRANSACTION_GROUP_INFO:
                    final List<TxTransactionItem> txTransactionItems = txManagerService.get().listByTxGroupId(txTransactionGroup.getId());
                    txTransactionGroup.setItemList(txTransactionItems);
                    hb.setTxTransactionGroup(txTransactionGroup);
                    ctx.writeAndFlush(hb);
                    break;
                case ROLLBACK:
                case COMPLETE_COMMIT:
                    if (CollectionUtils.isNotEmpty(items)) {
                        final TxTransactionItem item = items.get(0);
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

    private HeartBeat buildSendMessage(String key, Boolean success) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setKey(key);
        heartBeat.setAction(NettyMessageActionEnum.RECEIVE.getCode());
        if (success) {
            heartBeat.setResult(NettyResultEnum.SUCCESS.getCode());
        } else {
            heartBeat.setResult(NettyResultEnum.FAIL.getCode());
        }
        return heartBeat;

    }

}