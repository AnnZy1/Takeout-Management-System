package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID删除权限关联
     * 
     * @param roleId 角色ID
     */
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    void insertBatch(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}

