package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 套餐分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class SetmealPageQueryDTO implements Serializable {

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
     * 分类ID
     */
    private Long categoryId;

    /**
     * 售卖状态：1-起售 0-停售
     */
    private Integer status;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 套餐名称（模糊查询）
     */
    private String name;
}

