package com.blueskykong.lottor.samples.user.service.mapper;

import com.blueskykong.lottor.samples.user.domain.UserEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

/**
 * @author keets
 * @data 2018/8/2.
 */
public interface UserMapper {

    @Insert("INSERT INTO user(username,password,self_desc) VALUES(#{username},#{password},#{selfDesc})")
    @SelectKey(before = false, keyProperty = "id", resultType = Long.class, statement = {" SELECT LAST_INSERT_ID()  "})
    int saveUser(UserEntity userEntity);

    @Select("select * from user where id = #{id}")
    UserEntity getUserById(Long id);

}
