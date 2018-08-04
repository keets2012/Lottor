package com.blueskykong.lottor.samples.auth.rest;

import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.samples.auth.domain.Permission;
import com.blueskykong.lottor.samples.auth.domain.RoleEntity;
import com.blueskykong.lottor.samples.auth.domain.RolePermission;
import com.blueskykong.lottor.samples.auth.service.RoleUserService;
import com.blueskykong.lottor.samples.auth.service.mapper.PermissionMapper;
import com.blueskykong.lottor.samples.auth.service.mapper.RoleMapper;
import com.blueskykong.lottor.samples.auth.service.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @data 2018/3/19.
 */
@RestController
@RequestMapping("/auth")
public class RoleUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUserController.class);

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;


    @GetMapping("/permission")
    public String permission() {
        LogUtil.info(LOGGER, () -> "！");
        permissionMapper.savePermission(new Permission(UUID.randomUUID().toString(), "ALL", "所有权限"));

        permissionMapper.savePermission(new Permission(UUID.randomUUID().toString(), "READ_ONLY", "只读权限"));

        return "this is ok!";
    }

    @GetMapping("/role")
    public String role() {
        LogUtil.info(LOGGER, () -> "！");
        roleMapper.saveRole(new RoleEntity(UUID.randomUUID().toString(), "admin", new Timestamp(System.currentTimeMillis()), "管理员"));

        roleMapper.saveRole(new RoleEntity(UUID.randomUUID().toString(), "employee", new Timestamp(System.currentTimeMillis()), "普通员工"));

        return "this is ok!";
    }

    @GetMapping("/role-permission")
    public String rolePermission() {
        LogUtil.info(LOGGER, () -> "！");

        rolePermissionMapper.savePermission(new RolePermission(UUID.randomUUID().toString(), "a309b198-e5cc-4cc4-b08b-12c91ead3b1a", "4fcd9da1-faa7-49be-80e2-c47ea8c1af98"));

        rolePermissionMapper.savePermission(new RolePermission(UUID.randomUUID().toString(), "ce804be7-b2aa-486e-87cb-c39916f7683d", "a38e277a-0b04-4347-ae92-9b653bd0d15b"));

        return "this is ok!";
    }

}
