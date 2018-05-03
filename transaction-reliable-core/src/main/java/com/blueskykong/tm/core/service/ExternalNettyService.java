package com.blueskykong.tm.core.service;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TxTransactionMsg;

import java.util.List;

/**
 * @author keets
 */
public interface ExternalNettyService {

    /**
     * pre-commit msgs
     *
     * @param preCommitMsgs
     */
    public Boolean preSend(TxTransactionMsg preCommitMsgs);

    /**
     * confirm msgs
     *
     * @param success
     */
    public void postSend(Boolean success, Object message);

    /**
     * msgs after consuming
     *
     * @param msg
     * @param success
     */
    public void consumedSend(TransactionMsg msg, Boolean success);
}
