package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据字典实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("dict")
public class Dict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典ID
     */
    @TableId(type = IdType.AUTO)
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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;
}

