package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Branch;
import com.delivery.management.entity.Category;
import com.delivery.management.entity.Dish;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.Setmeal;
import com.delivery.management.mapper.BranchMapper;
import com.delivery.management.mapper.CategoryMapper;
import com.delivery.management.mapper.DishMapper;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.SetmealMapper;
import com.delivery.management.service.BranchService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分店服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class BranchServiceImpl implements BranchService {

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询分店列表
     */
    @Override
    public PageInfo<BranchVO> pageQuery(BranchPageQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<Branch> queryWrapper = new LambdaQueryWrapper<>();

        // 分店名称模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            queryWrapper.like(Branch::getName, queryDTO.getName());
        }

        // 分店状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Branch::getStatus, queryDTO.getStatus());
        }

        // 过滤已删除的分店
        queryWrapper.eq(Branch::getIsDeleted, 0);

        // 数据范围控制：分店员工仅查看本店信息
        Long currentId = BaseContext.getCurrentId();
        if (currentId != null) {
            Employee currentEmployee = employeeMapper.selectById(currentId);
            if (currentEmployee != null) {
                // 判断是否为管理员（这里简化处理，实际应该根据角色判断）
                // 如果不是管理员，只查看本店信息
                // 暂时先查询所有，后续可以在权限模块中完善
            }
        }

        // 按ID升序排列
        queryWrapper.orderByAsc(Branch::getId);

        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 执行查询
        List<Branch> branchList = branchMapper.selectList(queryWrapper);

        // 封装分页结果，使用 PageInfo 包装查询结果
        PageInfo<Branch> branchPageInfo = new PageInfo<>(branchList);
        
        // 转换为VO
        List<BranchVO> voList = branchList.stream().map(branch -> {
            BranchVO vo = new BranchVO();
            BeanUtils.copyProperties(branch, vo);
            return vo;
        }).collect(Collectors.toList());

        // 将 VO 列表设置到 PageInfo 中
        PageInfo<BranchVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(branchPageInfo, pageInfo);
        // 确保分页参数正确复制
        pageInfo.setTotal(branchPageInfo.getTotal());
        pageInfo.setPages(branchPageInfo.getPages());
        pageInfo.setPageNum(branchPageInfo.getPageNum());
        pageInfo.setPageSize(branchPageInfo.getPageSize());
        
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 新增分店
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BranchVO save(BranchSaveDTO saveDTO) {
        // 检查分店名称是否已存在
        LambdaQueryWrapper<Branch> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Branch::getName, saveDTO.getName());
        Branch existBranch = branchMapper.selectOne(queryWrapper);
        if (existBranch != null) {
            throw new BusinessException("分店名称已存在");
        }

        // 创建分店实体
        Branch branch = new Branch();
        BeanUtils.copyProperties(saveDTO, branch);

        // 保存分店
        branchMapper.insert(branch);

        // 转换为VO返回
        BranchVO vo = new BranchVO();
        BeanUtils.copyProperties(branch, vo);

        log.info("新增分店成功：{}", branch.getName());
        return vo;
    }

    /**
     * 修改分店
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BranchUpdateDTO updateDTO) {
        // 查询分店
        Branch branch = branchMapper.selectById(updateDTO.getId());
        if (branch == null || branch.getIsDeleted() == 1) {
            throw new BusinessException("分店不存在");
        }

        // 如果修改分店名称，检查是否重复
        if (StringUtils.hasText(updateDTO.getName()) && !updateDTO.getName().equals(branch.getName())) {
            LambdaQueryWrapper<Branch> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Branch::getName, updateDTO.getName());
            Branch existBranch = branchMapper.selectOne(queryWrapper);
            if (existBranch != null && !existBranch.getId().equals(updateDTO.getId())) {
                throw new BusinessException("分店名称已存在");
            }
        }

        // 如果分店状态修改为禁用，需要禁用关联的员工、分类、菜品、套餐
        if (updateDTO.getStatus() != null && updateDTO.getStatus() == 0 && branch.getStatus() == 1) {
            // 禁用关联的员工
            LambdaUpdateWrapper<Employee> empWrapper = new LambdaUpdateWrapper<>();
            empWrapper.eq(Employee::getBranchId, updateDTO.getId());
            empWrapper.set(Employee::getStatus, 0);
            empWrapper.set(Employee::getUpdateUser, BaseContext.getCurrentId());
            employeeMapper.update(null, empWrapper);

            // 禁用关联的分类
            LambdaUpdateWrapper<Category> catWrapper = new LambdaUpdateWrapper<>();
            catWrapper.eq(Category::getBranchId, updateDTO.getId());
            catWrapper.set(Category::getStatus, 0);
            catWrapper.set(Category::getUpdateUser, BaseContext.getCurrentId());
            categoryMapper.update(null, catWrapper);

            // 注意：菜品和套餐的禁用需要在对应的Service中处理
        }

        // 更新分店信息
        Branch updateBranch = new Branch();
        BeanUtils.copyProperties(updateDTO, updateBranch);
        branchMapper.updateById(updateBranch);

        log.info("修改分店成功，分店ID：{}", updateDTO.getId());
    }

    /**
     * 删除分店
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 查询分店
        Branch branch = branchMapper.selectById(id);
        if (branch == null || branch.getIsDeleted() == 1) {
            throw new BusinessException("分店不存在");
        }

        // 检查分店是否关联员工
        LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(Employee::getBranchId, id);
        empWrapper.eq(Employee::getIsDeleted, 0);
        Long empCount = employeeMapper.selectCount(empWrapper);
        if (empCount > 0) {
            throw new BusinessException("分店关联员工，不可删除");
        }

        // 检查分店是否关联分类
        LambdaQueryWrapper<Category> catWrapper = new LambdaQueryWrapper<>();
        catWrapper.eq(Category::getBranchId, id);
        catWrapper.eq(Category::getIsDeleted, 0);
        Long catCount = categoryMapper.selectCount(catWrapper);
        if (catCount > 0) {
            throw new BusinessException("分店关联分类，不可删除");
        }

        // 检查分店是否关联菜品
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getBranchId, id);
        dishWrapper.eq(Dish::getIsDeleted, 0);
        Long dishCount = dishMapper.selectCount(dishWrapper);
        if (dishCount > 0) {
            throw new BusinessException("分店关联菜品，不可删除");
        }

        // 检查分店是否关联套餐
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getBranchId, id);
        setmealWrapper.eq(Setmeal::getIsDeleted, 0);
        Long setmealCount = setmealMapper.selectCount(setmealWrapper);
        if (setmealCount > 0) {
            throw new BusinessException("分店关联套餐，不可删除");
        }

        // 检查分店是否关联订单（这里需要OrdersMapper，暂时先不检查，后续完善）
        // 如果有订单关联，不可删除

        // 软删除分店
        // 使用 deleteById 触发 MyBatis-Plus 的逻辑删除机制
        branchMapper.deleteById(id);

        log.info("删除分店成功，分店ID：{}", id);
    }
}

