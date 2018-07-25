
package com.blueskykong.tm.core.interceptor;

import com.blueskykong.tm.common.enums.OperationEnum;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 *
 */
@FunctionalInterface
public interface TxTransactionInterceptor {

    void interceptor(Object[] args, OperationEnum operationEnum);
}
