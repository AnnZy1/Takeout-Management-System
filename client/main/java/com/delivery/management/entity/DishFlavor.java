package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜品口味实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("dish_flavor")
public class DishFlavor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 口味ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 口味名称
     */
    private String name;

    /**
     * 口味值
     */
    private String value;

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
}

