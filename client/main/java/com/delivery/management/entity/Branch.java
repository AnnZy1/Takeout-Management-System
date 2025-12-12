package com.delivery.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分店实体类
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
@TableName("branch")
public class Branch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分店ID
     */
    @TableId(type = IdType.AUTO)
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
     * 软删除标识：1-已删 0-未删
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 最后修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}

