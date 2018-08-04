package com.blueskykong.lottor.samples.user.domain;

/**
 * @author keets
 * @data 2018/8/4.
 */
public enum StateEnum {

    PRODUCE_SUCCESS(0, "PRODUCE_SUCCESS"),

    PRODUCE_FAIL(1, "PRODUCE_SUCCESS"),

    CONSUME_FAIL(2, "PRODUCE_SUCCESS");


    private int index;

    private String desc;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    StateEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }
}
