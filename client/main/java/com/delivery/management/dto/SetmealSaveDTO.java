package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 新增套餐DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class SetmealSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐名称（分店唯一）
     */
    @NotBlank(message = "套餐名称不能为空")
    private String name;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 套餐总价
     */
    @NotNull(message = "套餐总价不能为空")
    @PositiveOrZero(message = "套餐总价必须大于等于0")
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
    private Integer status = 1;

    /**
     * 分店ID
     */
    @NotNull(message = "分店ID不能为空")
    private Long branchId;

    /**
     * 关联菜品列表
     */
    @NotEmpty(message = "关联菜品列表不能为空")
    private List<SetmealDishDTO> setmealDishes;

    @Data
    public static class SetmealDishDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        @NotNull(message = "菜品ID不能为空")
        private Long dishId;
        @NotNull(message = "菜品份数不能为空")
        private Integer copies;
        @NotNull(message = "菜品单价不能为空")
        private BigDecimal price;
        private Integer sort = 0;
    }
}

