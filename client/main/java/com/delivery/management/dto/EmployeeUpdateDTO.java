package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改员工DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class EmployeeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    private Long id;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 手机号
     */
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
    private Long roleId;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 账号状态：1-正常 0-锁定
     */
    private Integer status;
}

