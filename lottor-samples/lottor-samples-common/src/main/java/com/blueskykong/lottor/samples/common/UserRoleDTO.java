package com.blueskykong.lottor.samples.common;

/**
 * @author keets
 * @data 2018/8/3.
 */
public class UserRoleDTO {

    private RoleEnum roleEnum;

    private Long userId;

    public RoleEnum getRoleEnum() {
        return roleEnum;
    }

    public void setRoleEnum(RoleEnum roleEnum) {
        this.roleEnum = roleEnum;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
