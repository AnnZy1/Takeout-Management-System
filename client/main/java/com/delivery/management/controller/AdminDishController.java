package com.delivery.management.controller;

import com.delivery.management.common.annotation.Log;
import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.DishService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理员菜品控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class AdminDishController {

    @Autowired
    private DishService dishService;

    /**
     * 分页查询菜品列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<DishVO>> list(DishPageQueryDTO queryDTO) {
        log.info("分页查询菜品列表：{}", queryDTO);
        PageInfo<DishVO> pageInfo = dishService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 新增菜品
     * 
     * @param saveDTO 菜品信息
     * @return 菜品信息
     */
    @Log(module = "菜品管理", operationType = "新增")
    @PostMapping("/save")
    public Result<DishVO> save(@Valid @RequestBody DishSaveDTO saveDTO) {
        log.info("新增菜品：{}", saveDTO.getName());
        DishVO dishVO = dishService.save(saveDTO);
        return Result.success("新增菜品成功", dishVO);
    }

    /**
     * 修改菜品
     * 
     * @param updateDTO 菜品信息
     * @return 修改结果
     */
    @Log(module = "菜品管理", operationType = "修改")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody DishUpdateDTO updateDTO) {
        log.info("修改菜品，菜品ID：{}", updateDTO.getId());
        dishService.update(updateDTO);
        return Result.success("修改菜品成功");
    }

    /**
     * 删除菜品
     * 
     * @param id 菜品ID
     * @return 删除结果
     */
    @Log(module = "菜品管理", operationType = "删除")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除菜品，菜品ID：{}", id);
        dishService.delete(id);
        return Result.success("删除菜品成功");
    }

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     * @return 操作结果
     */
    @Log(module = "菜品管理", operationType = "批量操作")
    @PostMapping("/batch")
    public Result<?> batchOperation(@Valid @RequestBody DishBatchDTO batchDTO) {
        log.info("批量操作，操作类型：{}，菜品ID列表：{}", batchDTO.getOperation(), batchDTO.getIds());
        dishService.batchOperation(batchDTO);
        
        String operationName = switch (batchDTO.getOperation()) {
            case "enable" -> "起售";
            case "disable" -> "停售";
            default -> "操作";
        };
        
        return Result.success(String.format("批量%s%d个菜品成功", operationName, batchDTO.getIds().size()));
    }

    /**
     * 导入菜品
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/import")
    public Result<?> importDish(@RequestParam("file") MultipartFile file) {
        log.info("导入菜品：{}", file.getOriginalFilename());
        dishService.importDish(file);
        return Result.success("导入菜品成功");
    }

    /**
     * 导出菜品
     * 
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    @GetMapping("/export")
    public void exportDish(DishPageQueryDTO queryDTO, HttpServletResponse response) {
        log.info("导出菜品：{}", queryDTO);
        dishService.exportDish(queryDTO, response);
    }

    /**
     * 恢复菜品（从回收站恢复）
     * 
     * @param id 菜品ID
     * @return 恢复结果
     */
    @Log(module = "菜品管理", operationType = "恢复")
    @PutMapping("/restore/{id}")
    public Result<?> restore(@PathVariable Long id) {
        log.info("恢复菜品，菜品ID：{}", id);
        dishService.restore(id);
        return Result.success("恢复菜品成功");
    }

    /**
     * 永久删除菜品（从数据库物理删除）
     * 
     * @param id 菜品ID
     * @return 删除结果
     */
    @Log(module = "菜品管理", operationType = "永久删除")
    @DeleteMapping("/permanent/{id}")
    public Result<?> deletePermanently(@PathVariable Long id) {
        log.info("永久删除菜品，菜品ID：{}", id);
        dishService.deletePermanently(id);
        return Result.success("永久删除菜品成功");
    }
}

