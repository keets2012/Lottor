package com.blueskykong.tm.core.service;

import com.blueskykong.tm.common.entity.TransactionMsg;

import java.util.List;

public interface ExternalNettyService {

    /**
     * pre-commit msgs
     *
     * @param preCommitMsgs
     */
    public Boolean preSend(List<TransactionMsg> preCommitMsgs);

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
