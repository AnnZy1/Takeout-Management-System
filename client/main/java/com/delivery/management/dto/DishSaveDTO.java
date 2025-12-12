package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 新增菜品DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DishSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品名称（分店唯一）
     */
    @NotBlank(message = "菜品名称不能为空")
    private String name;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 基础单价
     */
    @NotNull(message = "基础单价不能为空")
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
    private Integer status = 1;

    /**
     * 分店ID
     */
    @NotNull(message = "分店ID不能为空")
    private Long branchId;

    /**
     * 口味列表
     */
    private List<FlavorDTO> flavors;

    @Data
    public static class FlavorDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private String value;
    }
}

