package com.blueskykong.lottor.samples.auth.service.mapper;

import com.blueskykong.lottor.samples.auth.domain.UserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author keets
 * @data 2018/8/2.
 */
public interface RoleUserMapper {

    @Insert("INSERT INTO user_role(user_id,role_id) VALUES(#{userId},#{roleId})")
    int saveRoleUser(UserRole userRole);

    @Select("select * from user_role where id = #{id}")
    UserRole getUserRoleById(Long id);

}
