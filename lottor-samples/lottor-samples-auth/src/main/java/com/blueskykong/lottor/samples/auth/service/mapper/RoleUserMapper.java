package com.blueskykong.lottor.samples.auth.service.mapper;

import com.blueskykong.lottor.samples.auth.domain.UserRole;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author keets
 * @data 2018/8/2.
 */
public interface RoleUserMapper {

    @Insert("INSERT INTO user(id,username,password,self_desc) VALUES(#{id}, #{username},#{password},#{selfDesc})")
    int saveUser(UserRole userRole);

    @Select("select * from user where id = #{id}")
    UserRole getUserById(Long id);

}
