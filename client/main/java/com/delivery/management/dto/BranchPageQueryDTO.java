package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 分店分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class BranchPageQueryDTO implements Serializable {

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
     * 分店名称（模糊查询）
     */
    private String name;

    /**
     * 分店状态：1-启用 0-禁用
     */
    private Integer status;
}

