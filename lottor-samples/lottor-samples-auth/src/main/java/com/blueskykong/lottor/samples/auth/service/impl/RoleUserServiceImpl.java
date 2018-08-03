package com.blueskykong.lottor.samples.auth.service.impl;

import com.blueskykong.lottor.samples.auth.domain.UserRole;
import com.blueskykong.lottor.samples.auth.service.RoleUserService;
import com.blueskykong.lottor.samples.auth.service.mapper.RoleUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleUserServiceImpl implements RoleUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUserServiceImpl.class);

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Override
    public Boolean saveRoleUser(UserRole userRole) {
        int result = roleUserMapper.saveRoleUser(userRole);
        return result > 0;
    }
}
