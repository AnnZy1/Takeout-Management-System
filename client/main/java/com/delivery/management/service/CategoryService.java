package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * 分类服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface CategoryService {

    /**
     * 分页查询分类列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<CategoryVO> pageQuery(CategoryPageQueryDTO queryDTO);

    /**
     * 新增分类
     * 
     * @param saveDTO 分类信息
     * @return 分类信息
     */
    CategoryVO save(CategorySaveDTO saveDTO);

    /**
     * 修改分类
     * 
     * @param updateDTO 分类信息
     */
    void update(CategoryUpdateDTO updateDTO);

    /**
     * 删除分类
     * 
     * @param id 分类ID
     */
    void delete(Long id);

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     */
    void batchOperation(CategoryBatchDTO batchDTO);

    /**
     * 拖拽排序
     * 
     * @param sortDTO 排序信息
     */
    void sort(CategorySortDTO sortDTO);
}

