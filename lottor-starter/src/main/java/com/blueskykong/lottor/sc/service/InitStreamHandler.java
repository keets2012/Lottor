package com.blueskykong.lottor.sc.service;

import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.entity.TransactionMsgAdapter;
import com.blueskykong.lottor.common.exception.TransactionException;
import com.blueskykong.lottor.common.helper.TransactionMsgHelper;
import com.blueskykong.lottor.common.serializer.ObjectSerializer;
import com.blueskykong.lottor.core.service.ExternalNettyService;
import org.springframework.messaging.Message;

public abstract class InitStreamHandler {

    private ObjectSerializer objectSerializer;

    protected ExternalNettyService nettyService;

    public InitStreamHandler(ExternalNettyService nettyService, ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
        this.nettyService = nettyService;
    }

    protected TransactionMsg init(Message message) {
        TransactionMsgAdapter transactionMsgAdapter = (TransactionMsgAdapter) message.getPayload();
        try {
            return TransactionMsgHelper.convertTransactionMsgAdapter(transactionMsgAdapter, objectSerializer);
        } catch (TransactionException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

//    public abstract void process(TransactionMsg msg);
}
