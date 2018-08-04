package com.blueskykong.lottor.samples.auth.service.mapper;

import com.blueskykong.lottor.samples.auth.domain.Permission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author keets
 * @data 2018/8/3.
 */
public interface PermissionMapper {

    @Insert("INSERT INTO permission(id,permission,description) VALUES(#{id},#{permission},#{description})")
    int savePermission(Permission permission);

    @Select("select * from permission where id = #{id}")
    Permission getPermissionById(String id);

}
