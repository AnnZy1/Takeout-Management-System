package com.delivery.management.controller;

import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.RoleService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员角色控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<RoleVO>> list(RolePageQueryDTO queryDTO) {
        log.info("分页查询角色列表：{}", queryDTO);
        PageInfo<RoleVO> pageInfo = roleService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 新增角色
     * 
     * @param saveDTO 角色信息
     * @return 角色信息
     */
    @PostMapping("/save")
    public Result<RoleVO> save(@Valid @RequestBody RoleSaveDTO saveDTO) {
        log.info("新增角色：{}", saveDTO.getName());
        RoleVO roleVO = roleService.save(saveDTO);
        return Result.success("新增角色成功", roleVO);
    }

    /**
     * 修改角色
     * 
     * @param updateDTO 角色信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody RoleUpdateDTO updateDTO) {
        log.info("修改角色，角色ID：{}", updateDTO.getId());
        roleService.update(updateDTO);
        return Result.success("修改角色成功");
    }

    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除角色，角色ID：{}", id);
        roleService.delete(id);
        return Result.success("删除角色成功");
    }
}
