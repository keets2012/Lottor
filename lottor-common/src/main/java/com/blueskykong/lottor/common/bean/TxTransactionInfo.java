package com.blueskykong.lottor.common.bean;

import com.blueskykong.lottor.common.enums.OperationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class TxTransactionInfo {


    @Getter
    private TransactionInvocation invocation;

    @Getter
    private Object[] args;

    @Getter
    private String txGroupId;

    @Getter
    private String compensationId;


    @Getter
    private int waitMaxTime = 60;


    @Getter
    private OperationEnum operationEnum;


}
