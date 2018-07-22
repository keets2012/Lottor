package com.blueskykong.tm.core.interceptor;

import com.blueskykong.tm.common.enums.OperationEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public abstract class AbstractTxTransactionAspect {

    private TxTransactionInterceptor txTransactionInterceptor;

    public void setTxTransactionInterceptor(TxTransactionInterceptor txTransactionInterceptor) {
        this.txTransactionInterceptor = txTransactionInterceptor;
    }

    public Object interceptMethod(Object[] pjp, OperationEnum operationEnum) throws Throwable {
        return txTransactionInterceptor.interceptor(pjp, operationEnum);
    }


    /**
     * 该方法返回的值为springBean的优先级别
     *
     * @return 优先级
     */
    public abstract int getOrder();
}
