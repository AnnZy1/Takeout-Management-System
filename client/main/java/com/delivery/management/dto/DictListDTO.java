package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 字典列表查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DictListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    private String dictType;
}

