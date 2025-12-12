package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 修改套餐DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class SetmealUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID
     */
    @NotNull(message = "套餐ID不能为空")
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
     * 套餐总价
     */
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
    private Integer status;

    /**
     * 关联菜品列表
     */
    private List<SetmealSaveDTO.SetmealDishDTO> setmealDishes;
}

