package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 套餐批量操作DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class SetmealBatchDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 套餐ID列表
     */
    @NotEmpty(message = "套餐ID列表不能为空")
    private List<Long> ids;

    /**
     * 操作类型：enable-起售 disable-停售
     */
    @NotNull(message = "操作类型不能为空")
    private String operation;
}

