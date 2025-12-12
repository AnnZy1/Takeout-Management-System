package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * 订单服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface OrderService {

    /**
     * 分页查询订单列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<OrderVO> pageQuery(OrderPageQueryDTO queryDTO);

    /**
     * 查询订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    OrderVO getDetail(Long id);

    /**
     * 修改订单状态
     * 
     * @param updateDTO 状态更新信息
     */
    void updateStatus(OrderUpdateStatusDTO updateDTO);

    /**
     * 取消订单
     * 
     * @param cancelDTO 取消信息
     */
    void cancel(OrderCancelDTO cancelDTO);

    /**
     * 导出订单
     * 
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    void exportOrder(OrderPageQueryDTO queryDTO, jakarta.servlet.http.HttpServletResponse response);
}

