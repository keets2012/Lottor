package com.blueskykong.tm.common.holder;


import com.blueskykong.tm.common.bean.TransactionInvocation;
import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.bean.adapter.TransactionRecoverAdapter;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.exception.TransactionException;
import com.blueskykong.tm.common.serializer.ObjectSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionRecoverUtils {


    public static byte[] convert(TransactionRecover transactionRecover,
                                 ObjectSerializer objectSerializer) throws TransactionException {
        TransactionRecoverAdapter adapter = new TransactionRecoverAdapter();
        final List<TransactionMsg> transactionMsgs = transactionRecover.getTransactionMsgs();
        adapter.setGroupId(transactionRecover.getGroupId());
        adapter.setLastTime(transactionRecover.getLastTime());
        adapter.setRetriedCount(transactionRecover.getRetriedCount());
        adapter.setStatus(transactionRecover.getStatus());
        adapter.setTaskId(transactionRecover.getTaskId());
        adapter.setTransId(transactionRecover.getId());
        adapter.setCreateTime(transactionRecover.getCreateTime());
        if (Objects.nonNull(transactionMsgs)) {
            adapter.setContents(objectSerializer.serialize(transactionMsgs));
        }
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

        if (!Objects.nonNull(contents)) {
            return null;
        }
        final TransactionRecoverAdapter adapter =
                objectSerializer.deSerialize(contents, TransactionRecoverAdapter.class);

        List<TransactionMsg> transactionMsgs =
                objectSerializer.deSerialize(adapter.getContents(), ArrayList.class);

        transactionRecover.setLastTime(adapter.getLastTime());
        transactionRecover.setRetriedCount(adapter.getRetriedCount());
        transactionRecover.setCreateTime(adapter.getCreateTime());
        transactionRecover.setGroupId(adapter.getGroupId());
        transactionRecover.setId(adapter.getTransId());
        transactionRecover.setTaskId(adapter.getTaskId());
        transactionRecover.setStatus(adapter.getStatus());
        transactionRecover.setTransactionMsgs(transactionMsgs);
        transactionRecover.setVersion(adapter.getVersion());
        return transactionRecover;


    }
}
