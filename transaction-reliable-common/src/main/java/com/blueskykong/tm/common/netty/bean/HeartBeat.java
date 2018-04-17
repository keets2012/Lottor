package com.blueskykong.tm.common.netty.bean;

import com.blueskykong.tm.common.entity.TransactionMsg;
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

    /*
     * 注意，这里是为了适配，后面进行调整，有字段重复
     */
    private TransactionMsg transactionMsg;

}
