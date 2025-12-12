package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.dto.*;
import com.delivery.management.entity.OrderDetail;
import com.delivery.management.entity.Orders;
import com.delivery.management.mapper.OrderDetailMapper;
import com.delivery.management.mapper.OrdersMapper;
import com.delivery.management.service.StatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据统计服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class StatServiceImpl implements StatService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 营业数据统计
     */
    @Override
    public StatBusinessVO business(StatBusinessDTO queryDTO) {
        // 计算时间范围
        LocalDateTime beginTime;
        LocalDateTime endTime = LocalDateTime.now();

        switch (queryDTO.getType()) {
            case "today":
                beginTime = endTime.toLocalDate().atStartOfDay();
                break;
            case "week":
                beginTime = endTime.minusDays(7);
                break;
            case "month":
                beginTime = endTime.minusDays(30);
                break;
            case "custom":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                beginTime = LocalDateTime.parse(queryDTO.getBeginTime(), formatter);
                endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);
                break;
            default:
                beginTime = endTime.toLocalDate().atStartOfDay();
        }

        // 构建查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        queryWrapper.eq(Orders::getStatus, 5); // 只统计已完成的订单

        // 分店ID过滤
        if (queryDTO.getBranchId() != null) {
            queryWrapper.eq(Orders::getBranchId, queryDTO.getBranchId());
        }

        // 查询订单列表
        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        // 计算统计数据
        BigDecimal totalAmount = ordersList.stream()
                .map(Orders::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long orderCount = (long) ordersList.size();

        BigDecimal avgAmount = orderCount > 0
                ? totalAmount.divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal refundAmount = ordersList.stream()
                .filter(order -> order.getPayStatus() == 2)
                .map(Orders::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 封装结果
        StatBusinessVO vo = new StatBusinessVO();
        vo.setTotalAmount(totalAmount);
        vo.setOrderCount(orderCount);
        vo.setAvgAmount(avgAmount);
        vo.setRefundAmount(refundAmount);

        return vo;
    }

    /**
     * 销量排行
     */
    @Override
    public List<StatSalesTopVO> salesTop(StatSalesTopDTO queryDTO) {
        // 解析时间范围
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse(queryDTO.getBeginTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);

        // 查询时间范围内的订单
        LambdaQueryWrapper<Orders> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.between(Orders::getOrderTime, beginTime, endTime);
        orderWrapper.eq(Orders::getStatus, 5); // 只统计已完成的订单

        if (queryDTO.getBranchId() != null) {
            orderWrapper.eq(Orders::getBranchId, queryDTO.getBranchId());
        }

        List<Orders> ordersList = ordersMapper.selectList(orderWrapper);
        List<Long> orderIds = ordersList.stream()
                .map(Orders::getId)
                .collect(Collectors.toList());

        if (orderIds.isEmpty()) {
            return List.of();
        }

        // 查询订单明细
        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(OrderDetail::getOrderId, orderIds);

        if ("dish".equals(queryDTO.getType())) {
            detailWrapper.isNotNull(OrderDetail::getDishId);
        } else if ("setmeal".equals(queryDTO.getType())) {
            detailWrapper.isNotNull(OrderDetail::getSetmealId);
        }

        List<OrderDetail> orderDetails = orderDetailMapper.selectList(detailWrapper);

        // 按商品名称分组统计
        Map<String, StatSalesTopVO> statMap = new HashMap<>();
        for (OrderDetail detail : orderDetails) {
            String name = detail.getName();
            StatSalesTopVO vo = statMap.getOrDefault(name, new StatSalesTopVO());
            vo.setName(name);
            vo.setSales(vo.getSales() != null ? vo.getSales() + detail.getNumber() : detail.getNumber());
            vo.setAmount(vo.getAmount() != null
                    ? vo.getAmount().add(detail.getTotalAmount())
                    : detail.getTotalAmount());
            statMap.put(name, vo);
        }

        // 转换为列表并按销量排序
        List<StatSalesTopVO> result = statMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getSales(), a.getSales()))
                .limit(10)
                .collect(Collectors.toList());

        return result;
    }

    /**
     * 订单分析
     */
    @Override
    public Object orderAnalysis(StatBusinessDTO queryDTO) {
        // 计算时间范围（同营业数据统计）
        LocalDateTime beginTime;
        LocalDateTime endTime = LocalDateTime.now();

        switch (queryDTO.getType()) {
            case "today":
                beginTime = endTime.toLocalDate().atStartOfDay();
                break;
            case "week":
                beginTime = endTime.minusDays(7);
                break;
            case "month":
                beginTime = endTime.minusDays(30);
                break;
            case "custom":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                beginTime = LocalDateTime.parse(queryDTO.getBeginTime(), formatter);
                endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);
                break;
            default:
                beginTime = endTime.toLocalDate().atStartOfDay();
        }

        // 构建查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Orders::getOrderTime, beginTime, endTime);

        if (queryDTO.getBranchId() != null) {
            queryWrapper.eq(Orders::getBranchId, queryDTO.getBranchId());
        }

        // 查询订单列表
        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        // 按状态统计
        Map<Integer, Long> statusMap = ordersList.stream()
                .collect(Collectors.groupingBy(Orders::getStatus, Collectors.counting()));

        // 按支付方式统计
        Map<Integer, Long> payMethodMap = ordersList.stream()
                .filter(order -> order.getPayMethod() != null)
                .collect(Collectors.groupingBy(Orders::getPayMethod, Collectors.counting()));

        // 封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("statusDistribution", statusMap);
        result.put("payMethodDistribution", payMethodMap);

        return result;
    }
}

