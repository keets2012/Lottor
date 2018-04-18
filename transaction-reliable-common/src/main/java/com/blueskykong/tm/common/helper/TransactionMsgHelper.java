package com.blueskykong.tm.common.helper;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.entity.TransactionMsgAdapter;
import com.blueskykong.tm.common.exception.TransactionException;
import com.blueskykong.tm.common.serializer.ObjectSerializer;

/**
 * @author keets
 * @data 2018/6/27.
 */
public class TransactionMsgHelper {
    public static TransactionMsgAdapter convertTransactionMsg(TransactionMsg msg) {
        return new TransactionMsgAdapter.Builder()
                .setConsumed(msg.getConsumed())
                .setCreateTime(msg.getCreateTime())
                .setGroupId(msg.getGroupId())
                .setMethod(msg.getMethod())
                .setSource(msg.getSource())
                .setUpdateTime(msg.getUpdateTime())
                .setTarget(msg.getTarget())
                .setSubTaskId(msg.getSubTaskId())
                .setMessage(msg.getMessage())
                .setArgsType(msg.getArgs().getClass().getName())
                .build();
    }

    public static TransactionMsg convertTransactionMsgAdapter(TransactionMsgAdapter msg, ObjectSerializer objectSerializer) throws TransactionException, ClassNotFoundException {
        Class clzz = Class.forName(msg.getArgsType());
        return new TransactionMsg.Builder()
                .setConsumed(msg.getConsumed())
                .setCreateTime(msg.getCreateTime())
                .setGroupId(msg.getGroupId())
                .setMethod(msg.getMethod())
                .setSource(msg.getSource())
                .setUpdateTime(msg.getUpdateTime())
                .setTarget(msg.getTarget())
                .setSubTaskId(msg.getSubTaskId())
                .setMessage(msg.getMessage())
                .setArgs(objectSerializer.deSerialize(msg.getArgs(), clzz))
                .build();
    }
}
