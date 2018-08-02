package com.blueskykong.lottor.core.interceptor;

import com.blueskykong.lottor.common.enums.OperationEnum;

@FunctionalInterface
public interface TxTransactionInterceptor {

    void interceptor(Object[] args, OperationEnum operationEnum);
}
