package com.delivery.management.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 套餐Excel导出DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class SetmealExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("套餐名称")
    private String name;

    @ExcelProperty("分类名称")
    private String categoryName;

    @ExcelProperty("套餐总价")
    private BigDecimal price;

    @ExcelProperty("套餐描述")
    private String description;

    @ExcelProperty("售卖状态")
    private String status;

    @ExcelProperty("关联菜品")
    private String dishes;
}

