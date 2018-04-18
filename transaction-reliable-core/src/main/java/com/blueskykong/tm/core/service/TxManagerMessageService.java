
package com.blueskykong.tm.core.service;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;

/**
 * @author keets
 */
public interface TxManagerMessageService {

    /**
     * 保存事务组 在事务发起方的时候进行调用
     *
     * @param txTransactionGroup 事务组
     * @return true 成功 false 失败
     */
    Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup);


    /**
     * 往事务组添加事务
     *
     * @param txGroupId         事务组id
     * @param txTransactionItem 子事务项
     * @return true 成功 false 失败
     */
    Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem);


    /**
     * 获取事务组状态
     *
     * @param txGroupId 事务组id
     * @return 事务组状态
     */
    int findTransactionGroupStatus(String txGroupId);


    /**
     * 获取事务组信息
     *
     * @param txGroupId 事务组id
     * @return TxTransactionGroup
     */
    TxTransactionGroup findByTxGroupId(String txGroupId);


    /**
     * 通知tm 回滚整个事务组
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean rollBackTxTransaction(String txGroupId, String waitKey);


    /**
     * 通知tm自身业务已经执行完成，等待提交事务
     * tm 收到后，进行pre_commit  再进行doCommit
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    Boolean preCommitTxTransaction(String txGroupId);


    /**
     * 完成提交自身的事务
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态
     * @return true 成功 false 失败
     */
    Boolean completeCommitTxTransaction(String txGroupId, String taskKey, int status);


    /**
     * 异步完成自身的提交
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态
     * @param message   完成信息 返回结果，或者是异常信息
     */
    void asyncCompleteCommit(String txGroupId, String taskKey, int status, Object message);

    /**
     * 异步完成自身的消费
     *
     * @param message   完成信息 返回结果，或者是异常信息
     */
    void asyncCompleteConsume(TransactionMsg message);

}
