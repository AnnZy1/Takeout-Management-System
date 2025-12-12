package com.delivery.management.controller;

import com.delivery.management.common.result.Result;
import com.delivery.management.dto.EmployeeLoginDTO;
import com.delivery.management.dto.EmployeeLoginVO;
import com.delivery.management.dto.UnlockDTO;
import com.delivery.management.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    /**
     * 员工登录
     * 
     * @param employeeLoginDTO 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@Valid @RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO.getUsername());
        EmployeeLoginVO loginVO = authService.login(employeeLoginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 员工退出
     * 
     * @return 退出结果
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        log.info("员工退出登录");
        authService.logout();
        return Result.success("退出成功");
    }

    /**
     * 账号解锁
     * 
     * @param unlockDTO 解锁信息
     * @return 解锁结果
     */
    @PostMapping("/unlock")
    public Result<?> unlock(@Valid @RequestBody UnlockDTO unlockDTO) {
        log.info("账号解锁，员工ID：{}", unlockDTO.getEmployeeId());
        authService.unlock(unlockDTO);
        return Result.success("账号解锁成功");
    }
    
    /**
     * 强制解锁账号（临时接口）
     * 
     * @param username 用户名
     * @return 解锁结果
     */
    @PostMapping("/force-unlock/{username}")
    public Result<?> forceUnlock(@PathVariable String username) {
        log.info("强制解锁账号，用户名：{}", username);
        com.delivery.management.common.utils.LoginAttemptCache.clearAttempts(username);
        return Result.success("账号解锁成功");
    }
}

