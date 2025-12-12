package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.dto.LoginLogPageQueryDTO;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.LoginLog;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.LoginLogMapper;
import com.delivery.management.service.LogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 日志服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 分页查询登录日志
     */
    @Override
    public PageInfo<?> pageQueryLoginLog(LoginLogPageQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        
        // 员工姓名模糊查询（需要关联员工表）
        if (queryDTO.getName() != null && !queryDTO.getName().trim().isEmpty()) {
            // 先根据姓名查询员工ID列表
            LambdaQueryWrapper<Employee> empQueryWrapper = new LambdaQueryWrapper<>();
            empQueryWrapper.like(Employee::getName, queryDTO.getName());
            List<Employee> employees = employeeMapper.selectList(empQueryWrapper);
            if (!employees.isEmpty()) {
                List<Long> employeeIds = employees.stream()
                        .map(Employee::getId)
                        .collect(Collectors.toList());
                queryWrapper.in(LoginLog::getEmployeeId, employeeIds);
            } else {
                // 如果没有匹配的员工，返回空结果
                queryWrapper.eq(LoginLog::getEmployeeId, -1L);
            }
        }

        // 时间范围查询
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (queryDTO.getBeginTime() != null && !queryDTO.getBeginTime().trim().isEmpty()) {
            LocalDateTime beginTime = LocalDateTime.parse(queryDTO.getBeginTime(), formatter);
            queryWrapper.ge(LoginLog::getLoginTime, beginTime);
        }
        if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().trim().isEmpty()) {
            LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);
            queryWrapper.le(LoginLog::getLoginTime, endTime);
        }

        // 登录状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(LoginLog::getStatus, queryDTO.getStatus());
        }

        // IP地址模糊查询
        if (queryDTO.getIp() != null && !queryDTO.getIp().trim().isEmpty()) {
            queryWrapper.like(LoginLog::getIp, queryDTO.getIp());
        }

        // 数据范围控制：分店员工仅查看本店员工登录日志
        Long currentId = BaseContext.getCurrentId();
        if (currentId != null) {
            Employee currentEmployee = employeeMapper.selectById(currentId);
            if (currentEmployee != null) {
                // 判断是否为管理员（这里简化处理，实际应该根据角色判断）
                // 如果不是管理员，只查看本店员工的登录日志
                // 注意：这里需要根据实际业务逻辑判断是否为管理员
                // 暂时先查询所有，后续可以在权限模块中完善
            }
        }

        // 按登录时间倒序排列
        queryWrapper.orderByDesc(LoginLog::getLoginTime);

        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 执行查询
        List<LoginLog> loginLogList = loginLogMapper.selectList(queryWrapper);

        // 封装分页结果
        PageInfo<LoginLog> pageInfo = new PageInfo<>(loginLogList);
        return pageInfo;
    }
}

