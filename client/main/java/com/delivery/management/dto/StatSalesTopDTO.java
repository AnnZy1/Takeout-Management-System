package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 销量排行统计DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class StatSalesTopDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型：dish-菜品 setmeal-套餐
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 开始时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    @NotBlank(message = "开始时间不能为空")
    private String beginTime;

    /**
     * 结束时间（格式：yyyy-MM-dd HH:mm:ss）
     */
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    /**
     * 分店ID
     */
    private Long branchId;
}

