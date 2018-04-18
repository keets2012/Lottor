package com.blueskykong.tm.core.service.impl;

import com.blueskykong.tm.common.bean.TransactionInvocation;
import com.blueskykong.tm.common.bean.TxTransactionInfo;
import com.blueskykong.tm.common.enums.OperationEnum;
import com.blueskykong.tm.common.helper.SpringBeanUtils;
import com.blueskykong.tm.core.annotation.ReliableTransaction;
import com.blueskykong.tm.core.concurrent.threadlocal.CompensationLocal;
import com.blueskykong.tm.core.service.AspectTransactionService;
import com.blueskykong.tm.core.service.TxTransactionFactoryService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * @author keets
 */
public class AspectTransactionServiceImpl implements AspectTransactionService {

    private final TxTransactionFactoryService txTransactionFactoryService;

    @Autowired
    public AspectTransactionServiceImpl(TxTransactionFactoryService txTransactionFactoryService) {
        this.txTransactionFactoryService = txTransactionFactoryService;
    }

    @Override
    public Object invoke(String transactionGroupId, ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
        Method thisMethod = clazz.getMethod(method.getName(), method.getParameterTypes());
        //TODO add for auto-compensation
        final String compensationId = CompensationLocal.getInstance().getCompensationId();

        final ReliableTransaction txTransaction = method.getAnnotation(ReliableTransaction.class);

        final int waitMaxTime = txTransaction.waitMaxTime();

        final OperationEnum operation = txTransaction.value();

        TransactionInvocation invocation = new TransactionInvocation(clazz, thisMethod.getName(), args, method.getParameterTypes());
        TxTransactionInfo info = new TxTransactionInfo(invocation, transactionGroupId, compensationId, waitMaxTime, operation);
        final Class c = txTransactionFactoryService.factoryOf(info);
        final TxTransactionHandler txTransactionHandler =
                (TxTransactionHandler) SpringBeanUtils.getInstance().getBean(c);
        return txTransactionHandler.handler(point, info);
    }
}
