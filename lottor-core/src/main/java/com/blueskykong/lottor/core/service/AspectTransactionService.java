package com.blueskykong.lottor.core.service;

import com.blueskykong.lottor.common.enums.OperationEnum;

@FunctionalInterface
public interface AspectTransactionService {

    void invoke(String transactionGroupId, Object[] args, OperationEnum operationEnum) ;
}
