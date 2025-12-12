package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 字典VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DictVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    private Long id;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典编码
     */
    private Integer dictCode;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * 字典描述
     */
    private String dictDesc;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 字典状态：1-启用 0-禁用
     */
    private Integer status;
}

