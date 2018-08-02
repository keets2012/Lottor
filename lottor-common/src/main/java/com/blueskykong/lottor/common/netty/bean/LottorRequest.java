package com.blueskykong.lottor.common.netty.bean;

import com.blueskykong.lottor.common.entity.TransactionMsg;
import lombok.Data;

import java.io.Serializable;

@Data
public class LottorRequest implements Serializable {

    private static final long serialVersionUID = 4183978848464761529L;

    private int action;

    private String key;

    private int result;

    private String metaInfo;

    private String serialProtocol;

    private TxTransactionGroup txTransactionGroup;

    /*
     * 注意，这里是为了适配，后面进行调整，有字段重复
     */
    private TransactionMsg transactionMsg;

}
