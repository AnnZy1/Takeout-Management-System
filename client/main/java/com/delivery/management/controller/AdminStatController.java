package com.delivery.management.controller;

import com.delivery.management.dto.*;
import com.delivery.management.service.StatService;
import com.delivery.management.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员数据统计控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/stat")
public class AdminStatController {

    @Autowired
    private StatService statService;

    /**
     * 营业数据统计
     * 
     * @param queryDTO 查询条件
     * @return 统计结果
     */
    @GetMapping("/business")
    public Result<StatBusinessVO> business(StatBusinessDTO queryDTO) {
        log.info("营业数据统计：{}", queryDTO);
        StatBusinessVO vo = statService.business(queryDTO);
        return Result.success("查询成功", vo);
    }

    /**
     * 销量排行
     * 
     * @param queryDTO 查询条件
     * @return 排行列表
     */
    @GetMapping("/salesTop")
    public Result<List<StatSalesTopVO>> salesTop(StatSalesTopDTO queryDTO) {
        log.info("销量排行统计：{}", queryDTO);
        List<StatSalesTopVO> list = statService.salesTop(queryDTO);
        return Result.success("查询成功", list);
    }

    /**
     * 订单分析
     * 
     * @param queryDTO 查询条件
     * @return 分析结果
     */
    @GetMapping("/orderAnalysis")
    public Result<Object> orderAnalysis(StatBusinessDTO queryDTO) {
        log.info("订单分析：{}", queryDTO);
        Object result = statService.orderAnalysis(queryDTO);
        return Result.success("查询成功", result);
    }
}

