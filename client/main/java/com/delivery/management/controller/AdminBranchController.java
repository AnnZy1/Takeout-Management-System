package com.delivery.management.controller;

import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.BranchService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员分店控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/branch")
public class AdminBranchController {

    @Autowired
    private BranchService branchService;

    /**
     * 分页查询分店列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<BranchVO>> list(BranchPageQueryDTO queryDTO) {
        log.info("分页查询分店列表：{}", queryDTO);
        PageInfo<BranchVO> pageInfo = branchService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 新增分店
     * 
     * @param saveDTO 分店信息
     * @return 分店信息
     */
    @PostMapping("/save")
    public Result<BranchVO> save(@Valid @RequestBody BranchSaveDTO saveDTO) {
        log.info("新增分店：{}", saveDTO.getName());
        BranchVO branchVO = branchService.save(saveDTO);
        return Result.success("新增分店成功", branchVO);
    }

    /**
     * 修改分店
     * 
     * @param updateDTO 分店信息
     * @return 修改结果
     */
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody BranchUpdateDTO updateDTO) {
        log.info("修改分店，分店ID：{}", updateDTO.getId());
        branchService.update(updateDTO);
        return Result.success("修改分店成功");
    }

    /**
     * 删除分店
     * 
     * @param id 分店ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除分店，分店ID：{}", id);
        branchService.delete(id);
        return Result.success("删除分店成功");
    }
}
