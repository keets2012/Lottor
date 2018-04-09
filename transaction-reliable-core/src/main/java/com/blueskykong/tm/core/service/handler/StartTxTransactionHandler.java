package com.blueskykong.tm.core.service.handler;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.enums.TransactionRoleEnum;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.holder.DateUtils;
import com.blueskykong.tm.common.holder.IdWorkerUtils;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.common.netty.bean.TxTransactionItem;
import com.blueskykong.tm.core.compensation.command.TxCompensationCommand;
import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionTaskLocal;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author keets
 */
public class StartTxTransactionHandler implements TxTransactionHandler {

    /**
     * pre-commit tx-group
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StartTxTransactionHandler.class);

    private final TxCompensationCommand txCompensationCommand;

    private final TxManagerMessageService txManagerMessageService;

    @Autowired(required = false)
    public StartTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxCompensationCommand txCompensationCommand) {
        this.txManagerMessageService = txManagerMessageService;
        this.txCompensationCommand = txCompensationCommand;

    }


    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
        LogUtil.info(LOGGER, "tx-transaction start,  事务发起类：{}",
                () -> point.getTarget().getClass());

        final String groupId = IdWorkerUtils.getInstance().createGroupId();

        //设置事务组ID
        TxTransactionLocal.getInstance().setTxGroupId(groupId);

        final String waitKey = IdWorkerUtils.getInstance().createTaskKey();
        TxTransactionTaskLocal.getInstance().setTxTaskId(waitKey);

        //创建事务组信息，预提交事务组
        final Boolean success = txManagerMessageService.saveTxTransactionGroup(newTxTransactionGroup(groupId, waitKey, info));
        if (success) {
            try {
                final Object res = point.proceed();
                //本地服务记录，用作补偿
                txCompensationCommand.saveTxCompensation(info.getInvocation(), groupId, waitKey);

                return res;
            } catch (Throwable throwable) {
                //通知tm整个事务组失败，需要回滚，标志事务组的状态
                //TODO ROLLABCK待优化
                txManagerMessageService.rollBackTxTransaction(groupId, waitKey);
                throwable.printStackTrace();
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

        List<TxTransactionItem> items = new ArrayList<>(2);

        //tmManager 用redis hash 结构来存储 整个事务组的状态做为hash结构的第一条数据

        TxTransactionItem groupItem = new TxTransactionItem();

        //整个事务组状态为预提交
        groupItem.setStatus(TransactionStatusEnum.PRE_COMMIT.getCode());

        //设置事务id为组的id  即为 hashKey
        groupItem.setTransId(groupId);
        groupItem.setTaskKey(groupId);
        groupItem.setCreateDate(DateUtils.getCurrentDateTime());

        //设置执行类名称
        groupItem.setTargetClass(info.getInvocation().getTargetClazz().getName());
        //设置执行类方法
        groupItem.setTargetMethod(info.getInvocation().getMethod());

        groupItem.setRole(TransactionRoleEnum.GROUP.getCode());


        items.add(groupItem);

        TxTransactionItem item = new TxTransactionItem();
        item.setTaskKey(taskKey);
        item.setTransId(IdWorkerUtils.getInstance().createUUID());
        item.setRole(TransactionRoleEnum.START.getCode());
        item.setStatus(TransactionStatusEnum.PRE_COMMIT.getCode());
        item.setTxGroupId(groupId);

        //设置事务最大等待时间
        item.setWaitMaxTime(info.getWaitMaxTime());
        //设置创建时间
        item.setCreateDate(DateUtils.getCurrentDateTime());
        //设置执行类名称
        item.setTargetClass(info.getInvocation().getTargetClazz().getName());
        //设置执行类方法
        item.setTargetMethod(info.getInvocation().getMethod());
        //设置参数
        item.setArgs(info.getInvocation().getArgumentValues());
        items.add(item);

        txTransactionGroup.setItemList(items);
        return txTransactionGroup;
    }


}
