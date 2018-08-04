package com.blueskykong.lottor.samples.user.service.mapper;

import com.blueskykong.lottor.samples.user.domain.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author keets
 * @data 2018/8/2.
 */
public interface UserMapper {

    @Insert("INSERT INTO user(id,username,password,self_desc) VALUES(#{id},#{username},#{password},#{selfDesc})")
    int saveUser(UserEntity userEntity);

    @Insert("INSERT INTO user(id,username,password,self_desc) VALUES(#{id},#{username})")
    int saveUserFailure(UserEntity userEntity);

    @Select("select * from user where id = #{id}")
    UserEntity getUserById(Long id);

}
