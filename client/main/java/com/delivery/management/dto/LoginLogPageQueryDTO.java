package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录日志分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class LoginLogPageQueryDTO implements Serializable {

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
     * 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String beginTime;

    /**
     * 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;

    /**
     * 登录状态：1-成功 0-失败
     */
    private Integer status;

    /**
     * 登录IP地址（模糊查询）
     */
    private String ip;
}

