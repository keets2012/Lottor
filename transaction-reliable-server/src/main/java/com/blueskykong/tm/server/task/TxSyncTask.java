package com.blueskykong.tm.server.task;

import com.blueskykong.tm.common.concurrent.threadpool.TxTransactionThreadFactory;
import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.netty.handler.NettyServerMessageHandler;
import com.blueskykong.tm.server.service.TxManagerService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.blueskykong.tm.common.enums.NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS;

public class TxSyncTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxSyncTask.class);

    private TxConfig txConfig;

    private final long period = 8 * 60;

    private TxManagerService txManagerService;

    private ScheduledExecutorService scheduledExecutorService;

    private NettyServerMessageHandler nettyServerMessageHandler;

    public TxSyncTask(TxManagerService txManagerService, NettyServerMessageHandler nettyServerMessageHandler) {
        this.txManagerService = txManagerService;
        this.nettyServerMessageHandler = nettyServerMessageHandler;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                TxTransactionThreadFactory.create("CompensationService", true));
    }

    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     */
    public void start(TxConfig txConfig) throws Exception {
        this.txConfig = txConfig;
        if (txConfig.getCompensation()) {
            scheduleCheck();//执行定时检查
        }
    }

    private void scheduleCheck() {
        scheduledExecutorService
                .scheduleAtFixedRate(() -> {
                    LogUtil.debug(LOGGER, "check for unCommitted tx-groups&&tx-msg and execute delayTime:{}", () -> txConfig.getCompensationRecoverTime());

                    // 检测事务组信息的状态
                    checkTxGroup();

                    //检测事务消息的状态
                    checkTxMsgWorker();
                }, 30, 45, TimeUnit.MINUTES);
    }

    private void checkTxGroup() {
        final List<TxTransactionItem> txTransactionItems = txManagerService.listTxItemByDelay(period);
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            txTransactionItems.stream().forEach(txTransactionItem -> {
                String service = txTransactionItem.getModelName();
                LottorRequest request = new LottorRequest();
                request.setAction(GET_TRANSACTION_GROUP_STATUS.getCode());
                List<ChannelHandlerContext> contexts = nettyServerMessageHandler.getCtxByName(service);
                if (contexts != null) {
                    Collections.shuffle(contexts);
                    ChannelHandlerContext context = contexts.stream().findAny().get();
                    request.setKey(txTransactionItem.getTxGroupId());
                    context.writeAndFlush(request);
                } else {
                    throw new TransactionRuntimeException("no available servers.");
                }
            });
        }
    }

    private void checkTxMsgWorker() {
        List<TransactionMsg> transactionMsgs = txManagerService.listTxMsgByDelay(period);
        if (CollectionUtils.isNotEmpty(transactionMsgs)) {
            transactionMsgs.stream().filter(transactionMsg -> {
                if (StringUtils.isBlank(transactionMsg.getTarget())) {
                    return false;
                }
                return true;
            }).forEach(transactionMsg -> {
                LottorRequest request = new LottorRequest();
                request.setAction(NettyMessageActionEnum.GET_TRANSACTION_MSG_STATUS.getCode());
                List<ChannelHandlerContext> contexts = nettyServerMessageHandler.getCtxByName(transactionMsg.getTarget());
                if (contexts != null) {
                    Collections.shuffle(contexts);
                    ChannelHandlerContext context = contexts.stream().findAny().get();
                    request.setKey(transactionMsg.getGroupId());
                    request.setTransactionMsg(transactionMsg);
                    context.writeAndFlush(request);
                } else {
                    throw new TransactionRuntimeException("no available servers.");
                }
            });
        }
    }

}
