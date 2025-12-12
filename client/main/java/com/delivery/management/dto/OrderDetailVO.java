package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class OrderDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片地址
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
}

