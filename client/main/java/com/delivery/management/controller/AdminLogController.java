package com.delivery.management.controller;

import com.delivery.management.common.result.Result;
import com.delivery.management.dto.LoginLogPageQueryDTO;
import com.delivery.management.service.LogService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员日志控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/log")
public class AdminLogController {

    @Autowired
    private LogService logService;

    /**
     * 分页查询登录日志
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/loginList")
    public Result<PageInfo<?>> loginList(LoginLogPageQueryDTO queryDTO) {
        log.info("分页查询登录日志：{}", queryDTO);
        PageInfo<?> pageInfo = logService.pageQueryLoginLog(queryDTO);
        return Result.success(pageInfo);
    }
}

