package com.delivery.management.controller;

import com.delivery.management.common.annotation.Log;
import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.SetmealService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员套餐控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
public class AdminSetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 分页查询套餐列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<SetmealVO>> list(SetmealPageQueryDTO queryDTO) {
        log.info("分页查询套餐列表：{}", queryDTO);
        PageInfo<SetmealVO> pageInfo = setmealService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 新增套餐
     * 
     * @param saveDTO 套餐信息
     * @return 套餐信息
     */
    @Log(module = "套餐管理", operationType = "新增")
    @PostMapping("/save")
    public Result<SetmealVO> save(@Valid @RequestBody SetmealSaveDTO saveDTO) {
        log.info("新增套餐：{}", saveDTO.getName());
        SetmealVO setmealVO = setmealService.save(saveDTO);
        return Result.success("新增套餐成功", setmealVO);
    }

    /**
     * 修改套餐
     * 
     * @param updateDTO 套餐信息
     * @return 修改结果
     */
    @Log(module = "套餐管理", operationType = "修改")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody SetmealUpdateDTO updateDTO) {
        log.info("修改套餐，套餐ID：{}", updateDTO.getId());
        setmealService.update(updateDTO);
        return Result.success("修改套餐成功");
    }

    /**
     * 删除套餐
     * 
     * @param id 套餐ID
     * @return 删除结果
     */
    @Log(module = "套餐管理", operationType = "删除")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        log.info("删除套餐，套餐ID：{}", id);
        setmealService.delete(id);
        return Result.success("删除套餐成功");
    }

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     * @return 操作结果
     */
    @PostMapping("/batch")
    public Result<?> batchOperation(@Valid @RequestBody SetmealBatchDTO batchDTO) {
        log.info("批量操作，操作类型：{}，套餐ID列表：{}", batchDTO.getOperation(), batchDTO.getIds());
        setmealService.batchOperation(batchDTO);
        
        String operationName = switch (batchDTO.getOperation()) {
            case "enable" -> "起售";
            case "disable" -> "停售";
            default -> "操作";
        };
        
        return Result.success(String.format("批量%s%d个套餐成功", operationName, batchDTO.getIds().size()));
    }

    /**
     * 导出套餐
     * 
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    @GetMapping("/export")
    public void exportSetmeal(SetmealPageQueryDTO queryDTO, HttpServletResponse response) {
        log.info("导出套餐：{}", queryDTO);
        setmealService.exportSetmeal(queryDTO, response);
    }

    /**
     * 恢复套餐（从回收站恢复）
     * 
     * @param id 套餐ID
     * @return 恢复结果
     */
    @Log(module = "套餐管理", operationType = "恢复")
    @PutMapping("/restore/{id}")
    public Result<?> restore(@PathVariable Long id) {
        log.info("恢复套餐，套餐ID：{}", id);
        setmealService.restore(id);
        return Result.success("恢复套餐成功");
    }

    /**
     * 永久删除套餐（从数据库物理删除）
     * 
     * @param id 套餐ID
     * @return 删除结果
     */
    @Log(module = "套餐管理", operationType = "永久删除")
    @DeleteMapping("/permanent/{id}")
    public Result<?> deletePermanently(@PathVariable Long id) {
        log.info("永久删除套餐，套餐ID：{}", id);
        setmealService.deletePermanently(id);
        return Result.success("永久删除套餐成功");
    }
}
