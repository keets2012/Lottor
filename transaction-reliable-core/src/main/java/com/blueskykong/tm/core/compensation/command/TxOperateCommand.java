package com.blueskykong.tm.core.compensation.command;

import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.CompensationActionEnum;
import com.blueskykong.tm.common.enums.ConsumedStatus;
import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.core.compensation.TxOperateService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author keets
 */
public class TxOperateCommand implements Command {

    private final TxOperateService txOperateService;

    public TxOperateCommand(TxOperateService txOperateService) {
        this.txOperateService = txOperateService;
    }

    /**
     * 执行命令接口
     *
     * @param txOperateAction 封装命令信息
     */
    @Override
    public void execute(TxOperateAction txOperateAction) {
        txOperateService.submit(txOperateAction);
    }


    public String saveTxCompensation(List<TransactionMsg> msgs, String groupId, String taskId) {
        TxOperateAction action = new TxOperateAction();
        action.setCompensationActionEnum(CompensationActionEnum.SAVE);
        TransactionRecover recover = new TransactionRecover();
        recover.setRetriedCount(1);
        recover.setStatus(TransactionStatusEnum.BEGIN.getCode());
        recover.setId(groupId);
        recover.setTransactionMsgs(msgs);
        recover.setGroupId(groupId);
        recover.setTaskId(taskId);
        recover.setCreateTime(new Date());
        action.setTransactionRecover(recover);
        execute(action);
        return recover.getId();
    }


    public String updateTxCompensation(String groupId, int status) {
        TxOperateAction action = new TxOperateAction();
        action.setCompensationActionEnum(CompensationActionEnum.UPDATE);
        TransactionRecover recover = new TransactionRecover();
        recover.setStatus(status);
        recover.setId(groupId);
        recover.setGroupId(groupId);
        action.setTransactionRecover(recover);
        execute(action);
        return recover.getId();
    }

    public void removeTxCompensation(String compensateId) {
        TxOperateAction action = new TxOperateAction();
        action.setCompensationActionEnum(CompensationActionEnum.DELETE);
        TransactionRecover recover = new TransactionRecover();
        recover.setId(compensateId);
        action.setTransactionRecover(recover);
        execute(action);
    }

    public TransactionRecover getTxCompensation(String compensateId) {
        return txOperateService.findTransactionRecover(compensateId);
    }

    public LottorRequest getTxGroupStatus(String key) {
        TransactionRecover transactionRecover = getTxCompensation(key);
        if (Objects.isNull(transactionRecover)) {
            return null;
        }
        int status = transactionRecover.getStatus();

        LottorRequest request = new LottorRequest();
        request.setAction(NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS
                .getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(key);
        txTransactionGroup.setStatus(status);
        request.setTxTransactionGroup(txTransactionGroup);
        request.setKey(key);
        return request;
    }

    public LottorRequest getTxMsgStatus(String key) {
        TransactionMsg msg = txOperateService.findTransactionMsg(key);
        LottorRequest lottorRequest = new LottorRequest();
        if (!Objects.nonNull(msg)) {
            msg.setSubTaskId(key);
            msg.setConsumed(ConsumedStatus.UNCONSUMED.getStatus());
        }
        lottorRequest.setTransactionMsg(msg);
        return lottorRequest;
    }
}
