package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * 角色服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface RoleService {

    /**
     * 分页查询角色列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<RoleVO> pageQuery(RolePageQueryDTO queryDTO);

    /**
     * 新增角色
     * 
     * @param saveDTO 角色信息
     * @return 角色信息
     */
    RoleVO save(RoleSaveDTO saveDTO);

    /**
     * 修改角色
     * 
     * @param updateDTO 角色信息
     */
    void update(RoleUpdateDTO updateDTO);

    /**
     * 删除角色
     * 
     * @param id 角色ID
     */
    void delete(Long id);
}

