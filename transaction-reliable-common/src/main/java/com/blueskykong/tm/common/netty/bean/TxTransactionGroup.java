package com.blueskykong.tm.common.netty.bean;

import lombok.Data;

import java.io.Serializable;


@Data
public class TxTransactionGroup implements Serializable {


    private static final long serialVersionUID = -8826219545126676832L;

    /**
     * 事务组id
     */
    private String id;

    private int waitTime;

    private String source;

    private String target;

    /**
     * 事务状态
     */
    private int status;

    private TxTransactionItem item;

}
