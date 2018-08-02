package com.blueskykong.lottor.core.service;

import com.blueskykong.lottor.common.bean.TxTransactionInfo;

@FunctionalInterface
public interface TxTransactionHandler {

    /**
     * 分布式事务处理接口
     *
     * @param info 信息
     * @return Object
     * @throws Throwable 异常
     */
    void handler(TxTransactionInfo info);
}
