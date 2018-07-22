package com.blueskykong.tm.server.service;

import com.blueskykong.tm.common.netty.bean.TxTransactionItem;

import java.util.List;

public interface TxTransactionExecutor {


    /**
     * 回滚整个事务组
     *
     * @param txGroupId 事务组id
     */
    void rollBack(String txGroupId);


    void checkTxGroup(List<TxTransactionItem> items);

    /**
     * 事务预提交
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean preCommit(String txGroupId);


}
