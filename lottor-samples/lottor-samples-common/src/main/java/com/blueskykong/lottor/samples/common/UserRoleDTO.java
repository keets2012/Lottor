package com.blueskykong.lottor.samples.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author keets
 * @data 2018/8/3.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDTO {

    private RoleEnum roleEnum;

    private String userId;
}
