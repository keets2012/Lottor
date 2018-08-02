package com.blueskykong.lottor.server.service;


import com.blueskykong.lottor.common.entity.TransactionMsgAdapter;

public interface OutputFactoryService<T> {

    /**
     * 发送事务消息
     *
     * @param msg
     * @return Boolean
     */
    Boolean sendMsg(TransactionMsgAdapter msg);

}
