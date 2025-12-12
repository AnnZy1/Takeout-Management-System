package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录日志Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}

