package com.delivery.management.service;

import com.delivery.management.dto.DictListDTO;
import com.delivery.management.dto.DictSaveDTO;
import com.delivery.management.dto.DictVO;

import java.util.List;

/**
 * 字典服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface DictService {

    /**
     * 根据字典类型查询字典列表
     * 
     * @param listDTO 查询条件
     * @return 字典列表
     */
    List<DictVO> listByType(DictListDTO listDTO);

    /**
     * 新增字典
     * 
     * @param saveDTO 字典信息
     * @return 字典信息
     */
    DictVO save(DictSaveDTO saveDTO);
}

