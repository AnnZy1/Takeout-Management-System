package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.common.utils.IpUtil;
import com.delivery.management.common.utils.JwtUtil;
import com.delivery.management.common.utils.LoginAttemptCache;
import com.delivery.management.common.utils.PasswordUtil;
import com.delivery.management.dto.EmployeeLoginDTO;
import com.delivery.management.dto.EmployeeLoginVO;
import com.delivery.management.dto.UnlockDTO;
import com.delivery.management.entity.Branch;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.LoginLog;
import com.delivery.management.entity.Role;
import com.delivery.management.mapper.BranchMapper;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.LoginLogMapper;
import com.delivery.management.mapper.PermissionMapper;
import com.delivery.management.mapper.RoleMapper;
import com.delivery.management.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    /**
     * 员工登录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        String ip = IpUtil.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // 查询员工
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee employee = employeeMapper.selectOne(queryWrapper);

        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(username);
        loginLog.setIp(ip);
        loginLog.setUserAgent(userAgent);
        loginLog.setLoginTime(LocalDateTime.now());

        // 检查账号是否被锁定（登录失败3次锁定15分钟）
        if (LoginAttemptCache.isLocked(username)) {
            long remainingMinutes = LoginAttemptCache.getRemainingLockTime(username);
            loginLog.setEmployeeId(employee != null ? employee.getId() : 0L);
            loginLog.setBranchId(employee != null ? employee.getBranchId() : null);
            loginLog.setStatus(0);
            loginLog.setErrorMsg("账号已被锁定，请" + remainingMinutes + "分钟后重试");
            loginLogMapper.insert(loginLog);
            throw new BusinessException("账号已被锁定，请" + remainingMinutes + "分钟后重试");
        }

        // 员工不存在或密码错误
        if (employee == null || !PasswordUtil.matches(password, employee.getPassword())) {
            // 记录登录失败
            boolean isLocked = LoginAttemptCache.recordFailure(username);
            
            loginLog.setEmployeeId(0L);
            loginLog.setStatus(0);
            if (isLocked) {
                loginLog.setErrorMsg("用户名或密码错误，账号已被锁定15分钟");
            } else {
                int remainingAttempts = LoginAttemptCache.getRemainingAttempts(username);
                loginLog.setErrorMsg("用户名或密码错误，剩余尝试次数：" + remainingAttempts);
            }
            loginLogMapper.insert(loginLog);
            
            if (isLocked) {
                throw new BusinessException("用户名或密码错误，账号已被锁定15分钟");
            } else {
                throw new BusinessException("用户名或密码错误");
            }
        }

        // 登录成功，清除失败记录
        LoginAttemptCache.clearAttempts(username);

        // 账号被锁定
        if (employee.getStatus() == 0) {
            loginLog.setEmployeeId(employee.getId());
            loginLog.setBranchId(employee.getBranchId());
            loginLog.setStatus(0);
            loginLog.setErrorMsg("账号已被锁定");
            loginLogMapper.insert(loginLog);
            throw new BusinessException("账号已被锁定，请联系管理员");
        }

        // 账号已删除
        if (employee.getIsDeleted() == 1) {
            loginLog.setEmployeeId(employee.getId());
            loginLog.setBranchId(employee.getBranchId());
            loginLog.setStatus(0);
            loginLog.setErrorMsg("账号已被删除");
            loginLogMapper.insert(loginLog);
            throw new BusinessException("账号已被删除");
        }

        // 查询角色信息
        Role role = roleMapper.selectById(employee.getRoleId());
        if (role == null || role.getStatus() == 0) {
            loginLog.setEmployeeId(employee.getId());
            loginLog.setBranchId(employee.getBranchId());
            loginLog.setStatus(0);
            loginLog.setErrorMsg("角色不存在或已禁用");
            loginLogMapper.insert(loginLog);
            throw new BusinessException("角色不存在或已禁用");
        }

        // 查询分店信息
        Branch branch = branchMapper.selectById(employee.getBranchId());
        if (branch == null || branch.getStatus() == 0 || branch.getIsDeleted() == 1) {
            loginLog.setEmployeeId(employee.getId());
            loginLog.setBranchId(employee.getBranchId());
            loginLog.setStatus(0);
            loginLog.setErrorMsg("分店不存在或已禁用");
            loginLogMapper.insert(loginLog);
            throw new BusinessException("分店不存在或已禁用");
        }

        // 查询按钮权限列表
        List<String> permissions = permissionMapper.selectPermissionByRoleId(employee.getRoleId());

        // 生成JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("empId", employee.getId());
        claims.put("branchId", employee.getBranchId());
        claims.put("username", employee.getUsername());
        String token = jwtUtil.generateToken(claims);

        // 记录登录日志（成功）
        loginLog.setEmployeeId(employee.getId());
        loginLog.setBranchId(employee.getBranchId());
        loginLog.setStatus(1);
        loginLogMapper.insert(loginLog);

        // 清除登录失败记录
        LoginAttemptCache.clearAttempts(username);

        // 构建返回结果
        EmployeeLoginVO loginVO = new EmployeeLoginVO();
        loginVO.setToken(token);

        EmployeeLoginVO.UserInfo userInfo = new EmployeeLoginVO.UserInfo();
        userInfo.setId(employee.getId());
        userInfo.setUsername(employee.getUsername());
        userInfo.setName(employee.getName());
        userInfo.setRoleId(role.getId());
        userInfo.setRoleName(role.getName());
        userInfo.setBranchId(branch.getId());
        userInfo.setBranchName(branch.getName());
        userInfo.setPermissions(permissions);

        loginVO.setUserInfo(userInfo);

        log.info("员工登录成功：{}", username);
        return loginVO;
    }

    /**
     * 员工退出
     */
    @Override
    public void logout() {
        // 前端清除Token，后端无需处理
        log.info("员工退出登录");
    }

    /**
     * 账号解锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlock(UnlockDTO unlockDTO) {
        Long employeeId = unlockDTO.getEmployeeId();
        
        // 查询员工
        Employee employee = employeeMapper.selectById(employeeId);
        if (employee == null || employee.getIsDeleted() == 1) {
            throw new BusinessException("员工不存在");
        }

        // 解锁账号
        employee.setStatus(1);
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.updateById(employee);

        log.info("账号解锁成功，员工ID：{}", employeeId);
    }
}

