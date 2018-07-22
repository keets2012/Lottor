
package com.blueskykong.tm.core.interceptor;

import com.blueskykong.tm.common.enums.OperationEnum;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 *
 */
@FunctionalInterface
public interface TxTransactionInterceptor {

    /**
     * 事务切面的拦截方法
     *
     * @param pjp spring事务切点
     * @return Object
     * @ 异常
     */
    Object interceptor(Object[] pjp, OperationEnum operationEnum);
}
