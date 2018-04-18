package com.blueskykong.tm.server.service;


import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.ServiceNameEnum;

/**
 * @author keets
 */
public interface OutputFactoryService<T> {

    /**
     * 发送事务消息
     *
     * @param msg
     * @return Boolean
     */
    Boolean sendMsg(TransactionMsg msg);

}
