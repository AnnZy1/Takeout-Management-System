package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片地址（冗余）
     */
    private String image;

    /**
     * 菜品ID
     */
    private Long dishId;

    /**
     * 套餐ID
     */
    private Long setmealId;

    /**
     * 菜品口味
     */
    private String dishFlavor;

    /**
     * 商品数量
     */
    private Integer number;

    /**
     * 商品单价
     */
    private BigDecimal amount;

    /**
     * 商品总价
     */
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

