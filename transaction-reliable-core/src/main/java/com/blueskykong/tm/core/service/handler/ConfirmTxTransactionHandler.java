package com.blueskykong.tm.core.service.handler;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.compensation.command.TxCompensationCommand;
import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionTaskLocal;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;

/**
 * @author keets
 */
public class ConfirmTxTransactionHandler implements TxTransactionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmTxTransactionHandler.class);

    private final TxManagerMessageService txManagerMessageService;

    private final TxCompensationCommand txCompensationCommand;

    @Autowired
    public ConfirmTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxCompensationCommand txCompensationCommand) {

        this.txManagerMessageService = txManagerMessageService;
        this.txCompensationCommand = txCompensationCommand;
    }

    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
        LogUtil.info(LOGGER, "tx-transaction confirm,  事务确认类：{}",
                () -> point.getTarget().getClass());

        final String groupId = TxTransactionLocal.getInstance().getTxGroupId();

        final String waitKey = TxTransactionTaskLocal.getInstance().getTxTaskId();

        try {
            //发起调用
            final Object res = point.proceed();

            int status;
            if ((Boolean) info.getInvocation().getArgumentValues()[0] == true) {
                status = TransactionStatusEnum.COMMIT.getCode();
                //确认本地的事务状态，当前措施为删除补偿信息
                txCompensationCommand.removeTxCompensation(groupId);
            } else {
                status = TransactionStatusEnum.ROLLBACK.getCode();
            }
            LogUtil.debug(LOGGER, "comfirm status: {}", () -> status);

            //通知tm完成事务
            CompletableFuture.runAsync(() ->
                    txManagerMessageService
                            .asyncCompleteCommit(groupId, waitKey,
                                    status, res));

            LogUtil.info(LOGGER, "tx-transaction end, 事务发起类：{}",
                    () -> point.getTarget().getClass());
            return res;
        } catch (final Throwable throwable) {
            //通知tm整个事务组失败，需要回滚标志状态
            //TODO ROLLABCK待优化
            txManagerMessageService.rollBackTxTransaction(groupId, waitKey);
            LogUtil.error(LOGGER, throwable::getLocalizedMessage);
            throw throwable;
        } finally {
            TxTransactionLocal.getInstance().removeTxGroupId();
            TxTransactionTaskLocal.getInstance().removeTxTaskId();
        }
    }
}
