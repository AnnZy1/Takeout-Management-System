package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名（冗余）
     */
    private String operatorName;

    /**
     * 操作分店ID
     */
    private Long branchId;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 操作IP地址
     */
    private String ip;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 操作状态：1-成功 0-失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;
}

