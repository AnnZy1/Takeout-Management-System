package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 角色分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class RolePageQueryDTO implements Serializable {

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
     * 角色名称（模糊查询）
     */
    private String name;
}

