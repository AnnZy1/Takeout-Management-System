package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("dish")
public class Dish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜品ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 基础单价
     */
    private BigDecimal price;

    /**
     * 规格配置（JSON）
     */
    private String specifications;

    /**
     * 菜品图片地址（OSS）
     */
    private String image;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 售卖状态：1-起售 0-停售
     */
    private Integer status;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 软删除标识：1-已删 0-未删
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 最后修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}

