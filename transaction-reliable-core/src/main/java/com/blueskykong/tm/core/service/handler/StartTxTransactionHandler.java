package com.blueskykong.tm.core.service.handler;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.tm.common.concurrent.threadlocal.TxTransactionTaskLocal;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TransactionMsgAdapter;
import com.blueskykong.tm.common.enums.TransactionRoleEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.exception.TransactionException;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.helper.TransactionMsgHelper;
import com.blueskykong.tm.common.holder.DateUtils;
import com.blueskykong.tm.common.holder.IdWorkerUtils;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.compensation.command.TxOperateCommand;
import com.blueskykong.tm.core.service.ModelNameService;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author keets
 */
public class StartTxTransactionHandler implements TxTransactionHandler {

    /**
     * pre-commit tx-group
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StartTxTransactionHandler.class);

    private final TxOperateCommand txOperateCommand;

    private final TxManagerMessageService txManagerMessageService;

    private final ObjectSerializer objectSerializer;

    private ModelNameService modelNameService;

    public StartTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxOperateCommand txOperateCommand,
                                     ObjectSerializer objectSerializer, ModelNameService modelNameService) {
        this.objectSerializer = objectSerializer;
        this.txManagerMessageService = txManagerMessageService;
        this.txOperateCommand = txOperateCommand;
        this.modelNameService = modelNameService;
    }


    @Override
    public Object handler(TxTransactionInfo info) {
        LogUtil.info(LOGGER, "tx-transaction start,  事务发起类：{}",
                () -> "");

        final String groupId = IdWorkerUtils.getInstance().createGroupId();

        //设置事务组ID
        TxTransactionLocal.getInstance().setTxGroupId(groupId);

        final String waitKey = IdWorkerUtils.getInstance().createTaskKey();
        TxTransactionTaskLocal.getInstance().setTxTaskId(waitKey);

        //创建事务组信息，预提交事务组
        final Boolean success = txManagerMessageService.saveTxTransactionGroup(newTxTransactionGroup(groupId, waitKey, info));
        if (success) {
            try {
                //本地服务记录，用作补偿
                txOperateCommand.saveTxCompensation((List<TransactionMsg>) info.getArgs()[0], groupId, waitKey);
                return "";
            } catch (Throwable throwable) {
                //通知tm整个事务组失败，需要回滚，标志事务组的状态
                //TODO ROLLBACK待优化
                txManagerMessageService.rollBackTxTransaction(groupId, waitKey);
                txOperateCommand.updateTxCompensation(groupId, TransactionStatusEnum.ROLLBACK.getCode());
                LogUtil.error(LOGGER, "failed to start TxTransaction for: {}", throwable::getLocalizedMessage);
                throw throwable;
            }
        } else {
            throw new TransactionRuntimeException("TxManager 连接异常！");
        }
    }

    private TxTransactionGroup newTxTransactionGroup(String groupId, String taskKey, TxTransactionInfo info) {
        //创建事务组信息
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(groupId);

        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setTransId(IdWorkerUtils.getInstance().createUUID());
        item.setRole(TransactionRoleEnum.START.getCode());
        item.setStatus(TransactionStatusEnum.PRE_COMMIT.getCode());
        item.setTxGroupId(groupId);
        item.setModelName(modelNameService.findModelName());

        //设置事务最大等待时间
        item.setWaitMaxTime(info.getWaitMaxTime());
        //设置创建时间
        item.setCreateDate(DateUtils.getCurrentDateTime());
        //设置参数，只会是第一个
        List<TransactionMsg> msgList = (List<TransactionMsg>) info.getArgs()[0];
        List<TransactionMsgAdapter> adapters = msgList.stream().map(msg -> {
            TransactionMsgAdapter transactionMsgAdapter = null;
            try {
                byte[] args = objectSerializer.serialize(msg.getArgs());
                transactionMsgAdapter = TransactionMsgHelper.convertTransactionMsg(msg);
                transactionMsgAdapter.setArgs(args);
            } catch (TransactionException e) {
                e.printStackTrace();
                LogUtil.error(LOGGER, "failed to serialize transactionMsg, groupId is: {}, cause is: {}", () -> groupId, e::getLocalizedMessage);
            }
            return transactionMsgAdapter;
        }).collect(Collectors.toList());

        item.setMsgs(adapters);

        txTransactionGroup.setItem(item);
        return txTransactionGroup;
    }


}
