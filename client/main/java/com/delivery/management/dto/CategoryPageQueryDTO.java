package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 分类分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class CategoryPageQueryDTO implements Serializable {

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
     * 分类类型：1-菜品分类 2-套餐分类
     */
    private Integer type;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 分类状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 分类名称（模糊查询）
     */
    private String name;
}

