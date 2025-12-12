package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 修改角色DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class RoleUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 关联的权限ID列表
     */
    @NotEmpty(message = "权限列表不能为空")
    private List<Long> permissionIds;
}

