package com.blueskykong.lottor.samples.common;

/**
 * @author keets
 * @data 2018/8/3.
 */
public enum RoleEnum {

    ADMIN("admin"),
    EMPLOYEE("employee");

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    RoleEnum(String name) {
        this.name = name;
    }
}
