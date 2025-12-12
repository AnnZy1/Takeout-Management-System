package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String number;

    /**
     * 订单状态：1-待付款 2-待接单 3-已接单 4-派送中 5-已完成 6-已取消
     */
    private Integer status;

    /**
     * 分店ID
     */
    private Long branchId;

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
     * 支付方式：1-微信支付 2-支付宝支付
     */
    private Integer payMethod;

    /**
     * 支付状态：0-未支付 1-已支付 2-退款
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
     * 餐具数量
     */
    private Integer tablewareNumber;

    /**
     * 餐具状态：1-按餐量提供 0-自定义数量
     */
    private Integer tablewareStatus;

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
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 预计送达时间
     */
    @TableField(exist = false)
    private LocalDateTime estimatedDeliveryTime;

    /**
     * 实际送达时间
     */
    private LocalDateTime deliveryTime;

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
     * 最后修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}

