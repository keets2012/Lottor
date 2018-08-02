package com.blueskykong.lottor.samples.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author keets
 * @data 2018/8/2.
 */
@Data
@AllArgsConstructor
public class RolePermission {

    private Long id;

    private Long roleId;

    private Long permissionId;
}
