package com.delivery.management.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 菜品Excel导入导出DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DishExcelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("菜品名称")
    private String name;

    @ExcelProperty("分类名称")
    private String categoryName;

    @ExcelProperty("基础单价")
    private BigDecimal price;

    @ExcelProperty("规格配置")
    private String specifications;

    @ExcelProperty("菜品描述")
    private String description;

    @ExcelProperty("售卖状态")
    private String status;

    @ExcelProperty("口味")
    private String flavors;
}

