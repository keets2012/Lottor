package com.blueskykong.tm.common.holder;


import com.blueskykong.tm.common.bean.TransactionInvocation;
import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.bean.adapter.TransactionRecoverAdapter;
import com.blueskykong.tm.common.exception.TransactionException;
import com.blueskykong.tm.common.serializer.ObjectSerializer;

public class TransactionRecoverUtils {


    public static byte[] convert(TransactionRecover transactionRecover,
                                 ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecoverAdapter adapter = new TransactionRecoverAdapter();
        final TransactionInvocation transactionInvocation = transactionRecover.getTransactionInvocation();
        adapter.setGroupId(transactionRecover.getGroupId());
        adapter.setLastTime(transactionRecover.getLastTime());
        adapter.setRetriedCount(transactionRecover.getRetriedCount());
        adapter.setStatus(transactionRecover.getStatus());
        adapter.setTaskId(transactionRecover.getTaskId());
        adapter.setTransId(transactionRecover.getId());
        adapter.setTargetClass(transactionInvocation.getTargetClazz().getName());
        adapter.setTargetMethod(transactionInvocation.getMethod());
        adapter.setCreateTime(transactionRecover.getCreateTime());
        adapter.setContents(objectSerializer.serialize(transactionInvocation));
        adapter.setVersion(transactionRecover.getVersion());
        return objectSerializer.serialize(adapter);
    }



    public static TransactionRecover transformVO(byte[] contents,
                                                   ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecover transactionRecover = new TransactionRecover();

        final TransactionRecoverAdapter adapter =
                objectSerializer.deSerialize(contents, TransactionRecoverAdapter.class);


        transactionRecover.setLastTime(adapter.getLastTime());
        transactionRecover.setRetriedCount(adapter.getRetriedCount());
        transactionRecover.setCreateTime(adapter.getCreateTime());
        transactionRecover.setGroupId(adapter.getGroupId());
        transactionRecover.setId(adapter.getTransId());
        transactionRecover.setTaskId(adapter.getTaskId());
        transactionRecover.setStatus(adapter.getStatus());

        transactionRecover.setVersion(adapter.getVersion());
        return transactionRecover;


    }



    public static TransactionRecover transformBean(byte[] contents,
                                                   ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecover transactionRecover = new TransactionRecover();

        final TransactionRecoverAdapter adapter =
                objectSerializer.deSerialize(contents, TransactionRecoverAdapter.class);

        TransactionInvocation transactionInvocation =
                objectSerializer.deSerialize(adapter.getContents(), TransactionInvocation.class);

        transactionRecover.setLastTime(adapter.getLastTime());
        transactionRecover.setRetriedCount(adapter.getRetriedCount());
        transactionRecover.setCreateTime(adapter.getCreateTime());
        transactionRecover.setGroupId(adapter.getGroupId());
        transactionRecover.setId(adapter.getTransId());
        transactionRecover.setTaskId(adapter.getTaskId());
        transactionRecover.setStatus(adapter.getStatus());
        transactionRecover.setTransactionInvocation(transactionInvocation);
        transactionRecover.setVersion(adapter.getVersion());
        return transactionRecover;


    }
}
