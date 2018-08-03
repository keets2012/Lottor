package com.blueskykong.lottor.samples.auth.rest;

import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.samples.auth.domain.UserRole;
import com.blueskykong.lottor.samples.auth.service.RoleUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @data 2018/3/19.
 */
@RestController
@RequestMapping("/auth")
public class RoleUserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUserController.class);

    @Autowired
    private RoleUserService roleUserService;

    @GetMapping("/")
    public String test() {
        LogUtil.info(LOGGER, () -> "！");
        roleUserService.saveRoleUser(new UserRole(0L, 1000L, 2000L));
//        userService.createUser();
        return "this is ok!";
    }
}
