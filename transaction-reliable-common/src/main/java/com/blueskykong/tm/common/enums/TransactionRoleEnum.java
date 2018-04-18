package com.blueskykong.tm.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum TransactionRoleEnum {


    /**
     * Start transaction role enum.
     */
    START(0, "发起者"),


    /**
     * Actor transaction role enum.
     */
    ACTOR(1, "参与者"),

    /**
     * 事务组
     */
    GROUP(2,"事务组")
    ;


    private int code;

    private String desc;

    TransactionRoleEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TransactionRoleEnum acquireByCode(int code) {
        Optional<TransactionRoleEnum> roleEnum =
                Arrays.stream(TransactionRoleEnum.values())
                        .filter(v -> Objects.equals(v.getCode(), code))
                        .findFirst();
        return roleEnum.orElse(TransactionRoleEnum.START);

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
