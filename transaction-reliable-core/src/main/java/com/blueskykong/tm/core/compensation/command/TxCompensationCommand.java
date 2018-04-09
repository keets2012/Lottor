package com.blueskykong.tm.core.compensation.command;

import com.blueskykong.tm.common.bean.TransactionInvocation;
import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.enums.CompensationActionEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.core.compensation.TxCompensationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author keets
 */
public class TxCompensationCommand implements Command {

    private final TxCompensationService txCompensationService;

    @Autowired
    public TxCompensationCommand(TxCompensationService txCompensationService) {
        this.txCompensationService = txCompensationService;
    }

    /**
     * 执行命令接口
     *
     * @param txCompensationAction 封装命令信息
     */
    @Override
    public void execute(TxCompensationAction txCompensationAction) {
        txCompensationService.submit(txCompensationAction);
    }


    public String saveTxCompensation(TransactionInvocation invocation, String groupId, String taskId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.SAVE);
        TransactionRecover recover = new TransactionRecover();
        recover.setRetriedCount(1);
        recover.setStatus(TransactionStatusEnum.BEGIN.getCode());
        recover.setId(groupId);
        recover.setTransactionInvocation(invocation);
        recover.setGroupId(groupId);
        recover.setTaskId(taskId);
        recover.setCreateTime(new Date());
        action.setTransactionRecover(recover);
        execute(action);
        return recover.getId();
    }

    public void removeTxCompensation(String compensateId) {
        TxCompensationAction action = new TxCompensationAction();
        action.setCompensationActionEnum(CompensationActionEnum.DELETE);
        TransactionRecover recover = new TransactionRecover();
        recover.setId(compensateId);
        action.setTransactionRecover(recover);
        execute(action);
    }

}
