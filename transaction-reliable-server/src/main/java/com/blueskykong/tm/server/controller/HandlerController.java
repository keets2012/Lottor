package com.blueskykong.tm.server.controller;

import com.blueskykong.tm.common.enums.NettyMessageActionEnum;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.blueskykong.tm.common.netty.bean.TxTransactionGroup;
import com.blueskykong.tm.server.netty.handler.NettyServerMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author keets
 * @data 2018/6/28.
 */
@RestController
public class HandlerController {

    @Autowired
    private NettyServerMessageHandler nettyServerMessageHandler;

    @GetMapping("/test")
    public String test() {
        ChannelHandlerContext ctx = nettyServerMessageHandler.getClientContext().get("1").get(0);
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {

            ctx.writeAndFlush(getTxStatus("123445"));
        }
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
