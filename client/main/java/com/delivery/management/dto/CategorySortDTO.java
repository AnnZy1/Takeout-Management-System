package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 分类排序DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class CategorySortDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排序列表
     */
    @NotEmpty(message = "排序列表不能为空")
    private List<SortItem> sortList;

    @Data
    public static class SortItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private Integer sort;
    }
}

