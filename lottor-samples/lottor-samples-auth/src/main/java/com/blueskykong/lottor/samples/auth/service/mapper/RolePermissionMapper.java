package com.blueskykong.lottor.samples.auth.service.mapper;

import com.blueskykong.lottor.samples.auth.domain.RolePermission;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * @author keets
 * @data 2018/8/3.
 */
public interface RolePermissionMapper {

    @Insert("INSERT INTO role_permission(id,role_id,permission_id) VALUES(#{id},#{roleId},#{permissionId})")
    int savePermission(RolePermission rolePermission);

    @Select("select * from role_permission where id = #{id}")
    RolePermission getRolePermissionById(String id);

}
