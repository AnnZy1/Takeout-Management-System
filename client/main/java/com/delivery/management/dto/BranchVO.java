package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分店VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class BranchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分店ID
     */
    private Long id;

    /**
     * 分店名称
     */
    private String name;

    /**
     * 分店地址
     */
    private String address;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    /**
     * 分店状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}

