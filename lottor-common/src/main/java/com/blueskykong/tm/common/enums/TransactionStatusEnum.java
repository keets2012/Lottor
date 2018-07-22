package com.blueskykong.tm.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum TransactionStatusEnum {

    /**
     * Rollback transaction status enum.
     */
    ROLLBACK(0, "回滚"),

    /**
     * Commit transaction status enum.
     */
    COMMIT(1, "已经提交"),


    /**
     * Begin transaction status enum.
     */
    BEGIN(2, "开始"),

    /**
     * Running transaction status enum.
     */
    RUNNING(3, "执行中"),

    /**
     * Failure transaction status enum.
     */
    FAILURE(4, "失败"),

    /**
     * Complete transaction status enum.
     */
    PRE_COMMIT(5, "预提交"),

    /**
     * Lock transaction status enum.
     */
    LOCK(6, "锁定");


    private int code;

    private String desc;

    TransactionStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static TransactionStatusEnum acquireByCode(int code) {
        Optional<TransactionStatusEnum> transactionStatusEnum =
                Arrays.stream(TransactionStatusEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        return transactionStatusEnum.orElse(TransactionStatusEnum.BEGIN);

    }

    public static String acquireDescByCode(int code) {
        return acquireByCode(code).getDesc();
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
