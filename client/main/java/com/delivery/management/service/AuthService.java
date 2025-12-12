package com.delivery.management.service;

import com.delivery.management.dto.EmployeeLoginDTO;
import com.delivery.management.dto.EmployeeLoginVO;
import com.delivery.management.dto.UnlockDTO;

/**
 * 认证服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface AuthService {

    /**
     * 员工登录
     * 
     * @param employeeLoginDTO 登录信息
     * @return 登录结果（包含Token和用户信息）
     */
    EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工退出
     */
    void logout();

    /**
     * 账号解锁
     * 
     * @param unlockDTO 解锁信息
     */
    void unlock(UnlockDTO unlockDTO);
}

