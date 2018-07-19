package com.blueskykong.tm.server.task;

import com.blueskykong.tm.common.concurrent.threadpool.TxTransactionThreadFactory;
import com.blueskykong.tm.common.entity.TransactionMsg;
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
import com.blueskykong.tm.server.socket.SocketManager;
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
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.blueskykong.tm.common.enums.NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS;

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
                TxTransactionThreadFactory.create("CompensationService", true));
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
                    LogUtil.debug(LOGGER, "check for unCommitted tx-groups&&tx-msg and execute delayTime:{}", () -> txConfig.getDelayTime());

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
            txTransactionItems.stream().forEach(txTransactionItem -> {
                baseItemService.updateItem(new BaseItem(CollectionNameEnum.TxTransactionItem.getType(), txTransactionItem.getTxGroupId()));

                String service = txTransactionItem.getModelName();
                LottorRequest request = new LottorRequest();
                request.setAction(GET_TRANSACTION_GROUP_STATUS.getCode());
                List<ChannelHandlerContext> contexts = nettyServerMessageHandler.getCtxByName(service);
                Assert.notNull(contexts, "no available servers.");
                Collections.shuffle(contexts);
                Channel context = contexts.stream().findAny().get().channel();
                if (context.isActive()) {
                    request.setKey(txTransactionItem.getTxGroupId());
                    context.writeAndFlush(request);
                } else {
                    throw new TransactionRuntimeException("no available servers.");
                }
            });
        }
    }

    private void checkTxMsg() {
        List<TransactionMsg> transactionMsgs = txManagerService.listTxMsgByDelay(period);
        LogUtil.info(LOGGER, "schedule check tx-msg at {} and transactionMsgs size id {}", () -> getNowDate(), () -> transactionMsgs.size());

        if (CollectionUtils.isNotEmpty(transactionMsgs)) {
            transactionMsgs.stream().filter(transactionMsg -> {
                if (StringUtils.isBlank(transactionMsg.getTarget())) {
                    return false;
                }
                return true;
            }).forEach(transactionMsg -> {

                baseItemService.updateItem(new BaseItem(CollectionNameEnum.TransactionMsg.getType(), transactionMsg.getSubTaskId()));
                LottorRequest request = new LottorRequest();
                request.setAction(NettyMessageActionEnum.GET_TRANSACTION_MSG_STATUS.getCode());
                List<ChannelHandlerContext> contexts = nettyServerMessageHandler.getCtxByName(transactionMsg.getTarget());
                Assert.notNull(contexts, "no available servers.");
                Collections.shuffle(contexts);
                Channel context = contexts.stream().findAny().get().channel();
                if (context.isActive()) {
                    request.setKey(transactionMsg.getGroupId());
                    request.setTransactionMsg(transactionMsg);
                    context.writeAndFlush(request);
                } else {
                    throw new TransactionRuntimeException("no available servers.");
                }
            });
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
