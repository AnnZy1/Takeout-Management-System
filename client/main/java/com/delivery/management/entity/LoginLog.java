package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 员工ID（未登录成功时为0）
     */
    private Long employeeId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 所属分店ID
     */
    private Long branchId;

    /**
     * 登录IP地址
     */
    private String ip;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录状态：1-成功 0-失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 浏览器/设备信息
     */
    private String userAgent;
}

