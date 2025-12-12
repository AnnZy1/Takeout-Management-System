package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 订单分页查询DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class OrderPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

    /**
     * 订单号（模糊查询）
     */
    private String number;

    /**
     * 收货人（模糊查询）
     */
    private String consignee;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String beginTime;

    /**
     * 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    private String endTime;

    /**
     * 分店ID
     */
    private Long branchId;
}

