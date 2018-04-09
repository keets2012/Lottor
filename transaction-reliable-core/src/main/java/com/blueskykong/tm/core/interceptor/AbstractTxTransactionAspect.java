package com.blueskykong.tm.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author keets
 */
@Aspect
public abstract class AbstractTxTransactionAspect {

    private TxTransactionInterceptor txTransactionInterceptor;

    public void setTxTransactionInterceptor(TxTransactionInterceptor txTransactionInterceptor) {
        this.txTransactionInterceptor = txTransactionInterceptor;
    }

    @Pointcut("@annotation(com.blueskykong.tm.core.annotation.ReliableTransaction)")
    public void transactionInterceptor() {

    }


    @Around("transactionInterceptor()")
    public Object interceptMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return txTransactionInterceptor.interceptor(proceedingJoinPoint);
    }


    /**
     * 该方法返回的值为springBean的优先级别
     *
     * @return 优先级
     */
    public abstract int getOrder();
}
