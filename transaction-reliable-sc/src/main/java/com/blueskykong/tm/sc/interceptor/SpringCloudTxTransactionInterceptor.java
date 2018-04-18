package com.blueskykong.tm.sc.interceptor;

import com.blueskykong.tm.core.concurrent.threadlocal.TxTransactionLocal;
import com.blueskykong.tm.core.interceptor.TxTransactionInterceptor;
import com.blueskykong.tm.core.service.AspectTransactionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author keets
 */
public class SpringCloudTxTransactionInterceptor implements TxTransactionInterceptor {

    private final AspectTransactionService aspectTransactionService;

    @Autowired
    public SpringCloudTxTransactionInterceptor(AspectTransactionService aspectTransactionService) {
        this.aspectTransactionService = aspectTransactionService;
    }


    @Override
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {

        String groupId = TxTransactionLocal.getInstance().getTxGroupId();

        return aspectTransactionService.invoke(groupId, pjp);
    }

}
