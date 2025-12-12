package com.delivery.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 新增字典DTO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class DictSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    /**
     * 字典编码
     */
    @NotNull(message = "字典编码不能为空")
    private Integer dictCode;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    /**
     * 字典描述
     */
    private String dictDesc;

    /**
     * 排序值
     */
    private Integer sort = 0;

    /**
     * 字典状态：1-启用 0-禁用
     */
    private Integer status = 1;
}

