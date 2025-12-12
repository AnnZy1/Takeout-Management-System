package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 密码重置DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class ResetPwdDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;
}

