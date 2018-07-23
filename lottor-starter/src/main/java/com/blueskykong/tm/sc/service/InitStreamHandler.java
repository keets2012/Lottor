package com.blueskykong.tm.sc.service;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TransactionMsgAdapter;
import com.blueskykong.tm.common.exception.TransactionException;
import com.blueskykong.tm.common.helper.TransactionMsgHelper;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.service.ExternalNettyService;
import org.springframework.messaging.Message;

/**
 * @data 2018/6/27.
 */
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
