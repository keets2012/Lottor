package com.blueskykong.tm.common.bean;

import com.blueskykong.tm.common.enums.OperationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class TxTransactionInfo {


    @Getter
    private TransactionInvocation invocation;


    @Getter
    private String txGroupId;

    @Getter
    private String compensationId;


    @Getter
    private int waitMaxTime = 60;


    @Getter
    private OperationEnum operationEnum;


}
