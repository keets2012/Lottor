package com.blueskykong.tm.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author keets
 * @data 2018/5/3.
 */
@Data
public class TxTransactionMsg implements Serializable {

    private List<TransactionMsg> msgs;

    /**
     * 用于消息的追溯
     */
    private String groupId;
}
