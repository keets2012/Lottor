package com.blueskykong.lottor.server.controller;

import com.blueskykong.lottor.common.enums.NettyMessageActionEnum;
import com.blueskykong.lottor.common.netty.bean.LottorRequest;
import com.blueskykong.lottor.common.netty.bean.TxTransactionGroup;
import com.blueskykong.lottor.server.netty.handler.NettyServerMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HandlerController {

    @Autowired
    private NettyServerMessageHandler nettyServerMessageHandler;

    @GetMapping("/test")
    public String test() {

        return "ok";
    }


    public LottorRequest getTxStatus(String key) {
        LottorRequest request = new LottorRequest();
        request.setAction(NettyMessageActionEnum.GET_TRANSACTION_GROUP_STATUS
                .getCode());
        TxTransactionGroup txTransactionGroup = new TxTransactionGroup();
        txTransactionGroup.setId(key);
        request.setTxTransactionGroup(txTransactionGroup);
        request.setKey(key);
        return request;
    }
}
