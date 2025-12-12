package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * 菜品服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface DishService {

    /**
     * 分页查询菜品列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<DishVO> pageQuery(DishPageQueryDTO queryDTO);

    /**
     * 新增菜品
     * 
     * @param saveDTO 菜品信息
     * @return 菜品信息
     */
    DishVO save(DishSaveDTO saveDTO);

    /**
     * 修改菜品
     * 
     * @param updateDTO 菜品信息
     */
    void update(DishUpdateDTO updateDTO);

    /**
     * 删除菜品
     * 
     * @param id 菜品ID
     */
    void delete(Long id);

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     */
    void batchOperation(DishBatchDTO batchDTO);

    /**
     * 导入菜品
     * 
     * @param file Excel文件
     */
    void importDish(org.springframework.web.multipart.MultipartFile file);

    /**
     * 导出菜品
     * 
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    void exportDish(DishPageQueryDTO queryDTO, jakarta.servlet.http.HttpServletResponse response);

    /**
     * 恢复菜品（从回收站恢复）
     * 
     * @param id 菜品ID
     */
    void restore(Long id);

    /**
     * 永久删除菜品（从数据库物理删除）
     * 
     * @param id 菜品ID
     */
    void deletePermanently(Long id);
}

