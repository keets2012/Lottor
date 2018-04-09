package com.blueskykong.tm.core.service.handler;

import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

    /**
     * 处理消费结果
     *
     * @param point point 切点
     * @param info  信息
     * @return Object
     * @throws Throwable
     */
    @Override
    public Object handler(ProceedingJoinPoint point, TxTransactionInfo info) throws Throwable {
        LogUtil.info(LOGGER, "tx-transaction consume,  事务组消费类：{}",
                () -> point.getTarget().getClass());
        Object[] args = info.getInvocation().getArgumentValues();

        try {
            if (args.length == 2) {
                TransactionMsg msg = (TransactionMsg) args[0];
                Boolean success = (Boolean) args[1];
                txManagerMessageService.commitActorTxTransaction(msg.getGroupId(), msg.getSubTaskId(), success);
            }
            //发起调用
            final Object res = point.proceed();

            LogUtil.info(LOGGER, "tx-transaction consumed, 消费类：{}",
                    () -> point.getTarget().getClass());

            return res;
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }
}
