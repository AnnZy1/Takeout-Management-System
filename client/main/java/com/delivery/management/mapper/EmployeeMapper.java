package com.delivery.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.delivery.management.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工Mapper接口
 * 
 * @author system
 * @date 2025-01-15
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}

