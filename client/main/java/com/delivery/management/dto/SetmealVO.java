package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 套餐VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class SetmealVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID
     */
    private Long id;

    /**
     * 套餐名称
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
     * 套餐总价
     */
    private BigDecimal price;

    /**
     * 套餐图片地址（OSS）
     */
    private String image;

    /**
     * 套餐描述
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
     * 分店名称
     */
    private String branchName;

    /**
     * 关联菜品列表
     */
    private List<SetmealDishVO> setmealDishes;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @Data
    public static class SetmealDishVO implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private Long dishId;
        private String name;
        private BigDecimal price;
        private Integer copies;
        private Integer sort;
    }
}

