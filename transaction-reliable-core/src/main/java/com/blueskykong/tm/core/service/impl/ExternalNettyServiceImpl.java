package com.blueskykong.tm.core.service.impl;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TxTransactionMsg;
import com.blueskykong.tm.common.enums.OperationEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.annotation.ReliableTransaction;
import com.blueskykong.tm.core.service.ExternalNettyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author keets
 */
public class ExternalNettyServiceImpl implements ExternalNettyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalNettyServiceImpl.class);

    @Override
    @ReliableTransaction
    public Boolean preSend(TxTransactionMsg preCommitMsgs) {
        if (preCommitMsgs != null) {
            LogUtil.info(LOGGER, () -> "发送preCommit消息");
        }
        return true;
    }

    @Override
    @ReliableTransaction(OperationEnum.TX_COMPLETE)
    public void postSend(Boolean success, Object message) {
        LogUtil.info(LOGGER, "发送confirm消息, {} ", () -> success);
    }

    @Override
    @ReliableTransaction(OperationEnum.TX_CONSUMED)
    public void consumedSend(TransactionMsg msg, Boolean success) {
        LogUtil.info(LOGGER, "发送Consume消息，groupId {} and subTaskId {}，消费结果为：{}", () -> msg.getGroupId(), () -> msg.getSubTaskId(), () -> success);
    }
}
