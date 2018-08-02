package com.blueskykong.lottor.server.config;

import com.blueskykong.lottor.common.netty.bean.TxTransactionItem;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.List;


@Data
public class ChannelSender {

    /**
     * 模块netty 长连接渠道
     */
    private Channel channel;


    /**
     * txManger的域名信息
     */
    private String tmDomain;

    /**
     * 事务item
     */
    private List<TxTransactionItem> itemList;

}
