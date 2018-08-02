
package com.blueskykong.lottor.common.enums;

public enum CompensationActionEnum {


    SAVE(0, "保存"),

    DELETE(1, "删除"),

    UPDATE(2, "更新"),

    COMPENSATE(3, "补偿"),

    VIEW(4, "查看");

    private int code;

    private String desc;

    CompensationActionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
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
