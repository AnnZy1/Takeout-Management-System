package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 员工登录DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class EmployeeLoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}

