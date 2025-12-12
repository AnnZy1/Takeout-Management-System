package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.Permission;
import com.delivery.management.entity.Role;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.PermissionMapper;
import com.delivery.management.mapper.RoleMapper;
import com.delivery.management.mapper.RolePermissionMapper;
import com.delivery.management.service.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 管理员角色名称
     */
    private static final String ADMIN_ROLE_NAME = "管理员";

    /**
     * 分页查询角色列表
     */
    @Override
    public PageInfo<RoleVO> pageQuery(RolePageQueryDTO queryDTO) {
        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();

        // 角色名称模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            queryWrapper.like(Role::getName, queryDTO.getName());
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Role::getCreateTime);

        // 执行查询
        List<Role> roleList = roleMapper.selectList(queryWrapper);

        // 转换为VO并查询权限列表
        List<RoleVO> voList = roleList.stream().map(role -> {
            RoleVO vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);

            // 查询权限ID列表
            LambdaQueryWrapper<com.delivery.management.entity.RolePermission> rpWrapper = 
                new LambdaQueryWrapper<>();
            rpWrapper.eq(com.delivery.management.entity.RolePermission::getRoleId, role.getId());
            List<com.delivery.management.entity.RolePermission> rpList = 
                rolePermissionMapper.selectList(rpWrapper);
            List<Long> permissionIds = rpList.stream()
                .map(com.delivery.management.entity.RolePermission::getPermissionId)
                .collect(Collectors.toList());
            vo.setPermissionIds(permissionIds);

            return vo;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageInfo<Role> rolePageInfo = new PageInfo<>(roleList);
        PageInfo<RoleVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(rolePageInfo, pageInfo);
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 新增角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoleVO save(RoleSaveDTO saveDTO) {
        // 检查角色名称是否已存在
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getName, saveDTO.getName());
        Role existRole = roleMapper.selectOne(queryWrapper);
        if (existRole != null) {
            throw new BusinessException("角色名称已存在");
        }

        // 验证权限是否存在
        List<Long> permissionIds = saveDTO.getPermissionIds();
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
            if (permissions.size() != permissionIds.size()) {
                throw new BusinessException("部分权限不存在");
            }
        }

        // 创建角色实体
        Role role = new Role();
        BeanUtils.copyProperties(saveDTO, role);

        // 保存角色
        roleMapper.insert(role);

        // 保存角色权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.insertBatch(role.getId(), permissionIds);
        }

        // 转换为VO返回
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        vo.setPermissionIds(permissionIds);

        log.info("新增角色成功：{}", role.getName());
        return vo;
    }

    /**
     * 修改角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleUpdateDTO updateDTO) {
        // 查询角色
        Role role = roleMapper.selectById(updateDTO.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否为管理员角色，管理员角色不可修改权限
        if (ADMIN_ROLE_NAME.equals(role.getName())) {
            throw new BusinessException("管理员角色不可修改权限");
        }

        // 如果修改角色名称，检查是否重复
        if (!updateDTO.getName().equals(role.getName())) {
            LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Role::getName, updateDTO.getName());
            Role existRole = roleMapper.selectOne(queryWrapper);
            if (existRole != null && !existRole.getId().equals(updateDTO.getId())) {
                throw new BusinessException("角色名称已存在");
            }
        }

        // 验证权限是否存在
        List<Long> permissionIds = updateDTO.getPermissionIds();
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<Permission> permissions = permissionMapper.selectBatchIds(permissionIds);
            if (permissions.size() != permissionIds.size()) {
                throw new BusinessException("部分权限不存在");
            }
        }

        // 更新角色信息
        Role updateRole = new Role();
        BeanUtils.copyProperties(updateDTO, updateRole);
        roleMapper.updateById(updateRole);

        // 删除原有权限关联
        rolePermissionMapper.deleteByRoleId(updateDTO.getId());

        // 保存新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            rolePermissionMapper.insertBatch(updateDTO.getId(), permissionIds);
        }

        log.info("修改角色成功，角色ID：{}", updateDTO.getId());
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 查询角色
        Role role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否为管理员角色，管理员角色不可删除
        if (ADMIN_ROLE_NAME.equals(role.getName())) {
            throw new BusinessException("管理员角色不可删除");
        }

        // 检查角色是否关联员工
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getRoleId, id);
        queryWrapper.eq(Employee::getIsDeleted, 0);
        Long count = employeeMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException("角色关联员工，不可删除");
        }

        // 软删除角色（这里使用逻辑删除，但Role表没有is_deleted字段，所以使用物理删除）
        // 根据数据库设计，role表没有is_deleted字段，所以这里使用物理删除
        // 删除角色权限关联（外键级联删除会自动处理）
        rolePermissionMapper.deleteByRoleId(id);
        roleMapper.deleteById(id);

        log.info("删除角色成功，角色ID：{}", id);
    }
}

