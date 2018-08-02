package com.blueskykong.lottor.core.service.message;

import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.enums.NettyMessageActionEnum;
import com.blueskykong.lottor.common.enums.TransactionStatusEnum;
import com.blueskykong.lottor.common.netty.bean.LottorRequest;
import com.blueskykong.lottor.common.netty.bean.TxTransactionGroup;
import com.blueskykong.lottor.common.netty.bean.TxTransactionItem;
import com.blueskykong.lottor.core.netty.handler.NettyClientMessageHandler;
import com.blueskykong.lottor.core.service.TxManagerMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class NettyMessageServiceImpl implements TxManagerMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMessageServiceImpl.class);

    private final ThreadLocal<NettyClientMessageHandler> nettyClientMessageHandler;

    public NettyMessageServiceImpl(NettyClientMessageHandler nettyClientMessageHandler) {
        this.nettyClientMessageHandler = ThreadLocal.withInitial(() -> nettyClientMessageHandler);
    }

    @Override
    public Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.CREATE_GROUP.getCode());
        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;

    }

    @Override
    public Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.ADD_TRANSACTION.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        txTransactionGroup.setItem(txTransactionItem);
        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    @Override
    public int findTransactionGroupStatus(String txGroupId) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);

        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (Integer) object;
        }
        return TransactionStatusEnum.ROLLBACK.getCode();

    }

    /**
     * 获取事务组信息
     *
     * @param txGroupId 事务组id
     * @return TxTransactionGroup
     */
    @Override
    public TxTransactionGroup findByTxGroupId(String txGroupId) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.FIND_TRANSACTION_GROUP_INFO.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (TxTransactionGroup) object;
        }
        return null;
    }

    /**
     * 通知tm 回滚整个事务组
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    @Override
    public Boolean rollBackTxTransaction(String txGroupId, String taskKey) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        txTransactionGroup.setId(txGroupId);
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        txTransactionGroup.setItem(item);
        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    /**
     * 通知tm自身业务已经执行完成，等待提交事务
     * tm 收到后，进行pre_commit  再进行doCommit
     *
     * @param txGroupId 事务组id
     * @return true 成功 false 失败
     */
    @Override
    public Boolean preCommitTxTransaction(String txGroupId) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.PRE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setStatus(TransactionStatusEnum.PRE_COMMIT.getCode());
        txTransactionGroup.setId(txGroupId);
//        TxTransactionItem

        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    /**
     * 完成提交自身的事务
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态
     * @return true 成功 false 失败
     */
    @Override
    public Boolean completeCommitTxTransaction(String txGroupId, String taskKey, int status) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(status);
        txTransactionGroup.setItem(item);
        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(lottorRequest);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    /**
     * 异步完成自身事务的提交
     *
     * @param txGroupId 事务组id
     * @param taskKey   子事务的taskKey
     * @param status    状态  {@linkplain TransactionStatusEnum}
     * @param message   完成信息 返回结果，或者是异常信息
     */
    @Override
    public void asyncCompleteCommit(String txGroupId, String taskKey, int status, Object message) {
        LottorRequest lottorRequest = new LottorRequest();
        lottorRequest.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);

        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(status);
        item.setMessage(message);

        txTransactionGroup.setItem(item);

        lottorRequest.setTxTransactionGroup(txTransactionGroup);
        nettyClientMessageHandler.get().asyncSendTxManagerMessage(lottorRequest);
    }

    /**
     * 异步完成自身的消费
     *
     * @param message 完成信息 返回结果，或者是异常信息
     */
    @Override
    public void asyncCompleteConsume(TransactionMsg message) {
        LottorRequest lottorRequest = new LottorRequest();

        lottorRequest.setAction(NettyMessageActionEnum.CONSUMED.getCode());
        lottorRequest.setTransactionMsg(message);

        nettyClientMessageHandler.get().asyncSendTxManagerMessage(lottorRequest);
    }


}
