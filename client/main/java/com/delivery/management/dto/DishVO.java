package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜品VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DishVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品ID
     */
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 基础单价
     */
    private BigDecimal price;

    /**
     * 规格配置（JSON字符串）
     */
    private String specifications;

    /**
     * 菜品图片地址（OSS）
     */
    private String image;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 售卖状态：1-起售 0-停售
     */
    private Integer status;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 口味列表
     */
    private List<FlavorVO> flavors;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Data
    public static class FlavorVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private String value;
    }
}

