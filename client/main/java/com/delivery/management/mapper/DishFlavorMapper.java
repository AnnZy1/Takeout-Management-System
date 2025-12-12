package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 菜品口味Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

    /**
     * 根据菜品ID删除口味
     * 
     * @param dishId 菜品ID
     */
    void deleteByDishId(@Param("dishId") Long dishId);
}

