package com.blueskykong.tm.common.enums;

/**
 * @author keets
 */
public enum ConsumedStatus {

    UNCONSUMED("未消费", 0),

    CONSUMED_FAILURE("消费失败", 1),

    CONSUMED_SUCCESS("消费成功", 2);

    private String desc;

    private int status;

    private ConsumedStatus(String desc, int status) {
        this.desc = desc;
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public int getStatus() {
        return status;
    }
}