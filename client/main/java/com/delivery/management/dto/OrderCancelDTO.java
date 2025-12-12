package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 取消订单DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class OrderCancelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long id;

    /**
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;
}

