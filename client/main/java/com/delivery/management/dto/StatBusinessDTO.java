package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 营业数据统计DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class StatBusinessDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 时间类型：today/week/month/custom
     */
    private String type;

    /**
     * 开始时间（自定义时间范围）
     */
    private String beginTime;

    /**
     * 结束时间（自定义时间范围）
     */
    private String endTime;

    /**
     * 分店ID
     */
    private Long branchId;
}

