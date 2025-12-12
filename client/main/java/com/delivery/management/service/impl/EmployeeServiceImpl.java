package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.common.utils.PasswordUtil;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Branch;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.Role;
import com.delivery.management.mapper.BranchMapper;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.RoleMapper;
import com.delivery.management.service.EmployeeService;
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
 * 员工服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private BranchMapper branchMapper;

    /**
     * 默认密码
     */
    private static final String DEFAULT_PASSWORD = "123456";

    /**
     * 分页查询员工列表
     */
    @Override
    public PageInfo<EmployeeVO> pageQuery(EmployeePageQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        // 员工姓名模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            queryWrapper.like(Employee::getName, queryDTO.getName());
        }

        // 手机号模糊查询
        if (StringUtils.hasText(queryDTO.getPhone())) {
            queryWrapper.like(Employee::getPhone, queryDTO.getPhone());
        }

        // 角色ID
        if (queryDTO.getRoleId() != null) {
            queryWrapper.eq(Employee::getRoleId, queryDTO.getRoleId());
        }

        // 分店ID（数据范围控制）
        if (queryDTO.getBranchId() != null) {
            queryWrapper.eq(Employee::getBranchId, queryDTO.getBranchId());
        } else {
            // 如果不是管理员，自动过滤本店数据
            Long currentId = BaseContext.getCurrentId();
            if (currentId != null) {
                Employee currentEmployee = employeeMapper.selectById(currentId);
                if (currentEmployee != null) {
                    // 判断是否为管理员（这里简化处理，实际应该根据角色判断）
                    // 暂时先查询所有，后续可以在权限模块中完善
                }
            }
        }

        // 账号状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Employee::getStatus, queryDTO.getStatus());
        }

        // 过滤已删除的员工
        queryWrapper.eq(Employee::getIsDeleted, 0);

        // 按ID升序排列
        queryWrapper.orderByAsc(Employee::getId);

        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 执行查询
        List<Employee> employeeList = employeeMapper.selectList(queryWrapper);

        // 封装分页结果，使用 PageInfo 包装查询结果
        PageInfo<Employee> employeePageInfo = new PageInfo<>(employeeList);

        // 转换为VO
        List<EmployeeVO> voList = employeeList.stream().map(employee -> {
            EmployeeVO vo = new EmployeeVO();
            BeanUtils.copyProperties(employee, vo);

            // 查询角色名称
            if (employee.getRoleId() != null) {
                Role role = roleMapper.selectById(employee.getRoleId());
                if (role != null) {
                    vo.setRoleName(role.getName());
                }
            }

            // 查询分店名称
            if (employee.getBranchId() != null) {
                Branch branch = branchMapper.selectById(employee.getBranchId());
                if (branch != null) {
                    vo.setBranchName(branch.getName());
                }
            }

            return vo;
        }).collect(Collectors.toList());

        // 将 VO 列表设置到 PageInfo 中
        PageInfo<EmployeeVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(employeePageInfo, pageInfo);
        // 确保分页参数正确复制
        pageInfo.setTotal(employeePageInfo.getTotal());
        pageInfo.setPages(employeePageInfo.getPages());
        pageInfo.setPageNum(employeePageInfo.getPageNum());
        pageInfo.setPageSize(employeePageInfo.getPageSize());
        
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 新增员工
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeVO save(EmployeeSaveDTO saveDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<Employee> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(Employee::getUsername, saveDTO.getUsername());
        Employee existEmployee = employeeMapper.selectOne(usernameWrapper);
        if (existEmployee != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        LambdaQueryWrapper<Employee> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(Employee::getPhone, saveDTO.getPhone());
        existEmployee = employeeMapper.selectOne(phoneWrapper);
        if (existEmployee != null) {
            throw new BusinessException("手机号已存在");
        }

        // 验证角色是否存在
        Role role = roleMapper.selectById(saveDTO.getRoleId());
        if (role == null || role.getStatus() == 0) {
            throw new BusinessException("角色不存在或已禁用");
        }

        // 验证分店是否存在
        Branch branch = branchMapper.selectById(saveDTO.getBranchId());
        if (branch == null || branch.getStatus() == 0 || branch.getIsDeleted() == 1) {
            throw new BusinessException("分店不存在或已禁用");
        }

        // 创建员工实体
        Employee employee = new Employee();
        BeanUtils.copyProperties(saveDTO, employee);
        employee.setPassword(PasswordUtil.encode(DEFAULT_PASSWORD)); // 默认密码123456

        // 保存员工
        employeeMapper.insert(employee);

        // 转换为VO返回
        EmployeeVO vo = new EmployeeVO();
        BeanUtils.copyProperties(employee, vo);
        vo.setRoleName(role.getName());
        vo.setBranchName(branch.getName());

        log.info("新增员工成功：{}", employee.getUsername());
        return vo;
    }

    /**
     * 修改员工
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(EmployeeUpdateDTO updateDTO) {
        // 查询员工
        Employee employee = employeeMapper.selectById(updateDTO.getId());
        if (employee == null || employee.getIsDeleted() == 1) {
            throw new BusinessException("员工不存在");
        }

        // 如果修改手机号，检查是否重复
        if (StringUtils.hasText(updateDTO.getPhone()) && !updateDTO.getPhone().equals(employee.getPhone())) {
            LambdaQueryWrapper<Employee> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(Employee::getPhone, updateDTO.getPhone());
            Employee existEmployee = employeeMapper.selectOne(phoneWrapper);
            if (existEmployee != null && !existEmployee.getId().equals(updateDTO.getId())) {
                throw new BusinessException("手机号已存在");
            }
        }

        // 如果修改角色，验证角色是否存在
        if (updateDTO.getRoleId() != null && !updateDTO.getRoleId().equals(employee.getRoleId())) {
            Role role = roleMapper.selectById(updateDTO.getRoleId());
            if (role == null || role.getStatus() == 0) {
                throw new BusinessException("角色不存在或已禁用");
            }
        }

        // 如果修改分店，验证分店是否存在
        if (updateDTO.getBranchId() != null && !updateDTO.getBranchId().equals(employee.getBranchId())) {
            Branch branch = branchMapper.selectById(updateDTO.getBranchId());
            if (branch == null || branch.getStatus() == 0 || branch.getIsDeleted() == 1) {
                throw new BusinessException("分店不存在或已禁用");
            }
        }

        // 更新员工信息
        Employee updateEmployee = new Employee();
        BeanUtils.copyProperties(updateDTO, updateEmployee);
        employeeMapper.updateById(updateEmployee);

        log.info("修改员工成功，员工ID：{}", updateDTO.getId());
    }

    /**
     * 密码重置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPwdDTO resetPwdDTO) {
        Long employeeId = resetPwdDTO.getEmployeeId();

        // 查询员工
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null || employee.getIsDeleted() == 1) {
            throw new BusinessException("员工不存在");
        }

        // 重置密码为默认密码
        employee.setPassword(PasswordUtil.encode(DEFAULT_PASSWORD));
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateById(employee);

        log.info("密码重置成功，员工ID：{}", employeeId);
    }

    /**
     * 批量操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperation(BatchOperationDTO batchDTO) {
        List<Long> ids = batchDTO.getIds();
        String operation = batchDTO.getOperation();

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("员工ID列表不能为空");
        }

        // 查询员工列表
        List<Employee> employeeList = employeeMapper.selectBatchIds(ids);
        if (employeeList.size() != ids.size()) {
            throw new BusinessException("部分员工不存在");
        }

        // 检查是否包含管理员账号（不能批量删除管理员）
        if ("delete".equals(operation)) {
            // 这里需要根据实际业务判断，暂时先允许删除
            // 后续可以在权限模块中完善
        }

        // 执行批量操作
        switch (operation) {
            case "lock":
                // 批量锁定
                LambdaUpdateWrapper<Employee> lockWrapper = new LambdaUpdateWrapper<>();
                lockWrapper.in(Employee::getId, ids);
                lockWrapper.set(Employee::getStatus, 0);
                lockWrapper.set(Employee::getUpdateUser, BaseContext.getCurrentId());
                employeeMapper.update(null, lockWrapper);
                log.info("批量锁定{}名员工成功", ids.size());
                break;

            case "unlock":
                // 批量启用
                LambdaUpdateWrapper<Employee> unlockWrapper = new LambdaUpdateWrapper<>();
                unlockWrapper.in(Employee::getId, ids);
                unlockWrapper.set(Employee::getStatus, 1);
                unlockWrapper.set(Employee::getUpdateUser, BaseContext.getCurrentId());
                employeeMapper.update(null, unlockWrapper);
                log.info("批量启用{}名员工成功", ids.size());
                break;

            case "delete":
                // 批量删除（软删除）
                // 使用 deleteBatchIds 触发 MyBatis-Plus 的逻辑删除机制
                employeeMapper.deleteBatchIds(ids);
                log.info("批量删除{}名员工成功", ids.size());
                break;

            default:
                throw new BusinessException("不支持的操作类型：" + operation);
        }
    }
}

