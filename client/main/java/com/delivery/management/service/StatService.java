package com.delivery.management.service;

import com.delivery.management.dto.*;

import java.util.List;

/**
 * 数据统计服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface StatService {

    /**
     * 营业数据统计
     * 
     * @param queryDTO 查询条件
     * @return 统计结果
     */
    StatBusinessVO business(StatBusinessDTO queryDTO);

    /**
     * 销量排行
     * 
     * @param queryDTO 查询条件
     * @return 排行列表
     */
    List<StatSalesTopVO> salesTop(StatSalesTopDTO queryDTO);

    /**
     * 订单分析
     * 
     * @param queryDTO 查询条件
     * @return 分析结果
     */
    Object orderAnalysis(StatBusinessDTO queryDTO);
}

