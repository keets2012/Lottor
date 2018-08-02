package com.blueskykong.lottor.common.enums;


public enum TransactionTypeEnum {

    /**
     * Root transaction type enum.
     */
    ROOT(1),
    /**
     * Branch transaction type enum.
     */
    BRANCH(2);

    /**
     * The Id.
     */
    int id;

    TransactionTypeEnum(int id) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Value of transaction type enum.
     *
     * @param id the id
     * @return the transaction type enum
     */
    public  static TransactionTypeEnum  valueOf(int id) {
        switch (id) {
            case 1:
                return ROOT;
            case 2:
                return BRANCH;
            default:
                return null;
        }
    }

}
