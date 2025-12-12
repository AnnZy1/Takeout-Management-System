package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.dto.DictListDTO;
import com.delivery.management.dto.DictSaveDTO;
import com.delivery.management.dto.DictVO;
import com.delivery.management.entity.Dict;
import com.delivery.management.mapper.DictMapper;
import com.delivery.management.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictMapper dictMapper;

    /**
     * 根据字典类型查询字典列表
     */
    @Override
    public List<DictVO> listByType(DictListDTO listDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictType, listDTO.getDictType());
        queryWrapper.eq(Dict::getStatus, 1); // 只查询启用的字典

        // 按排序值升序排列
        queryWrapper.orderByAsc(Dict::getSort);

        // 执行查询
        List<Dict> dictList = dictMapper.selectList(queryWrapper);

        // 转换为VO
        return dictList.stream().map(dict -> {
            DictVO vo = new DictVO();
            BeanUtils.copyProperties(dict, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 新增字典
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DictVO save(DictSaveDTO saveDTO) {
        // 检查字典类型+编码是否已存在
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getDictType, saveDTO.getDictType());
        queryWrapper.eq(Dict::getDictCode, saveDTO.getDictCode());
        Dict existDict = dictMapper.selectOne(queryWrapper);
        if (existDict != null) {
            throw new BusinessException("字典类型和编码已存在");
        }

        // 创建字典实体
        Dict dict = new Dict();
        BeanUtils.copyProperties(saveDTO, dict);

        // 保存字典
        dictMapper.insert(dict);

        // 转换为VO返回
        DictVO vo = new DictVO();
        BeanUtils.copyProperties(dict, vo);

        log.info("新增字典成功：{} - {}", dict.getDictType(), dict.getDictLabel());
        return vo;
    }
}

