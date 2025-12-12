package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Category;
import com.delivery.management.entity.Dish;
import com.delivery.management.entity.Setmeal;
import com.delivery.management.mapper.BranchMapper;
import com.delivery.management.mapper.CategoryMapper;
import com.delivery.management.mapper.DishMapper;
import com.delivery.management.mapper.SetmealMapper;
import com.delivery.management.service.CategoryService;
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
 * 分类服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BranchMapper branchMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询分类列表
     */
    @Override
    public PageInfo<CategoryVO> pageQuery(CategoryPageQueryDTO queryDTO) {
        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        // 分类类型
        if (queryDTO.getType() != null) {
            queryWrapper.eq(Category::getType, queryDTO.getType());
        }

        // 分店ID（数据范围控制）
        if (queryDTO.getBranchId() != null) {
            queryWrapper.eq(Category::getBranchId, queryDTO.getBranchId());
        }

        // 分类状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Category::getStatus, queryDTO.getStatus());
        }

        // 分类名称模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            queryWrapper.like(Category::getName, queryDTO.getName());
        }

        // 按排序值升序排列
        queryWrapper.orderByAsc(Category::getSort);

        // 执行查询
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);
        
        // 封装分页结果，使用 PageInfo 包装查询结果
        PageInfo<Category> categoryPageInfo = new PageInfo<>(categoryList);

        // 转换为VO
        List<CategoryVO> voList = categoryList.stream().map(category -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            if (category.getBranchId() != null) {
                var branch = branchMapper.selectById(category.getBranchId());
                if (branch != null) {
                    vo.setBranchName(branch.getName());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        // 将 VO 列表设置到 PageInfo 中
        PageInfo<CategoryVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(categoryPageInfo, pageInfo);
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 新增分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryVO save(CategorySaveDTO saveDTO) {
        // 检查同分店同类型分类名称是否已存在
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, saveDTO.getName());
        queryWrapper.eq(Category::getType, saveDTO.getType());
        queryWrapper.eq(Category::getBranchId, saveDTO.getBranchId());
        Category existCategory = categoryMapper.selectOne(queryWrapper);
        if (existCategory != null) {
            throw new BusinessException("同分店同类型分类名称已存在");
        }

        // 验证分店是否存在
        var branch = branchMapper.selectById(saveDTO.getBranchId());
        if (branch == null || branch.getStatus() == 0 || branch.getIsDeleted() == 1) {
            throw new BusinessException("分店不存在或已禁用");
        }

        // 创建分类实体
        Category category = new Category();
        BeanUtils.copyProperties(saveDTO, category);

        // 保存分类
        categoryMapper.insert(category);

        // 转换为VO返回
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        vo.setBranchName(branch.getName());

        log.info("新增分类成功：{}", category.getName());
        return vo;
    }

    /**
     * 修改分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CategoryUpdateDTO updateDTO) {
        // 查询分类
        Category category = categoryMapper.selectById(updateDTO.getId());
        if (category == null || category.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }

        // 如果修改分类名称，检查是否重复
        if (StringUtils.hasText(updateDTO.getName()) && !updateDTO.getName().equals(category.getName())) {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Category::getName, updateDTO.getName());
            queryWrapper.eq(Category::getType, category.getType());
            queryWrapper.eq(Category::getBranchId, category.getBranchId());
            Category existCategory = categoryMapper.selectOne(queryWrapper);
            if (existCategory != null && !existCategory.getId().equals(updateDTO.getId())) {
                throw new BusinessException("同分店同类型分类名称已存在");
            }
        }

        // 如果分类状态修改为禁用，需要禁用关联的菜品/套餐
        if (updateDTO.getStatus() != null && updateDTO.getStatus() == 0 && category.getStatus() == 1) {
            if (category.getType() == 1) {
                // 禁用关联的菜品
                LambdaUpdateWrapper<Dish> dishWrapper = new LambdaUpdateWrapper<>();
                dishWrapper.eq(Dish::getCategoryId, updateDTO.getId());
                dishWrapper.set(Dish::getStatus, 0);
                dishWrapper.set(Dish::getUpdateUser, BaseContext.getCurrentId());
                dishMapper.update(null, dishWrapper);
            } else if (category.getType() == 2) {
                // 禁用关联的套餐
                LambdaUpdateWrapper<Setmeal> setmealWrapper = new LambdaUpdateWrapper<>();
                setmealWrapper.eq(Setmeal::getCategoryId, updateDTO.getId());
                setmealWrapper.set(Setmeal::getStatus, 0);
                setmealWrapper.set(Setmeal::getUpdateUser, BaseContext.getCurrentId());
                setmealMapper.update(null, setmealWrapper);
            }
        }

        // 更新分类信息
        Category updateCategory = new Category();
        BeanUtils.copyProperties(updateDTO, updateCategory);
        categoryMapper.updateById(updateCategory);

        log.info("修改分类成功，分类ID：{}", updateDTO.getId());
    }

    /**
     * 删除分类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 查询分类
        Category category = categoryMapper.selectById(id);
        if (category == null || category.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }

        // 检查分类是否关联菜品/套餐
        if (category.getType() == 1) {
            LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
            dishWrapper.eq(Dish::getCategoryId, id);
            dishWrapper.eq(Dish::getIsDeleted, 0);
            Long dishCount = dishMapper.selectCount(dishWrapper);
            if (dishCount > 0) {
                throw new BusinessException("分类关联菜品，不可删除");
            }
        } else if (category.getType() == 2) {
            LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
            setmealWrapper.eq(Setmeal::getCategoryId, id);
            setmealWrapper.eq(Setmeal::getIsDeleted, 0);
            Long setmealCount = setmealMapper.selectCount(setmealWrapper);
            if (setmealCount > 0) {
                throw new BusinessException("分类关联套餐，不可删除");
            }
        }

        // 软删除分类
        // 使用 deleteById 触发 MyBatis-Plus 的逻辑删除机制
        categoryMapper.deleteById(id);

        log.info("删除分类成功，分类ID：{}", id);
    }

    /**
     * 批量操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperation(CategoryBatchDTO batchDTO) {
        List<Long> ids = batchDTO.getIds();
        String operation = batchDTO.getOperation();

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("分类ID列表不能为空");
        }

        // 执行批量操作
        switch (operation) {
            case "enable":
                // 批量启用
                LambdaUpdateWrapper<Category> enableWrapper = new LambdaUpdateWrapper<>();
                enableWrapper.in(Category::getId, ids);
                enableWrapper.set(Category::getStatus, 1);
                enableWrapper.set(Category::getUpdateUser, BaseContext.getCurrentId());
                categoryMapper.update(null, enableWrapper);
                log.info("批量启用{}个分类成功", ids.size());
                break;

            case "disable":
                // 批量禁用
                LambdaUpdateWrapper<Category> disableWrapper = new LambdaUpdateWrapper<>();
                disableWrapper.in(Category::getId, ids);
                disableWrapper.set(Category::getStatus, 0);
                disableWrapper.set(Category::getUpdateUser, BaseContext.getCurrentId());
                categoryMapper.update(null, disableWrapper);
                log.info("批量禁用{}个分类成功", ids.size());
                break;

            default:
                throw new BusinessException("不支持的操作类型：" + operation);
        }
    }

    /**
     * 拖拽排序
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sort(CategorySortDTO sortDTO) {
        List<CategorySortDTO.SortItem> sortList = sortDTO.getSortList();
        if (sortList == null || sortList.isEmpty()) {
            throw new BusinessException("排序列表不能为空");
        }

        // 批量更新排序值
        for (CategorySortDTO.SortItem item : sortList) {
            Category category = new Category();
            category.setId(item.getId());
            category.setSort(item.getSort());
            category.setUpdateUser(BaseContext.getCurrentId());
            categoryMapper.updateById(category);
        }

        log.info("分类排序成功，共{}个分类", sortList.size());
    }
}

