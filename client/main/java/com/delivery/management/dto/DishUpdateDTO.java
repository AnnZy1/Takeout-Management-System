package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 修改菜品DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DishUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品ID
     */
    @NotNull(message = "菜品ID不能为空")
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
     * 基础单价
     */
    @PositiveOrZero(message = "基础单价必须大于等于0")
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
     * 口味列表
     */
    private List<DishSaveDTO.FlavorDTO> flavors;
}

