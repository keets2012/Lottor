package com.blueskykong.lottor.server.entity;

import lombok.Data;

import java.util.List;

@Data
public class TxManagerInfo {


    private static final long serialVersionUID = 1975118058422053078L;
    /**
     * socket ip
     */
    private String ip;
    /**
     * socket port
     */
    private int port;

    /**
     * max connection
     */
    private int maxConnection;

    /**
     * now connection
     */
    private int nowConnection;

    /**
     * transaction_wait_max_time
     */
    private int transactionWaitMaxTime;

    /**
     * redis_save_max_time
     */
    private int redisSaveMaxTime;

    /**
     * clusterInfoList
     */
    private List<String> clusterInfoList;

}
