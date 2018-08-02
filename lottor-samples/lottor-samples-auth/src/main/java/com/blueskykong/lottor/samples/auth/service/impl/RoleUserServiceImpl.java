package com.blueskykong.lottor.samples.auth.service.impl;

import com.blueskykong.lottor.samples.auth.domain.UserRole;
import com.blueskykong.lottor.samples.auth.service.RoleUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoleUserServiceImpl implements RoleUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleUserServiceImpl.class);


    @Override
    public Boolean saveRoleUser(UserRole userRole) {
        return null;
    }
}
