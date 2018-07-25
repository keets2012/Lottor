package com.blueskykong.tm.core.service;

import com.blueskykong.tm.common.enums.OperationEnum;
import org.aspectj.lang.ProceedingJoinPoint;

@FunctionalInterface
public interface AspectTransactionService {

    void invoke(String transactionGroupId, Object[] args, OperationEnum operationEnum) ;
}
