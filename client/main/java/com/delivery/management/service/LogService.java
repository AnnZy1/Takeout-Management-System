package com.delivery.management.service;

import com.delivery.management.dto.LoginLogPageQueryDTO;
import com.github.pagehelper.PageInfo;

/**
 * 日志服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface LogService {

    /**
     * 分页查询登录日志
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<?> pageQueryLoginLog(LoginLogPageQueryDTO queryDTO);
}

