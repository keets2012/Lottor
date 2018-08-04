package com.blueskykong.lottor.samples.auth.service;

import com.blueskykong.lottor.samples.auth.domain.RoleEntity;
import com.blueskykong.lottor.samples.auth.domain.UserRole;

public interface RoleUserService {

    Boolean saveRoleUser(UserRole userRole);


    RoleEntity getRole(String name);
}
