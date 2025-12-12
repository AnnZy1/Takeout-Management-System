package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class RoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
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
     * 权限ID列表
     */
    private List<Long> permissionIds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

