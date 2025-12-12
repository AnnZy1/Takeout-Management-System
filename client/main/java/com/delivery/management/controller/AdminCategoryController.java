package com.delivery.management.controller;

import com.delivery.management.common.annotation.Log;
import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.CategoryService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员分类控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<CategoryVO>> list(CategoryPageQueryDTO queryDTO) {
        log.info("分页查询分类列表：{}", queryDTO);
        PageInfo<CategoryVO> pageInfo = categoryService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 新增分类
     * 
     * @param saveDTO 分类信息
     * @return 分类信息
     */
    @Log(module = "分类管理", operationType = "新增")
    @PostMapping("/save")
    public Result<CategoryVO> save(@Valid @RequestBody CategorySaveDTO saveDTO) {
        log.info("新增分类：{}", saveDTO.getName());
        CategoryVO categoryVO = categoryService.save(saveDTO);
        return Result.success("新增分类成功", categoryVO);
    }

    /**
     * 修改分类
     * 
     * @param updateDTO 分类信息
     * @return 修改结果
     */
    @Log(module = "分类管理", operationType = "修改")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody CategoryUpdateDTO updateDTO) {
        log.info("修改分类，分类ID：{}", updateDTO.getId());
        categoryService.update(updateDTO);
        return Result.success("修改分类成功");
    }

    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 删除结果
     */
    @Log(module = "分类管理", operationType = "删除")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除分类，分类ID：{}", id);
        categoryService.delete(id);
        return Result.success("删除分类成功");
    }

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     * @return 操作结果
     */
    @Log(module = "分类管理", operationType = "批量操作")
    @PostMapping("/batch")
    public Result<?> batchOperation(@Valid @RequestBody CategoryBatchDTO batchDTO) {
        log.info("批量操作，操作类型：{}，分类ID列表：{}", batchDTO.getOperation(), batchDTO.getIds());
        categoryService.batchOperation(batchDTO);
        
        String operationName = switch (batchDTO.getOperation()) {
            case "enable" -> "启用";
            case "disable" -> "禁用";
            default -> "操作";
        };
        
        return Result.success(String.format("批量%s%d个分类成功", operationName, batchDTO.getIds().size()));
    }

    /**
     * 拖拽排序
     * 
     * @param sortDTO 排序信息
     * @return 排序结果
     */
    @PostMapping("/sort")
    public Result<?> sort(@Valid @RequestBody CategorySortDTO sortDTO) {
        log.info("分类排序：{}", sortDTO.getSortList());
        categoryService.sort(sortDTO);
        return Result.success("排序成功");
    }
}

