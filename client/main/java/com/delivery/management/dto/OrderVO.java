package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String number;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单状态标签
     */
    private String statusLabel;

    /**
     * 分店ID
     */
    private Long branchId;

    /**
     * 分店名称
     */
    private String branchName;

    /**
     * 收货人
     */
    private String consignee;

    /**
     * 收货人手机号
     */
    private String phone;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 付款时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 支付方式
     */
    private Integer payMethod;

    /**
     * 支付状态
     */
    private Integer payStatus;

    /**
     * 订单总金额
     */
    private BigDecimal amount;

    /**
     * 打包费
     */
    private BigDecimal packAmount;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 拒单原因
     */
    private String rejectionReason;

    /**
     * 订单明细列表
     */
    private List<OrderDetailVO> orderDetails;
}

