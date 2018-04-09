
package com.blueskykong.tm.core.service.message;

import com.blueskykong.tm.common.enums.ConsumedStatus;
import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.HeartBeat;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.core.netty.handler.NettyClientMessageHandler;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Objects;

/**
 * @author keets
 */
public class NettyMessageServiceImpl implements TxManagerMessageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyMessageServiceImpl.class);

    private final ThreadLocal<NettyClientMessageHandler> nettyClientMessageHandler;

    @Autowired
    public NettyMessageServiceImpl(NettyClientMessageHandler nettyClientMessageHandler) {
        this.nettyClientMessageHandler = ThreadLocal.withInitial(() -> nettyClientMessageHandler);
    }

    @Override
    public Boolean saveTxTransactionGroup(TxTransactionGroup txTransactionGroup) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.CREATE_GROUP.getCode());
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;

    }

    @Override
    public Boolean addTxTransaction(String txGroupId, TxTransactionItem txTransactionItem) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.ADD_TRANSACTION.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        txTransactionGroup.setItemList(Collections.singletonList(txTransactionItem));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
        if (Objects.nonNull(object)) {
            return (Boolean) object;
        }
        return false;
    }

    @Override
    public int findTransactionGroupStatus(String txGroupId) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);

        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
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
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.FIND_TRANSACTION_GROUP_INFO.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
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
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.ROLLBACK.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        txTransactionGroup.setId(txGroupId);
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(TransactionStatusEnum.ROLLBACK.getCode());
        txTransactionGroup.setItemList(Collections.singletonList(item));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
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
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.PRE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setStatus(TransactionStatusEnum.PRE_COMMIT.getCode());
        txTransactionGroup.setId(txGroupId);
//        TxTransactionItem

        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
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
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(status);
        txTransactionGroup.setItemList(Collections.singletonList(item));
        heartBeat.setTxTransactionGroup(txTransactionGroup);
        final Object object = nettyClientMessageHandler.get().sendTxManagerMessage(heartBeat);
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
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.COMPLETE_COMMIT.getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);

        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setStatus(status);
        item.setMessage(message);

        txTransactionGroup.setItemList(Collections.singletonList(item));

        heartBeat.setTxTransactionGroup(txTransactionGroup);
        nettyClientMessageHandler.get().asyncSendTxManagerMessage(heartBeat);
    }

    /**
     * 提交参与者事务状态
     *
     * @param txGroupId 事务组id
     * @param hashKey   参与者
     * @param status    状态
     * @return true 成功 false 失败
     */
    //TODO 是否消费方持久化
    @Override
    public Boolean commitActorTxTransaction(String txGroupId, String hashKey, Boolean status) {
        HeartBeat heartBeat = new HeartBeat();
        heartBeat.setAction(NettyMessageActionEnum.CONSUMED.getCode());

        TxTransactionItem txTransactionItem = new TxTransactionItem();
        txTransactionItem.setTaskKey(hashKey);
        txTransactionItem.setStatus(status ? ConsumedStatus.CONSUMED_SUCCESS.getStatus() : ConsumedStatus.CONSUMED_FAILURE.getStatus());

        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(txGroupId);
        txTransactionGroup.setItemList(Collections.singletonList(txTransactionItem));

        heartBeat.setTxTransactionGroup(txTransactionGroup);
        LogUtil.debug(LOGGER, "消费方发送消息： {}", NettyMessageActionEnum.CONSUMED::getDesc);
        nettyClientMessageHandler.get().asyncSendTxManagerMessage(heartBeat);
        return true;
    }
}
