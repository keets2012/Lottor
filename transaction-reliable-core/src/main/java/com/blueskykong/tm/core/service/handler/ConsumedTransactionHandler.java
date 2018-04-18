package com.blueskykong.tm.core.service.handler;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.ConsumedStatus;
import com.blueskykong.tm.common.holder.LogUtil;
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
public class ConsumedTransactionHandler implements TxTransactionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumedTransactionHandler.class);

    private final TxManagerMessageService txManagerMessageService;

    @Autowired
    public ConsumedTransactionHandler(TxManagerMessageService txManagerMessageService) {
        this.txManagerMessageService = txManagerMessageService;
    }

    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
        LogUtil.info(LOGGER, "tx-transaction confirm,  事务确认类：{}",
                () -> point.getTarget().getClass());
        try {
            //发起调用
            final Object res = point.proceed();

            int status;
            if (info.getInvocation().getArgumentValues().length != 2) {
                throw new IllegalArgumentException("wrong arguments for consuming!");
            }
            if ((Boolean) info.getInvocation().getArgumentValues()[1] == true) {
                status = ConsumedStatus.CONSUMED_SUCCESS.getStatus();
            } else {
                status = ConsumedStatus.CONSUMED_FAILURE.getStatus();
            }
            LogUtil.debug(LOGGER, "consume status: {}", () -> status);
            TransactionMsg transactionMsg = (TransactionMsg) info.getInvocation().getArgumentValues()[0];
            transactionMsg.setConsumed(status);
            //通知tm完成事务
            CompletableFuture.runAsync(() ->
                    txManagerMessageService
                            .asyncCompleteConsume(transactionMsg));

            LogUtil.info(LOGGER, "tx-transaction 消费完成, 事务发起类：{}",
                    () -> point.getTarget().getClass());
            return res;
        } catch (final Throwable throwable) {
            LogUtil.error(LOGGER, throwable::getLocalizedMessage);
            throw throwable;
        }
    }
}
