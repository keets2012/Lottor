package com.blueskykong.tm.common.enums;


/**
 * @author keets
 */
public enum OperationEnum {

    /**
     * OperationEnum start  TX.
     */
    TX_NEW(0),

    /**
     * OperationEnum complete TX.
     */
    TX_COMPLETE(1),

    /**
     * OperationEnum consumed tx.
     */
    TX_CONSUMED(2);

    private final int value;

    OperationEnum(int value) {
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public int getValue() {
        return this.value;
    }
}
