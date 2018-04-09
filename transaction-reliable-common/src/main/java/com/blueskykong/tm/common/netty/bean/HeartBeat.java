package com.blueskykong.tm.common.netty.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author keets
 */
@Data
public class HeartBeat implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;

    private int action;

    private String key;

    private int result;

    private TxTransactionGroup txTransactionGroup;

}
