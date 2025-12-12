package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 员工分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class EmployeePageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 员工姓名（模糊查询）
     */
    private String name;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

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

