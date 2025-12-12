package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

/**
 * 权限实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(type = IdType.AUTO)
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
     * 路由路径/按钮标识
     */
    private String path;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 权限状态：1-启用 0-禁用
     */
    private Integer status;
}

