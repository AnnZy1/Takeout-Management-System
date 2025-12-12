package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 新增分类DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class CategorySaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称（同类型+分店唯一）
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * 分类类型：1-菜品分类 2-套餐分类
     */
    @NotNull(message = "分类类型不能为空")
    private Integer type;

    /**
     * 排序值
     */
    private Integer sort = 0;

    /**
     * 分店ID
     */
    @NotNull(message = "分店ID不能为空")
    private Long branchId;

    /**
     * 分类状态：1-启用 0-禁用
     */
    private Integer status = 1;
}

