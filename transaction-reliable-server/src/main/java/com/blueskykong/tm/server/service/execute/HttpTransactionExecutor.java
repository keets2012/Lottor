package com.blueskykong.tm.server.service.execute;

import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.HeartBeat;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.server.config.ChannelSender;
import com.blueskykong.tm.server.config.ExecutorMessageTool;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


@Component
public class HttpTransactionExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransactionExecutor.class);


    public void rollBack(List<TxTransactionItem> txTransactionItems) {
        try {
            execute(txTransactionItems, TransactionStatusEnum.ROLLBACK);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(LOGGER, "txManger 发送rollback指令异常 ", e::getMessage);
        }

    }


    public void commit(List<TxTransactionItem> txTransactionItems) {
        try {
            execute(txTransactionItems, TransactionStatusEnum.COMMIT);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info(LOGGER, "txManger 发送commit 指令异常 ", e::getMessage);
        }
    }


    private void execute(List<TxTransactionItem> txTransactionItems, TransactionStatusEnum transactionStatusEnum) {
        if (CollectionUtils.isNotEmpty(txTransactionItems)) {
            final CompletableFuture[] cfs = txTransactionItems
                    .stream()
                    .map(item ->
                            CompletableFuture.runAsync(() -> {
                                ChannelSender channelSender = new ChannelSender();
                                final HeartBeat heartBeat = ExecutorMessageTool.buildMessage(item,
                                        channelSender, transactionStatusEnum);
                                if (Objects.nonNull(channelSender.getChannel())) {
                                    channelSender.getChannel().writeAndFlush(heartBeat);
                                } else {
                                    LOGGER.error("txMange {},指令失败，channel为空，事务组id：{}, 事务taskId为:{}",
                                            transactionStatusEnum.getDesc(), item.getTxGroupId(), item.getTaskKey());
                                }

                            }).whenComplete((v, e) ->
                                    LOGGER.info("txManger 成功发送 {} 指令 事务taskId为：{}", transactionStatusEnum.getDesc(), item.getTaskKey())))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(cfs).join();
        }
    }


}
