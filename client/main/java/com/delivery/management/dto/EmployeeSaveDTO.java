package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 新增员工DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class EmployeeSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名（唯一）
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 员工姓名
     */
    @NotBlank(message = "员工姓名不能为空")
    private String name;

    /**
     * 手机号（唯一）
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 性别：男/女/未知
     */
    private String sex;

    /**
     * 身份证号（加密存储）
     */
    private String idNumber;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 分店ID
     */
    @NotNull(message = "分店ID不能为空")
    private Long branchId;

    /**
     * 账号状态：1-正常 0-锁定
     */
    private Integer status = 1;
}

