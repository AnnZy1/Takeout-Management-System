package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 菜品分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DishPageQueryDTO implements Serializable {

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
     * 菜品名称（模糊查询）
     */
    private String name;

    /**
     * 是否显示已删除菜品：0-不显示 1-显示
     */
    private Integer showDeleted;
}

