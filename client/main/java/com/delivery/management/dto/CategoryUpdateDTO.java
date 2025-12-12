package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改分类DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class CategoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 分类状态：1-启用 0-禁用
     */
    private Integer status;
}

