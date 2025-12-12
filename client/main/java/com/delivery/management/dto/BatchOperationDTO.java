package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 批量操作DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class BatchOperationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID列表
     */
    @NotEmpty(message = "员工ID列表不能为空")
    private List<Long> ids;

    /**
     * 操作类型：lock-锁定 / unlock-启用 / delete-删除
     */
    @NotNull(message = "操作类型不能为空")
    private String operation;
}

