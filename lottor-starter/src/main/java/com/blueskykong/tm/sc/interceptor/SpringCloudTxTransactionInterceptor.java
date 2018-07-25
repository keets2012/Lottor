package com.blueskykong.tm.sc.interceptor;

import com.blueskykong.tm.common.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.tm.common.enums.OperationEnum;
import com.blueskykong.tm.core.interceptor.TxTransactionInterceptor;
import com.blueskykong.tm.core.service.AspectTransactionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringCloudTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public SpringCloudTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }


    @Override
    public void interceptor(Object[] args, OperationEnum operationEnum) {

        String groupId = TxTransactionLocal.getInstance().getTxGroupId();

        aspectTransactionService.invoke(groupId, args, operationEnum);
    }

}
