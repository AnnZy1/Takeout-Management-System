package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类类型：1-菜品分类 2-套餐分类
     */
    private Integer type;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 分类状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 分店名称
     */
    private String branchName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

