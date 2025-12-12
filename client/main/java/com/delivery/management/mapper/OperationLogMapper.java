package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}

