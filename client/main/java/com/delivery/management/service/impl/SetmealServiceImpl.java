package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.common.utils.ExcelUtil;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Branch;
import com.delivery.management.entity.Category;
import com.delivery.management.entity.Dish;
import com.delivery.management.entity.Setmeal;
import com.delivery.management.entity.SetmealDish;
import com.delivery.management.mapper.BranchMapper;
import com.delivery.management.mapper.CategoryMapper;
import com.delivery.management.mapper.DishMapper;
import com.delivery.management.mapper.SetmealDishMapper;
import com.delivery.management.mapper.SetmealMapper;
import com.delivery.management.service.SetmealService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private BranchMapper branchMapper;

    /**
     * 分页查询套餐列表
     */
    @Override
    public PageInfo<SetmealVO> pageQuery(SetmealPageQueryDTO queryDTO) {
        // 设置分页参数
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        // 分类ID
        if (queryDTO.getCategoryId() != null) {
            queryWrapper.eq(Setmeal::getCategoryId, queryDTO.getCategoryId());
        }

        // 售卖状态
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Setmeal::getStatus, queryDTO.getStatus());
        }

        // 分店ID
        if (queryDTO.getBranchId() != null) {
            queryWrapper.eq(Setmeal::getBranchId, queryDTO.getBranchId());
        }

        // 套餐名称模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            queryWrapper.like(Setmeal::getName, queryDTO.getName());
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Setmeal::getCreateTime);

        // 执行查询
        List<Setmeal> setmealList = setmealMapper.selectList(queryWrapper);

        // 转换为VO并查询关联菜品
        List<SetmealVO> voList = setmealList.stream().map(setmeal -> {
            SetmealVO vo = new SetmealVO();
            BeanUtils.copyProperties(setmeal, vo);

            // 查询分类名称
            Category category = categoryMapper.selectById(setmeal.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }

            // 查询分店名称
            if (setmeal.getBranchId() != null) {
                Branch branch = branchMapper.selectById(setmeal.getBranchId());
                if (branch != null) {
                    vo.setBranchName(branch.getName());
                }
            }

            // 查询套餐菜品关系

            // 查询关联菜品列表
            LambdaQueryWrapper<SetmealDish> sdWrapper = new LambdaQueryWrapper<>();
            sdWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
            sdWrapper.orderByAsc(SetmealDish::getSort);
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(sdWrapper);
            List<SetmealVO.SetmealDishVO> dishVOs = setmealDishes.stream().map(sd -> {
                SetmealVO.SetmealDishVO dishVO = new SetmealVO.SetmealDishVO();
                BeanUtils.copyProperties(sd, dishVO);
                return dishVO;
            }).collect(Collectors.toList());
            vo.setSetmealDishes(dishVOs);

            return vo;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageInfo<Setmeal> setmealPageInfo = new PageInfo<>(setmealList);
        PageInfo<SetmealVO> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(setmealPageInfo, pageInfo);
        pageInfo.setList(voList);
        return pageInfo;
    }

    /**
     * 新增套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SetmealVO save(SetmealSaveDTO saveDTO) {
        // 检查同分店套餐名称是否已存在
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getName, saveDTO.getName());
        queryWrapper.eq(Setmeal::getBranchId, saveDTO.getBranchId());
        Setmeal existSetmeal = setmealMapper.selectOne(queryWrapper);
        if (existSetmeal != null) {
            throw new BusinessException("同分店套餐名称已存在");
        }

        // 验证分类是否存在
        Category category = categoryMapper.selectById(saveDTO.getCategoryId());
        if (category == null || category.getStatus() == 0 || category.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在或已禁用");
        }

        // 验证关联菜品是否存在并计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SetmealSaveDTO.SetmealDishDTO dishDTO : saveDTO.getSetmealDishes()) {
            Dish dish = dishMapper.selectById(dishDTO.getDishId());
            if (dish == null || dish.getIsDeleted() == 1) {
                throw new BusinessException("菜品不存在，ID：" + dishDTO.getDishId());
            }
            if (dish.getStatus() == 0) {
                throw new BusinessException("菜品已停售，ID：" + dishDTO.getDishId());
            }
            totalPrice = totalPrice.add(dishDTO.getPrice().multiply(BigDecimal.valueOf(dishDTO.getCopies())));
        }

        // 验证套餐总价是否小于等于菜品总价
        if (saveDTO.getPrice().compareTo(totalPrice) > 0) {
            throw new BusinessException("套餐总价必须小于等于关联菜品总价之和");
        }

        // 创建套餐实体
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(saveDTO, setmeal);

        // 保存套餐
        setmealMapper.insert(setmeal);

        // 保存关联菜品
        List<SetmealDish> setmealDishes = saveDTO.getSetmealDishes().stream().map(dishDTO -> {
            Dish dish = dishMapper.selectById(dishDTO.getDishId());
            SetmealDish sd = new SetmealDish();
            sd.setSetmealId(setmeal.getId());
            sd.setDishId(dishDTO.getDishId());
            sd.setName(dish.getName());
            sd.setPrice(dishDTO.getPrice());
            sd.setCopies(dishDTO.getCopies());
            sd.setSort(dishDTO.getSort() != null ? dishDTO.getSort() : 0);
            return sd;
        }).collect(Collectors.toList());

        setmealDishMapper.insertBatch(setmealDishes);

        // 转换为VO返回
        SetmealVO vo = new SetmealVO();
        BeanUtils.copyProperties(setmeal, vo);
        vo.setCategoryName(category.getName());

        log.info("新增套餐成功：{}", setmeal.getName());
        return vo;
    }

    /**
     * 修改套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SetmealUpdateDTO updateDTO) {
        // 查询套餐
        Setmeal setmeal = setmealMapper.selectById(updateDTO.getId());
        if (setmeal == null || setmeal.getIsDeleted() == 1) {
            throw new BusinessException("套餐不存在");
        }

        // 如果修改套餐名称，检查是否重复
        if (StringUtils.hasText(updateDTO.getName()) && !updateDTO.getName().equals(setmeal.getName())) {
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getName, updateDTO.getName());
            queryWrapper.eq(Setmeal::getBranchId, setmeal.getBranchId());
            Setmeal existSetmeal = setmealMapper.selectOne(queryWrapper);
            if (existSetmeal != null && !existSetmeal.getId().equals(updateDTO.getId())) {
                throw new BusinessException("同分店套餐名称已存在");
            }
        }

        // 如果修改了关联菜品，重新计算总价
        if (updateDTO.getSetmealDishes() != null && !updateDTO.getSetmealDishes().isEmpty()) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (SetmealSaveDTO.SetmealDishDTO dishDTO : updateDTO.getSetmealDishes()) {
                Dish dish = dishMapper.selectById(dishDTO.getDishId());
                if (dish == null || dish.getIsDeleted() == 1) {
                    throw new BusinessException("菜品不存在，ID：" + dishDTO.getDishId());
                }
                totalPrice = totalPrice.add(dishDTO.getPrice().multiply(BigDecimal.valueOf(dishDTO.getCopies())));
            }

            BigDecimal finalPrice = updateDTO.getPrice() != null ? updateDTO.getPrice() : setmeal.getPrice();
            if (finalPrice.compareTo(totalPrice) > 0) {
                throw new BusinessException("套餐总价必须小于等于关联菜品总价之和");
            }
        }

        // 更新套餐信息
        Setmeal updateSetmeal = new Setmeal();
        BeanUtils.copyProperties(updateDTO, updateSetmeal);
        setmealMapper.updateById(updateSetmeal);

        // 如果修改了关联菜品，更新关联关系
        if (updateDTO.getSetmealDishes() != null && !updateDTO.getSetmealDishes().isEmpty()) {
            // 删除原有关联
            setmealDishMapper.deleteBySetmealId(updateDTO.getId());

            // 保存新的关联
            List<SetmealDish> setmealDishes = updateDTO.getSetmealDishes().stream().map(dishDTO -> {
                Dish dish = dishMapper.selectById(dishDTO.getDishId());
                SetmealDish sd = new SetmealDish();
                sd.setSetmealId(updateDTO.getId());
                sd.setDishId(dishDTO.getDishId());
                sd.setName(dish.getName());
                sd.setPrice(dishDTO.getPrice());
                sd.setCopies(dishDTO.getCopies());
                sd.setSort(dishDTO.getSort() != null ? dishDTO.getSort() : 0);
                return sd;
            }).collect(Collectors.toList());

            setmealDishMapper.insertBatch(setmealDishes);
        }

        log.info("修改套餐成功，套餐ID：{}", updateDTO.getId());
    }

    /**
     * 删除套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 查询套餐
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal == null) {
            throw new BusinessException("套餐不存在");
        }

        // 检查套餐是否关联未完成订单（这里需要OrderDetailMapper，暂时先不检查）

        // 软删除套餐（MyBatis-Plus会自动处理逻辑删除）
        setmealMapper.deleteById(id);

        log.info("删除套餐成功，套餐ID：{}", id);
    }

    /**
     * 批量操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperation(SetmealBatchDTO batchDTO) {
        List<Long> ids = batchDTO.getIds();
        String operation = batchDTO.getOperation();

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("套餐ID列表不能为空");
        }

        // 执行批量操作
        switch (operation) {
            case "enable":
                // 批量起售
                LambdaUpdateWrapper<Setmeal> enableWrapper = new LambdaUpdateWrapper<>();
                enableWrapper.in(Setmeal::getId, ids);
                enableWrapper.set(Setmeal::getStatus, 1);
                enableWrapper.set(Setmeal::getUpdateUser, BaseContext.getCurrentId());
                setmealMapper.update(null, enableWrapper);
                log.info("批量起售{}个套餐成功", ids.size());
                break;

            case "disable":
                // 批量停售
                LambdaUpdateWrapper<Setmeal> disableWrapper = new LambdaUpdateWrapper<>();
                disableWrapper.in(Setmeal::getId, ids);
                disableWrapper.set(Setmeal::getStatus, 0);
                disableWrapper.set(Setmeal::getUpdateUser, BaseContext.getCurrentId());
                setmealMapper.update(null, disableWrapper);
                log.info("批量停售{}个套餐成功", ids.size());
                break;

            default:
                throw new BusinessException("不支持的操作类型：" + operation);
        }
    }

    /**
     * 导出套餐
     */
    @Override
    public void exportSetmeal(SetmealPageQueryDTO queryDTO, HttpServletResponse response) {
        // 查询所有符合条件的套餐（不分页）
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(Integer.MAX_VALUE);
        PageInfo<SetmealVO> pageInfo = pageQuery(queryDTO);
        List<SetmealVO> setmealList = pageInfo.getList();

        // 转换为Excel DTO
        List<SetmealExcelDTO> excelList = new ArrayList<>();
        for (SetmealVO setmeal : setmealList) {
            SetmealExcelDTO excelDTO = new SetmealExcelDTO();
            excelDTO.setName(setmeal.getName());
            excelDTO.setCategoryName(setmeal.getCategoryName());
            excelDTO.setPrice(setmeal.getPrice());
            excelDTO.setDescription(setmeal.getDescription());
            excelDTO.setStatus(setmeal.getStatus() == 1 ? "起售" : "停售");
            
            // 拼接关联菜品
            if (setmeal.getSetmealDishes() != null && !setmeal.getSetmealDishes().isEmpty()) {
                String dishes = setmeal.getSetmealDishes().stream()
                        .map(sd -> sd.getName() + " x" + sd.getCopies())
                        .collect(Collectors.joining("，"));
                excelDTO.setDishes(dishes);
            }
            
            excelList.add(excelDTO);
        }

        // 导出Excel
        String fileName = "套餐列表_" + System.currentTimeMillis();
        ExcelUtil.export(response, fileName, "套餐列表", excelList, SetmealExcelDTO.class);
    }

    /**
     * 恢复套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        // 查询已删除的套餐（忽略逻辑删除状态）
        Setmeal setmeal = setmealMapper.selectByIdIgnoreDeleted(id);
        if (setmeal == null) {
            throw new BusinessException("套餐不存在");
        }

        // 检查套餐是否已恢复
        if (setmeal.getIsDeleted() == 0) {
            throw new BusinessException("套餐未被删除，无需恢复");
        }

        // 检查分类是否存在且启用
        Category category = categoryMapper.selectById(setmeal.getCategoryId());
        if (category == null || category.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在");
        }
        if (category.getStatus() == 0) {
            throw new BusinessException("分类已禁用，无法恢复套餐");
        }

        // 检查关联菜品是否存在且未删除
        LambdaQueryWrapper<SetmealDish> sdWrapper = new LambdaQueryWrapper<>();
        sdWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(sdWrapper);
        for (SetmealDish setmealDish : setmealDishes) {
            Dish dish = dishMapper.selectById(setmealDish.getDishId());
            if (dish == null || dish.getIsDeleted() == 1) {
                throw new BusinessException("套餐关联的菜品不存在，ID：" + setmealDish.getDishId());
            }
        }

        // 恢复套餐
        setmealMapper.restoreSetmeal(id, BaseContext.getCurrentId(), java.time.LocalDateTime.now());

        log.info("恢复套餐成功，套餐ID：{}", id);
    }

    /**
     * 物理删除套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermanently(Long id) {
        // 查询已删除的套餐（忽略逻辑删除状态）
        Setmeal setmeal = setmealMapper.selectByIdIgnoreDeleted(id);
        if (setmeal == null) {
            throw new BusinessException("套餐不存在");
        }

        // 检查套餐是否已删除
        if (setmeal.getIsDeleted() == 0) {
            throw new BusinessException("套餐未被删除，无法物理删除");
        }

        // 检查套餐是否关联未完成订单（这里需要OrderDetailMapper，暂时先不检查）

        // 删除套餐关联的菜品
        setmealDishMapper.deleteBySetmealId(id);

        // 物理删除套餐
        setmealMapper.deletePermanently(id);

        log.info("物理删除套餐成功，套餐ID：{}", id);
    }
}

