package com.delivery.management.service;

import com.delivery.management.dto.*;
import com.github.pagehelper.PageInfo;

/**
 * 员工服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface EmployeeService {

    /**
     * 分页查询员工列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageInfo<EmployeeVO> pageQuery(EmployeePageQueryDTO queryDTO);

    /**
     * 新增员工
     * 
     * @param saveDTO 员工信息
     * @return 员工信息
     */
    EmployeeVO save(EmployeeSaveDTO saveDTO);

    /**
     * 修改员工
     * 
     * @param updateDTO 员工信息
     */
    void update(EmployeeUpdateDTO updateDTO);

    /**
     * 密码重置
     * 
     * @param resetPwdDTO 重置信息
     */
    void resetPassword(ResetPwdDTO resetPwdDTO);

    /**
     * 批量操作
     * 
     * @param batchDTO 批量操作信息
     */
    void batchOperation(BatchOperationDTO batchDTO);
}

