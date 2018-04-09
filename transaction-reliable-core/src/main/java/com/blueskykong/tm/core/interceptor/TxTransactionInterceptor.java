
package com.blueskykong.tm.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author keets
 */
@FunctionalInterface
public interface TxTransactionInterceptor {

    /**
     * 事务切面的拦截方法
     *
     * @param pjp spring事务切点
     * @return Object
     * @throws Throwable 异常
     */
    Object interceptor(ProceedingJoinPoint pjp) throws Throwable;
}
