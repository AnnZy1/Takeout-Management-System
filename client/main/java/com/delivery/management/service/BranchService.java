package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * 分店服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface BranchService {

    /**
     * 分页查询分店列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<BranchVO> pageQuery(BranchPageQueryDTO queryDTO);

    /**
     * 新增分店
     * 
     * @param saveDTO 分店信息
     * @return 分店信息
     */
    BranchVO save(BranchSaveDTO saveDTO);

    /**
     * 修改分店
     * 
     * @param updateDTO 分店信息
     */
    void update(BranchUpdateDTO updateDTO);

    /**
     * 删除分店
     * 
     * @param id 分店ID
     */
    void delete(Long id);
}

