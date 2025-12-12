package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}

