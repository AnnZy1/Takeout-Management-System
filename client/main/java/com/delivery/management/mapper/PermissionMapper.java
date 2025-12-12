package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据角色ID查询权限列表（按钮权限）
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<String> selectPermissionByRoleId(@Param("roleId") Long roleId);
}

