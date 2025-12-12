package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改订单状态DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class OrderUpdateStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long id;

    /**
     * 新状态
     */
    @NotNull(message = "订单状态不能为空")
    private Integer status;

    /**
     * 操作备注
     */
    private String remark;
}

