package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.common.utils.ExcelUtil;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Branch;
import com.delivery.management.entity.OrderDetail;
import com.delivery.management.entity.Orders;
import com.delivery.management.mapper.BranchMapper;
import com.delivery.management.mapper.OrderDetailMapper;
import com.delivery.management.mapper.OrdersMapper;
import com.delivery.management.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private BranchMapper branchMapper;

    /**
     * 订单状态字典
     */
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put(1, "待付款");
        STATUS_MAP.put(2, "待接单");
        STATUS_MAP.put(3, "已接单");
        STATUS_MAP.put(4, "派送中");
        STATUS_MAP.put(5, "已完成");
        STATUS_MAP.put(6, "已取消");
    }

    /**
     * 分页查询订单列表
     */
    @Override
    public PageInfo<OrderVO> pageQuery(OrderPageQueryDTO queryDTO) {
        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        // 订单号模糊查询
        if (StringUtils.hasText(queryDTO.getNumber())) {
            queryWrapper.like(Orders::getNumber, queryDTO.getNumber());
        }

        // 收货人模糊查询
        if (StringUtils.hasText(queryDTO.getConsignee())) {
            queryWrapper.like(Orders::getConsignee, queryDTO.getConsignee());
        }

        // 手机号模糊查询
        if (StringUtils.hasText(queryDTO.getPhone())) {
            queryWrapper.like(Orders::getPhone, queryDTO.getPhone());
        }

        // 订单状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Orders::getStatus, queryDTO.getStatus());
        }

        // 时间范围查询
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.hasText(queryDTO.getBeginTime())) {
            LocalDateTime beginTime = LocalDateTime.parse(queryDTO.getBeginTime(), formatter);
            queryWrapper.ge(Orders::getOrderTime, beginTime);
        }
        if (StringUtils.hasText(queryDTO.getEndTime())) {
            LocalDateTime endTime = LocalDateTime.parse(queryDTO.getEndTime(), formatter);
            queryWrapper.le(Orders::getOrderTime, endTime);
        }

        // 分店ID（数据范围控制）
        if (queryDTO.getBranchId() != null) {
            queryWrapper.eq(Orders::getBranchId, queryDTO.getBranchId());
        }

        // 按下单时间倒序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);

        // 执行查询
        List<Orders> ordersList = ordersMapper.selectList(queryWrapper);

        // 转换为VO
        List<OrderVO> voList = ordersList.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setStatusLabel(STATUS_MAP.get(order.getStatus()));

            // 查询分店名称
            Branch branch = branchMapper.selectById(order.getBranchId());
            if (branch != null) {
                vo.setBranchName(branch.getName());
            }

            return vo;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageInfo<Orders> ordersPageInfo = new PageInfo<>(ordersList);
        PageInfo<OrderVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(ordersPageInfo, pageInfo);
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 查询订单详情
     */
    @Override
    public OrderVO getDetail(Long id) {
        // 查询订单
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 转换为VO
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusLabel(STATUS_MAP.get(order.getStatus()));

        // 查询分店名称
        Branch branch = branchMapper.selectById(order.getBranchId());
        if (branch != null) {
            vo.setBranchName(branch.getName());
        }

        // 查询订单明细
        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.eq(OrderDetail::getOrderId, id);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(detailWrapper);
        List<OrderDetailVO> detailVOs = orderDetails.stream().map(detail -> {
            OrderDetailVO detailVO = new OrderDetailVO();
            BeanUtils.copyProperties(detail, detailVO);
            return detailVO;
        }).collect(Collectors.toList());
        vo.setOrderDetails(detailVOs);

        return vo;
    }

    /**
     * 修改订单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(OrderUpdateStatusDTO updateDTO) {
        // 查询订单
        Orders order = ordersMapper.selectById(updateDTO.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证状态流转是否合法
        Integer currentStatus = order.getStatus();
        Integer newStatus = updateDTO.getStatus();

        // 状态流转规则：待接单(2) -> 已接单(3) -> 派送中(4) -> 已完成(5)
        if (currentStatus == 2 && newStatus == 3) {
            // 待接单 -> 已接单
        } else if (currentStatus == 3 && newStatus == 4) {
            // 已接单 -> 派送中
        } else if (currentStatus == 4 && newStatus == 5) {
            // 派送中 -> 已完成
            order.setDeliveryTime(LocalDateTime.now());
        } else if (currentStatus == 5) {
            throw new BusinessException("已完成订单不可修改状态");
        } else if (currentStatus == 6) {
            throw new BusinessException("已取消订单不可修改状态");
        } else {
            throw new BusinessException("不支持的状态流转");
        }

        // 更新订单状态
        order.setStatus(newStatus);
        order.setUpdateUser(BaseContext.getCurrentId());
        ordersMapper.updateById(order);

        log.info("订单状态更新成功，订单ID：{}，状态：{} -> {}", updateDTO.getId(), currentStatus, newStatus);
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(OrderCancelDTO cancelDTO) {
        // 查询订单
        Orders order = ordersMapper.selectById(cancelDTO.getId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 验证订单状态
        if (order.getStatus() == 5) {
            throw new BusinessException("已完成订单不可取消");
        }
        if (order.getStatus() == 6) {
            throw new BusinessException("订单已取消");
        }

        // 如果订单已支付，需要处理退款
        if (order.getPayStatus() == 1) {
            // 更新支付状态为退款
            order.setPayStatus(2);
            // 这里可以调用退款接口，暂时只更新状态
        }

        // 更新订单状态
        order.setStatus(6);
        order.setCancelReason(cancelDTO.getCancelReason());
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateUser(BaseContext.getCurrentId());
        ordersMapper.updateById(order);

        log.info("订单取消成功，订单ID：{}", cancelDTO.getId());
    }

    /**
     * 导出订单
     */
    @Override
    public void exportOrder(OrderPageQueryDTO queryDTO, HttpServletResponse response) {
        // 查询所有符合条件的订单（不分页）
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(Integer.MAX_VALUE);
        PageInfo<OrderVO> pageInfo = pageQuery(queryDTO);
        List<OrderVO> orderList = pageInfo.getList();

        // 转换为Excel DTO
        List<OrderExcelDTO> excelList = new ArrayList<>();
        for (OrderVO order : orderList) {
            OrderExcelDTO excelDTO = new OrderExcelDTO();
            excelDTO.setNumber(order.getNumber());
            excelDTO.setStatus(order.getStatusLabel());
            excelDTO.setBranchName(order.getBranchName());
            excelDTO.setConsignee(order.getConsignee());
            excelDTO.setPhone(order.getPhone());
            excelDTO.setAddress(order.getAddress());
            excelDTO.setOrderTime(order.getOrderTime());
            excelDTO.setAmount(order.getAmount());
            excelDTO.setPayMethod(order.getPayMethod() != null ? 
                (order.getPayMethod() == 1 ? "微信支付" : "支付宝支付") : "未支付");
            excelDTO.setPayStatus(order.getPayStatus() != null ? 
                (order.getPayStatus() == 0 ? "未支付" : order.getPayStatus() == 1 ? "已支付" : "退款") : "未支付");
            excelList.add(excelDTO);
        }

        // 导出Excel
        String fileName = "订单列表_" + System.currentTimeMillis();
        ExcelUtil.export(response, fileName, "订单列表", excelList, OrderExcelDTO.class);
    }
}

