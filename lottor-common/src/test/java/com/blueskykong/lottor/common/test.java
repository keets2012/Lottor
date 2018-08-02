package com.blueskykong.lottor.common;

import com.blueskykong.lottor.common.entity.TransactionMsg;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        List<TransactionMsg> transactionMsgs = new ArrayList<>();
        TransactionMsg transactionMsg = new TransactionMsg();
        transactionMsg.setUpdateTime(System.currentTimeMillis());
        transactionMsgs.add(transactionMsg);
        TransactionMsg transactionMsg2 = new TransactionMsg();
        transactionMsg2.setTarget("123");
        transactionMsgs.add(transactionMsg2);
        transactionMsgs.stream().map(msg -> {
            msg.setSource("123");
            return msg;
        }).forEach(msg -> System.out.println(msg.getTarget()));
    }

}
