package com.blueskykong.lottor.sc.interceptor;

import com.blueskykong.lottor.common.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.lottor.common.enums.OperationEnum;
import com.blueskykong.lottor.core.interceptor.TxTransactionInterceptor;
import com.blueskykong.lottor.core.service.AspectTransactionService;
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
