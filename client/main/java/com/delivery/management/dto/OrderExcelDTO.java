package com.delivery.management.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单Excel导出DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class OrderExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("订单号")
    private String number;

    @ExcelProperty("订单状态")
    private String status;

    @ExcelProperty("分店名称")
    private String branchName;

    @ExcelProperty("收货人")
    private String consignee;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("详细地址")
    private String address;

    @ExcelProperty("下单时间")
    private LocalDateTime orderTime;

    @ExcelProperty("订单总金额")
    private BigDecimal amount;

    @ExcelProperty("支付方式")
    private String payMethod;

    @ExcelProperty("支付状态")
    private String payStatus;
}

