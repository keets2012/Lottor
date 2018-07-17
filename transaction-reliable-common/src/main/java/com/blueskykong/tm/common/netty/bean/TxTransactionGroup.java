package com.blueskykong.tm.common.netty.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class TxTransactionGroup implements Serializable {


    private static final long serialVersionUID = -8826219545126676832L;

    /**
     * 事务组id
     */
    private String id;

    /**
     * 事务等待时间
     */
    private int waitTime;

    private String source;

    private String target;
    /**
     * 事务状态
     */
    private int status;

    private List<TxTransactionItem> itemList;

    private TxTransactionItem item;

}
