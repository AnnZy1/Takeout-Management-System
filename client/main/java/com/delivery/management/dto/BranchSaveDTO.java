package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 新增分店DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class BranchSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分店名称（唯一）
     */
    @NotBlank(message = "分店名称不能为空")
    private String name;

    /**
     * 分店地址
     */
    @NotBlank(message = "分店地址不能为空")
    private String address;

    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    private String contactName;

    /**
     * 联系人手机号
     */
    @NotBlank(message = "联系人手机号不能为空")
    private String contactPhone;

    /**
     * 分店状态：1-启用 0-禁用
     */
    private Integer status = 1;
}

