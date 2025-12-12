package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐菜品关系实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("setmeal_dish")
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 套餐ID
     */
    private Long setmealId;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 菜品名称（冗余）
     */
    private String name;

    /**
     * 菜品单价（冗余）
     */
    private BigDecimal price;

    /**
     * 菜品份数
     */
    private Integer copies;

    /**
     * 排序值
     */
    private Integer sort;
}

