package com.delivery.management.controller;

import com.delivery.management.common.result.Result;
import com.delivery.management.dto.DictListDTO;
import com.delivery.management.dto.DictSaveDTO;
import com.delivery.management.dto.DictVO;
import com.delivery.management.service.DictService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员字典控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/dict")
public class AdminDictController {

    @Autowired
    private DictService dictService;

    /**
     * 根据字典类型查询字典列表
     * 
     * @param listDTO 查询条件
     * @return 字典列表
     */
    @GetMapping("/list")
    public Result<List<DictVO>> list(DictListDTO listDTO) {
        log.info("查询字典列表，字典类型：{}", listDTO.getDictType());
        List<DictVO> dictList = dictService.listByType(listDTO);
        return Result.success("查询成功", dictList);
    }

    /**
     * 新增字典
     * 
     * @param saveDTO 字典信息
     * @return 字典信息
     */
    @PostMapping("/save")
    public Result<DictVO> save(@Valid @RequestBody DictSaveDTO saveDTO) {
        log.info("新增字典：{} - {}", saveDTO.getDictType(), saveDTO.getDictLabel());
        DictVO dictVO = dictService.save(saveDTO);
        return Result.success("新增字典成功", dictVO);
    }
}

