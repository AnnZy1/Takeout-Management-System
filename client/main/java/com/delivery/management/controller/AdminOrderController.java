package com.delivery.management.controller;

import com.delivery.management.common.annotation.Log;
import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.OrderService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员订单控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 分页查询订单列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<OrderVO>> list(OrderPageQueryDTO queryDTO) {
        log.info("分页查询订单列表：{}", queryDTO);
        PageInfo<OrderVO> pageInfo = orderService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 查询订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    @GetMapping("/detail")
    public Result<OrderVO> detail(@RequestParam Long id) {
        log.info("查询订单详情，订单ID：{}", id);
        OrderVO orderVO = orderService.getDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 修改订单状态
     * 
     * @param updateDTO 状态更新信息
     * @return 更新结果
     */
    @Log(module = "订单管理", operationType = "修改状态")
    @PutMapping("/updateStatus")
    public Result<?> updateStatus(@Valid @RequestBody OrderUpdateStatusDTO updateDTO) {
        log.info("修改订单状态，订单ID：{}，新状态：{}", updateDTO.getId(), updateDTO.getStatus());
        orderService.updateStatus(updateDTO);
        return Result.success("订单状态更新成功");
    }

    /**
     * 取消订单
     * 
     * @param cancelDTO 取消信息
     * @return 取消结果
     */
    @Log(module = "订单管理", operationType = "取消订单")
    @PostMapping("/cancel")
    public Result<?> cancel(@Valid @RequestBody OrderCancelDTO cancelDTO) {
        log.info("取消订单，订单ID：{}", cancelDTO.getId());
        orderService.cancel(cancelDTO);
        return Result.success("订单取消成功");
    }

    /**
     * 导出订单
     * 
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    @GetMapping("/export")
    public void exportOrder(OrderPageQueryDTO queryDTO, HttpServletResponse response) {
        log.info("导出订单：{}", queryDTO);
        orderService.exportOrder(queryDTO, response);
    }
}

