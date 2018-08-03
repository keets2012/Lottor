package com.blueskykong.lottor.samples.auth.service.mapper;

import com.blueskykong.lottor.samples.auth.domain.RoleEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author keets
 * @data 2018/8/3.
 */
public interface RoleMapper {

    @Insert("INSERT INTO role(name,update_time,description) VALUES(#{name},#{updateTime},#{description})")
    int saveRole(RoleEntity roleEntity);

    @Select("select * from role where id = #{id}")
    RoleEntity getRoleById(Long id);

}
