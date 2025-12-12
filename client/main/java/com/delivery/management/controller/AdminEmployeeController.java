package com.delivery.management.controller;

import com.delivery.management.common.annotation.Log;
import com.delivery.management.common.result.Result;
import com.delivery.management.dto.*;
import com.delivery.management.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员员工控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class AdminEmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 分页查询员工列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    public Result<PageInfo<EmployeeVO>> list(EmployeePageQueryDTO queryDTO) {
        log.info("分页查询员工列表：{}", queryDTO);
        PageInfo<EmployeeVO> pageInfo = employeeService.pageQuery(queryDTO);
        return Result.success(pageInfo);
    }

    /**
     * 新增员工
     * 
     * @param saveDTO 员工信息
     * @return 员工信息
     */
    @Log(module = "员工管理", operationType = "新增")
    @PostMapping("/save")
    public Result<EmployeeVO> save(@Valid @RequestBody EmployeeSaveDTO saveDTO) {
        log.info("新增员工：{}", saveDTO.getUsername());
        EmployeeVO employeeVO = employeeService.save(saveDTO);
        return Result.success("新增员工成功", employeeVO);
    }

    /**
     * 修改员工
     * 
     * @param updateDTO 员工信息
     * @return 修改结果
     */
    @Log(module = "员工管理", operationType = "修改")
    @PutMapping("/update")
    public Result<?> update(@Valid @RequestBody EmployeeUpdateDTO updateDTO) {
        log.info("修改员工，员工ID：{}", updateDTO.getId());
        employeeService.update(updateDTO);
        return Result.success("修改员工成功");
    }

    /**
     * 密码重置
     * 
     * @param resetPwdDTO 重置信息
     * @return 重置结果
     */
    @Log(module = "员工管理", operationType = "密码重置")
    @PostMapping("/resetPwd")
    public Result<?> resetPassword(@Valid @RequestBody ResetPwdDTO resetPwdDTO) {
        log.info("密码重置，员工ID：{}", resetPwdDTO.getEmployeeId());
        employeeService.resetPassword(resetPwdDTO);
        return Result.success("密码重置成功（默认密码：123456）");
    }

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     * @return 操作结果
     */
    @Log(module = "员工管理", operationType = "批量操作")
    @PostMapping("/batch")
    public Result<?> batchOperation(@Valid @RequestBody BatchOperationDTO batchDTO) {
        log.info("批量操作，操作类型：{}，员工ID列表：{}", batchDTO.getOperation(), batchDTO.getIds());
        employeeService.batchOperation(batchDTO);
        
        String operationName = switch (batchDTO.getOperation()) {
            case "lock" -> "锁定";
            case "unlock" -> "启用";
            case "delete" -> "删除";
            default -> "操作";
        };
        
        return Result.success(String.format("批量%s%d名员工成功", operationName, batchDTO.getIds().size()));
    }
}

