package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

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

    /**
     * 软删除标识：1-已删 0-未删
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 最后修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}

