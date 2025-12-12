package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 套餐服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface SetmealService {

    /**
     * 分页查询套餐列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<SetmealVO> pageQuery(SetmealPageQueryDTO queryDTO);

    /**
     * 新增套餐
     * 
     * @param saveDTO 套餐信息
     * @return 套餐信息
     */
    SetmealVO save(SetmealSaveDTO saveDTO);

    /**
     * 修改套餐
     * 
     * @param updateDTO 套餐信息
     */
    void update(SetmealUpdateDTO updateDTO);

    /**
     * 删除套餐
     * 
     * @param id 套餐ID
     */
    void delete(Long id);

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     */
    void batchOperation(SetmealBatchDTO batchDTO);

    /**
     * 导出套餐
     * 
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    void exportSetmeal(SetmealPageQueryDTO queryDTO, HttpServletResponse response);

    /**
     * 恢复套餐
     * 
     * @param id 套餐ID
     */
    void restore(Long id);

    /**
     * 物理删除套餐
     * 
     * @param id 套餐ID
     */
    void deletePermanently(Long id);
}
