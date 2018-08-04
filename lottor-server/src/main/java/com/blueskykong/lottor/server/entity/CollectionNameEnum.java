package com.blueskykong.lottor.server.entity;

public enum CollectionNameEnum {
    TxTransactionItem("TxTransactionItem", 0),
    TransactionMsg("TransactionMsg", 1),
    TxManagerInfo("TxManagerInfo", 2),
    BaseItem("BaseItem", 3);


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
