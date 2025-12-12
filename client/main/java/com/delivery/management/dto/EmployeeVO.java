package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class EmployeeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     */
    private String sex;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 分店名称
     */
    private String branchName;

    /**
     * 账号状态：1-正常 0-锁定
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

