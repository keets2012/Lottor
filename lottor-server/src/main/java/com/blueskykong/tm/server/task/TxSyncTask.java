package com.blueskykong.tm.server.task;

import com.blueskykong.tm.common.concurrent.threadpool.TxTransactionThreadFactory;
import com.blueskykong.tm.common.entity.TransactionMsgAdapter;
import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.holder.Assert;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.BaseItem;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.config.NettyConfig;
import com.blueskykong.tm.server.entity.CollectionNameEnum;
import com.blueskykong.tm.server.netty.handler.NettyServerMessageHandler;
import com.blueskykong.tm.server.service.BaseItemService;
import com.blueskykong.tm.server.service.TxManagerService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.blueskykong.tm.common.enums.NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS;
import static com.blueskykong.tm.common.enums.NettyMessageActionEnum.GET_TRANSACTION_MSG_STATUS;


public class TxSyncTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxSyncTask.class);

    private NettyConfig txConfig;

    private final long period = 8 * 60;

    private TxManagerService txManagerService;

    private ScheduledExecutorService scheduledExecutorService;

    private NettyServerMessageHandler nettyServerMessageHandler;

    private BaseItemService baseItemService;

    public TxSyncTask(TxManagerService txManagerService, NettyServerMessageHandler nettyServerMessageHandler,
                      NettyConfig nettyConfig, BaseItemService baseItemService) {
        this.txManagerService = txManagerService;
        this.nettyServerMessageHandler = nettyServerMessageHandler;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("CheckService", true));
        this.txConfig = nettyConfig;
        this.baseItemService = baseItemService;
    }

    /**
     * 启动本地check事务，根据配置是否进行check
     */
    public void start() {
        if (txConfig.getCheck()) {
            scheduleCheck();//执行定时检查
        }
    }

    private void scheduleCheck() {
        scheduledExecutorService
                .scheduleAtFixedRate(() -> {
                    LogUtil.debug(LOGGER, "check for unCommitted tx-groups&&tx-msg and execute delayTime: {}", () -> txConfig.getInitDelay());

                    // 检测事务组信息的状态
                    checkTxGroup();

                    //检测事务消息的状态
                    checkTxMsg();
                }, txConfig.getInitDelay(), txConfig.getCheckPeriod(), TimeUnit.MINUTES);
    }

    private void checkTxGroup() {
        final List<TxTransactionItem> txTransactionItems = txManagerService.listTxItemByDelay(period);
        LogUtil.info(LOGGER, "schedule check tx-group at {} and txTransactionItems size is {}", () -> getNowDate(), () -> txTransactionItems.size());
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            CompletableFuture[] cfs = txTransactionItems.stream().map(txTransactionItem -> CompletableFuture.runAsync(() -> {
                baseItemService.updateItem(new BaseItem(CollectionNameEnum.TxTransactionItem.getType(), txTransactionItem.getTxGroupId())); //TODO 增加重试次数

                String service = txTransactionItem.getModelName();
                LottorRequest request = new LottorRequest();
                request.setAction(GET_TRANSACTION_GROUP_STATUS.getCode());
                List<ChannelHandlerContext> contexts = nettyServerMessageHandler.getCtxByName(service);
                if (CollectionUtils.isNotEmpty(contexts)) {
                    LogUtil.error(LOGGER, "no available servers for check tx-group, group-id is {} .", txTransactionItem::getTxGroupId);
                    return;
                }
                Collections.shuffle(contexts);
                Channel context = contexts.stream().findAny().get().channel();
                if (context.isActive()) {
                    request.setKey(txTransactionItem.getTxGroupId());
                    context.writeAndFlush(request);
                } else {
                    LogUtil.error(LOGGER, "channel context is inactive for service: {}, and msg-id is {} .",
                            () -> service, txTransactionItem::getTxGroupId);
                    return;
                }
            }).exceptionally(e -> {
                LogUtil.error(LOGGER, "no available servers for check tx-group, group-id is {} and cause is {}.",
                        txTransactionItem::getTxGroupId, e::getLocalizedMessage);
                return null;
            }).whenComplete((v, e) ->
                    LOGGER.info("txManger 成功发送 {} 指令, 事务groupId为：{}", GET_TRANSACTION_GROUP_STATUS.getDesc(), txTransactionItem.getTxGroupId())))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(cfs).join();
        }
    }

    private void checkTxMsg() {
        List<TransactionMsgAdapter> transactionMsgs = txManagerService.listTxMsgByDelay(period);
        LogUtil.info(LOGGER, "schedule check tx-msg at {} and transactionMsgs size is {}", () -> getNowDate(), () -> transactionMsgs.size());

        if (CollectionUtils.isNotEmpty(transactionMsgs)) {
            CompletableFuture[] cfs = transactionMsgs.stream().filter(transactionMsg -> {
                if (StringUtils.isBlank(transactionMsg.getTarget())) {
                    return false;
                }
                return true;
            }).map(transactionMsg -> CompletableFuture.runAsync(() -> {
                baseItemService.updateItem(new BaseItem(CollectionNameEnum.TransactionMsg.getType(), transactionMsg.getSubTaskId()));
                LottorRequest request = new LottorRequest();
                request.setAction(NettyMessageActionEnum.GET_TRANSACTION_MSG_STATUS.getCode());
                List<ChannelHandlerContext> contexts = nettyServerMessageHandler.getCtxByName(transactionMsg.getTarget());
                if (CollectionUtils.isNotEmpty(contexts)) {
                    LogUtil.error(LOGGER, "no available servers for check tx-msg, msg-id is {} .", transactionMsg::getSubTaskId);
                    return;
                }
                Collections.shuffle(contexts);
                Channel context = contexts.stream().findAny().get().channel();
                if (context.isActive()) {
                    request.setKey(transactionMsg.getGroupId());
                    request.setTransactionMsg(TransactionMsgAdapter.convert(transactionMsg));
                    context.writeAndFlush(request);
                } else {
                    LogUtil.error(LOGGER, "channel context is inactive for service: {}, and msg-id is {} .",
                            transactionMsg::getTarget, transactionMsg::getSubTaskId);
                    return;
                }
            }).exceptionally(e -> {
                LogUtil.error(LOGGER, "no available servers for check tx-msg, msg-id is {} and cause is {}.",
                        transactionMsg::getSubTaskId, e::getLocalizedMessage);
                return null;
            }).whenComplete((v, e) ->
                    LOGGER.info("txManger 成功发送 {} 指令 事务msgId为：{}", GET_TRANSACTION_MSG_STATUS.getDesc(), transactionMsg.getSubTaskId())))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(cfs).join();
        }
    }


    private String getNowDate() {
        String now;
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
        now = sdf.format(dt);
        return now;
    }

}
