package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 套餐菜品关系Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

    /**
     * 根据套餐ID删除关联菜品
     * 
     * @param setmealId 套餐ID
     */
    void deleteBySetmealId(@Param("setmealId") Long setmealId);

    /**
     * 批量插入套餐菜品关联
     * 
     * @param setmealDishes 套餐菜品关联列表
     */
    void insertBatch(@Param("list") List<SetmealDish> setmealDishes);
}

