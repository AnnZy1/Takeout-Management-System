package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销量排行VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class StatSalesTopVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 销量
     */
    private Long sales;

    /**
     * 销售额
     */
    private BigDecimal amount;
}

