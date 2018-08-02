package com.blueskykong.lottor.server.entity;

/**
 * @data 2018/4/3.
 */
public enum CollectionNameEnum {
    TxTransactionItem("TxTransactionItem", 0),
    TransactionMsg("TxTransactionItem", 1),
    TxManagerInfo("TxTransactionItem", 2),
    BaseItem("TxTransactionItem", 3);


    private String name;
    private int type;

    CollectionNameEnum(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
