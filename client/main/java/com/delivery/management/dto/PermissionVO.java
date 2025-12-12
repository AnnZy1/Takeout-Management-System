package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 权限VO
 */
@Data
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限类型：1-菜单权限 2-按钮权限
     */
    private Integer type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 子权限列表
     */
    private List<PermissionVO> children;
}
