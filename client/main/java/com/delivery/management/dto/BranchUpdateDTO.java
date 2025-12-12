package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改分店DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class BranchUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分店ID
     */
    @NotNull(message = "分店ID不能为空")
    private Long id;

    /**
     * 分店名称
     */
    private String name;

    /**
     * 分店地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 分店状态：1-启用 0-禁用
     */
    private Integer status;
}

