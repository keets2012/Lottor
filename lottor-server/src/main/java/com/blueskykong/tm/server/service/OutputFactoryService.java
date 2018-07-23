package com.blueskykong.tm.server.service;


import com.blueskykong.tm.common.entity.TransactionMsgAdapter;

public interface OutputFactoryService<T> {

    /**
     * 发送事务消息
     *
     * @param msg
     * @return Boolean
     */
    Boolean sendMsg(TransactionMsgAdapter msg);

}
