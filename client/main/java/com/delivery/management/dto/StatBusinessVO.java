package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 营业数据统计VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class StatBusinessVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总营业额
     */
    private BigDecimal totalAmount;

    /**
     * 订单总数
     */
    private Long orderCount;

    /**
     * 客单价
     */
    private BigDecimal avgAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;
}

