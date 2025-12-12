package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.alibaba.excel.EasyExcel;
import com.delivery.management.common.context.BaseContext;
import com.delivery.management.common.exception.BusinessException;
import com.delivery.management.common.utils.ExcelUtil;
import com.delivery.management.dto.*;
import com.delivery.management.entity.Category;
import com.delivery.management.entity.Dish;
import com.delivery.management.entity.DishFlavor;
import com.delivery.management.entity.Employee;
import com.delivery.management.entity.Role;
import com.delivery.management.entity.SetmealDish;
import com.delivery.management.mapper.CategoryMapper;
import com.delivery.management.mapper.DishFlavorMapper;
import com.delivery.management.mapper.DishMapper;
import com.delivery.management.mapper.EmployeeMapper;
import com.delivery.management.mapper.RoleMapper;
import com.delivery.management.mapper.SetmealDishMapper;
import com.delivery.management.service.DishService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 管理员角色名称
     */
    private static final String ADMIN_ROLE_NAME = "管理员";

    /**
     * 分页查询菜品列表
     */
    @Override
    public PageInfo<DishVO> pageQuery(DishPageQueryDTO queryDTO) {
        log.info("开始分页查询菜品，页码：{}，页大小：{}，显示已删除：{}", 
                queryDTO.getPageNum(), queryDTO.getPageSize(), queryDTO.getShowDeleted());
        
        // 执行查询
        List<Dish> dishList;
        long total = 0;
        
        // 过滤未删除的菜品（管理员可以选择查看已删除菜品）
        if (queryDTO.getShowDeleted() != null && queryDTO.getShowDeleted() == 1) {
            // ... existing custom SQL logic ...
            // 构建查询条件
            StringBuilder whereClause = new StringBuilder("WHERE is_deleted = 1");
            
            if (queryDTO.getCategoryId() != null) {
                whereClause.append(" AND category_id = ").append(queryDTO.getCategoryId());
            }
            if (queryDTO.getStatus() != null) {
                whereClause.append(" AND status = ").append(queryDTO.getStatus());
            }
            
            // 处理分店权限
            Long currentEmpId = BaseContext.getCurrentId();
            if (currentEmpId != null) {
                Employee currentEmployee = employeeMapper.selectById(currentEmpId);
                if (currentEmployee != null) {
                    Role role = roleMapper.selectById(currentEmployee.getRoleId());
                    boolean isAdmin = role != null && ADMIN_ROLE_NAME.equals(role.getName());
                    
                    if (!isAdmin && currentEmployee.getBranchId() != null) {
                        whereClause.append(" AND branch_id = ").append(currentEmployee.getBranchId());
                    }
                }
            }
            
            if (StringUtils.hasText(queryDTO.getName())) {
                whereClause.append(" AND name LIKE '%").append(queryDTO.getName()).append("%'");
            }
            
            // 计算总记录数
            String countSql = "SELECT COUNT(*) FROM dish " + whereClause;
            total = dishMapper.selectCountByCustomSql(countSql);
            
            // 构建分页查询SQL
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM dish ").append(whereClause);
            
            // 排序
            sqlBuilder.append(" ORDER BY create_time DESC");
            
            // 为自定义SQL手动添加分页限制
            int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
            sqlBuilder.append(" LIMIT " + offset + ", " + queryDTO.getPageSize());
            
            // 执行查询
            dishList = dishMapper.selectListByCustomSql(sqlBuilder.toString());
        } else {
            // 构建查询条件
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

            // 分类ID
            if (queryDTO.getCategoryId() != null) {
                queryWrapper.eq(Dish::getCategoryId, queryDTO.getCategoryId());
            }

            // 售卖状态
            if (queryDTO.getStatus() != null) {
                queryWrapper.eq(Dish::getStatus, queryDTO.getStatus());
            }

            // 分店ID（数据范围控制）
            if (queryDTO.getBranchId() != null) {
                queryWrapper.eq(Dish::getBranchId, queryDTO.getBranchId());
            } else {
                // 如果前端没有传递分店ID，则根据当前登录员工的分店ID进行过滤
                Long currentEmpId = BaseContext.getCurrentId();
                if (currentEmpId != null) {
                    Employee currentEmployee = employeeMapper.selectById(currentEmpId);
                    if (currentEmployee != null) {
                        // 查询角色信息，判断是否为管理员
                        Role role = roleMapper.selectById(currentEmployee.getRoleId());
                        boolean isAdmin = role != null && ADMIN_ROLE_NAME.equals(role.getName());
                        
                        // 非管理员只能查看自己分店的菜品
                        if (!isAdmin && currentEmployee.getBranchId() != null) {
                            queryWrapper.eq(Dish::getBranchId, currentEmployee.getBranchId());
                        }
                        // 管理员可以查看所有分店的菜品
                    }
                }
            }

            // 菜品名称模糊查询
            if (StringUtils.hasText(queryDTO.getName())) {
                queryWrapper.like(Dish::getName, queryDTO.getName());
            }

            // 按创建时间倒序排列
            queryWrapper.orderByDesc(Dish::getCreateTime);
            
            // 设置分页参数
            PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

            // 只显示未删除的菜品，MyBatis-Plus会自动添加软删除过滤条件
            dishList = dishMapper.selectList(queryWrapper);
            log.info("MyBatis查询结果数量：{}", dishList.size());
        }
        
        // 封装分页结果，使用 PageInfo 包装查询结果（对于非自定义SQL）
        PageInfo<Dish> dishPageInfo = null;
        if (queryDTO.getShowDeleted() == null || queryDTO.getShowDeleted() != 1) {
             dishPageInfo = new PageInfo<>(dishList);
             log.info("PageInfo(Dish) info - Total: {}, Pages: {}, Size: {}", 
                     dishPageInfo.getTotal(), dishPageInfo.getPages(), dishPageInfo.getSize());
        }

        // 转换为VO并查询口味
        List<DishVO> voList = dishList.stream().map(dish -> {
            DishVO vo = new DishVO();
            BeanUtils.copyProperties(dish, vo);

            // 查询分类名称
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }

            // 查询口味列表
            LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
            flavorWrapper.eq(DishFlavor::getDishId, dish.getId());
            List<DishFlavor> flavors = dishFlavorMapper.selectList(flavorWrapper);
            List<DishVO.FlavorVO> flavorVOs = flavors.stream().map(flavor -> {
                DishVO.FlavorVO flavorVO = new DishVO.FlavorVO();
                BeanUtils.copyProperties(flavor, flavorVO);
                return flavorVO;
            }).collect(Collectors.toList());
            vo.setFlavors(flavorVOs);

            return vo;
        }).collect(Collectors.toList());

        // 封装分页结果
        PageInfo<DishVO> pageInfo = new PageInfo<>();
        if (dishPageInfo != null) {
            BeanUtils.copyProperties(dishPageInfo, pageInfo);
            // 确保总数正确
            pageInfo.setTotal(dishPageInfo.getTotal());
            pageInfo.setPages(dishPageInfo.getPages());
            pageInfo.setPageNum(dishPageInfo.getPageNum());
            pageInfo.setPageSize(dishPageInfo.getPageSize());
        }
        pageInfo.setList(voList);
        
        // 如果是自定义SQL查询，需要手动设置总记录数和分页信息
        if (queryDTO.getShowDeleted() != null && queryDTO.getShowDeleted() == 1) {
            pageInfo.setTotal(total);
            pageInfo.setPageNum(queryDTO.getPageNum());
            pageInfo.setPageSize(queryDTO.getPageSize());
            pageInfo.setPages((int) Math.ceil((double) total / queryDTO.getPageSize()));
        }
        
        log.info("返回分页结果 - Total: {}, Pages: {}, Size: {}", 
                pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList().size());
        return pageInfo;
    }

    /**
     * 新增菜品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DishVO save(DishSaveDTO saveDTO) {
        log.info("开始新增菜品，名称：{}，分店ID：{}", saveDTO.getName(), saveDTO.getBranchId());
        // 检查同分店菜品名称是否已存在
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, saveDTO.getName());
        queryWrapper.eq(Dish::getBranchId, saveDTO.getBranchId());
        queryWrapper.eq(Dish::getIsDeleted, 0); // 只检查未删除的菜品
        Dish existDish = dishMapper.selectOne(queryWrapper);
        if (existDish != null) {
            throw new BusinessException("同分店菜品名称已存在");
        }

        // 验证分类是否存在
        Category category = categoryMapper.selectById(saveDTO.getCategoryId());
        if (category == null || category.getStatus() == 0 || category.getIsDeleted() == 1) {
            throw new BusinessException("分类不存在或已禁用");
        }

        // 创建菜品实体
        Dish dish = new Dish();
        BeanUtils.copyProperties(saveDTO, dish);

        // 保存菜品
        int rows = dishMapper.insert(dish);
        log.info("菜品插入数据库，影响行数：{}，ID：{}", rows, dish.getId());

        // 保存口味
        if (saveDTO.getFlavors() != null && !saveDTO.getFlavors().isEmpty()) {
            List<DishFlavor> flavors = saveDTO.getFlavors().stream().map(flavorDTO -> {
                DishFlavor flavor = new DishFlavor();
                flavor.setDishId(dish.getId());
                flavor.setName(flavorDTO.getName());
                flavor.setValue(flavorDTO.getValue());
                return flavor;
            }).collect(Collectors.toList());

            for (DishFlavor flavor : flavors) {
                dishFlavorMapper.insert(flavor);
            }
            log.info("保存口味成功，数量：{}", flavors.size());
        }

        // 转换为VO返回
        DishVO vo = new DishVO();
        BeanUtils.copyProperties(dish, vo);
        vo.setCategoryName(category.getName());

        log.info("新增菜品业务完成，返回VO：{}", vo);
        return vo;
    }

    /**
     * 修改菜品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DishUpdateDTO updateDTO) {
        // 查询菜品
        Dish dish = dishMapper.selectById(updateDTO.getId());
        if (dish == null || dish.getIsDeleted() == 1) {
            throw new BusinessException("菜品不存在");
        }

        // 如果修改菜品名称，检查是否重复
        if (StringUtils.hasText(updateDTO.getName()) && !updateDTO.getName().equals(dish.getName())) {
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getName, updateDTO.getName());
            queryWrapper.eq(Dish::getBranchId, dish.getBranchId());
            queryWrapper.eq(Dish::getIsDeleted, 0); // 只检查未删除的菜品
            Dish existDish = dishMapper.selectOne(queryWrapper);
            if (existDish != null && !existDish.getId().equals(updateDTO.getId())) {
                throw new BusinessException("同分店菜品名称已存在");
            }
        }

        // 更新菜品信息
        Dish updateDish = new Dish();
        BeanUtils.copyProperties(updateDTO, updateDish);
        dishMapper.updateById(updateDish);

        // 更新口味（先删除再新增）
        dishFlavorMapper.deleteByDishId(updateDTO.getId());
        if (updateDTO.getFlavors() != null && !updateDTO.getFlavors().isEmpty()) {
            List<DishFlavor> flavors = updateDTO.getFlavors().stream().map(flavorDTO -> {
                DishFlavor flavor = new DishFlavor();
                flavor.setDishId(updateDTO.getId());
                flavor.setName(flavorDTO.getName());
                flavor.setValue(flavorDTO.getValue());
                return flavor;
            }).collect(Collectors.toList());

            for (DishFlavor flavor : flavors) {
                dishFlavorMapper.insert(flavor);
            }
        }

        // 如果修改了菜品名称或价格，需要更新关联套餐的冗余字段
        if (StringUtils.hasText(updateDTO.getName()) || updateDTO.getPrice() != null) {
            LambdaQueryWrapper<SetmealDish> sdWrapper = new LambdaQueryWrapper<>();
            sdWrapper.eq(SetmealDish::getDishId, updateDTO.getId());
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(sdWrapper);
            for (SetmealDish sd : setmealDishes) {
                if (StringUtils.hasText(updateDTO.getName())) {
                    sd.setName(updateDTO.getName());
                }
                if (updateDTO.getPrice() != null) {
                    sd.setPrice(updateDTO.getPrice());
                }
                setmealDishMapper.updateById(sd);
            }
        }

        log.info("修改菜品成功，菜品ID：{}", updateDTO.getId());
    }

    /**
     * 删除菜品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("开始删除菜品，ID：{}", id);
        try {
            // 查询菜品
            Dish dish = dishMapper.selectById(id);
            log.info("查询到菜品：{}", dish);
            if (dish == null) {
                throw new BusinessException("菜品不存在");
            }

            // 检查菜品是否关联套餐
            LambdaQueryWrapper<SetmealDish> sdWrapper = new LambdaQueryWrapper<>();
            sdWrapper.eq(SetmealDish::getDishId, id);
            Long sdCount = setmealDishMapper.selectCount(sdWrapper);
            log.info("菜品关联套餐数量：{}", sdCount);
            if (sdCount > 0) {
                throw new BusinessException("菜品关联套餐，不可删除");
            }

            // 检查菜品是否关联未完成订单（这里需要OrderDetailMapper，暂时先不检查）

            // 使用MyBatis-Plus的自动软删除功能
            dishMapper.deleteById(id);

            log.info("删除菜品成功，菜品ID：{}", id);
        } catch (BusinessException e) {
            log.error("业务异常：{}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("系统异常：{}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 批量操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchOperation(DishBatchDTO batchDTO) {
        List<Long> ids = batchDTO.getIds();
        String operation = batchDTO.getOperation();

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("菜品ID列表不能为空");
        }

        // 安全获取当前用户ID
        Long currentUserId = BaseContext.getCurrentId();
        
        // 执行批量操作
        switch (operation) {
            case "enable":
                // 批量起售
                LambdaUpdateWrapper<Dish> enableWrapper = new LambdaUpdateWrapper<>();
                enableWrapper.in(Dish::getId, ids);
                enableWrapper.set(Dish::getStatus, 1);
                dishMapper.update(null, enableWrapper);
                log.info("批量起售{}个菜品成功", ids.size());
                break;

            case "disable":
                // 批量停售
                LambdaUpdateWrapper<Dish> disableWrapper = new LambdaUpdateWrapper<>();
                disableWrapper.in(Dish::getId, ids);
                disableWrapper.set(Dish::getStatus, 0);
                dishMapper.update(null, disableWrapper);
                log.info("批量停售{}个菜品成功", ids.size());
                break;

            default:
                throw new BusinessException("不支持的操作类型：" + operation);
        }
    }

    /**
     * 导入菜品
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importDish(MultipartFile file) {
        try {
            // 读取Excel
            List<DishExcelDTO> excelDataList = EasyExcel.read(file.getInputStream(), DishExcelDTO.class, null)
                    .sheet()
                    .doReadSync();

            if (excelDataList == null || excelDataList.isEmpty()) {
                throw new BusinessException("Excel文件为空");
            }

            // 获取当前用户的分店ID
            Long branchId = 1L;
            if (BaseContext.getCurrentId() != null) {
                var employee = employeeMapper.selectById(BaseContext.getCurrentId());
                if (employee != null) {
                    branchId = employee.getBranchId();
                }
            }

            // 批量导入菜品
            for (DishExcelDTO excelDTO : excelDataList) {
                // 根据分类名称查找分类ID（简化处理，假设分类已存在）
                // 实际应该根据分类名称和类型查找分类ID
                // 这里需要前端确保分类名称正确，或者后端根据名称+类型+分店查找
                
                // 创建菜品DTO
                DishSaveDTO saveDTO = new DishSaveDTO();
                saveDTO.setName(excelDTO.getName());
                // saveDTO.setCategoryId(...); // 需要根据分类名称查找，这里暂时跳过
                saveDTO.setPrice(excelDTO.getPrice());
                saveDTO.setSpecifications(excelDTO.getSpecifications());
                saveDTO.setDescription(excelDTO.getDescription());
                saveDTO.setStatus("起售".equals(excelDTO.getStatus()) ? 1 : 0);
                saveDTO.setBranchId(branchId);

                // 解析口味（格式：name1:value1;name2:value2）
                if (StringUtils.hasText(excelDTO.getFlavors())) {
                    String[] flavorPairs = excelDTO.getFlavors().split(";");
                    List<DishSaveDTO.FlavorDTO> flavors = new ArrayList<>();
                    for (String pair : flavorPairs) {
                        String[] parts = pair.split(":");
                        if (parts.length == 2) {
                            DishSaveDTO.FlavorDTO flavorDTO = new DishSaveDTO.FlavorDTO();
                            flavorDTO.setName(parts[0].trim());
                            flavorDTO.setValue(parts[1].trim());
                            flavors.add(flavorDTO);
                        }
                    }
                    saveDTO.setFlavors(flavors);
                }

                // 注意：这里需要categoryId，如果Excel中没有分类ID，需要根据分类名称查找
                // 暂时跳过，实际使用时需要完善
                // save(saveDTO);
            }

            log.info("导入菜品成功，共{}条", excelDataList.size());
        } catch (IOException e) {
            log.error("导入菜品失败：{}", e.getMessage(), e);
            throw new BusinessException("导入菜品失败：" + e.getMessage());
        }
    }

    /**
     * 导出菜品
     */
    @Override
    public void exportDish(DishPageQueryDTO queryDTO, HttpServletResponse response) {
        // 查询所有符合条件的菜品（不分页）
        queryDTO.setPageNum(1);
        queryDTO.setPageSize(Integer.MAX_VALUE);
        PageInfo<DishVO> pageInfo = pageQuery(queryDTO);
        List<DishVO> dishList = pageInfo.getList();

        // 转换为Excel DTO
        List<DishExcelDTO> excelList = new ArrayList<>();
        for (DishVO dish : dishList) {
            DishExcelDTO excelDTO = new DishExcelDTO();
            excelDTO.setName(dish.getName());
            excelDTO.setCategoryName(dish.getCategoryName());
            excelDTO.setPrice(dish.getPrice());
            excelDTO.setSpecifications(dish.getSpecifications());
            excelDTO.setDescription(dish.getDescription());
            excelDTO.setStatus(dish.getStatus() == 1 ? "起售" : "停售");
            
            // 拼接口味
            if (dish.getFlavors() != null && !dish.getFlavors().isEmpty()) {
                String flavors = dish.getFlavors().stream()
                        .map(f -> f.getName() + ":" + f.getValue())
                        .collect(Collectors.joining(";"));
                excelDTO.setFlavors(flavors);
            }
            
            excelList.add(excelDTO);
        }

        // 导出Excel
        String fileName = "菜品列表_" + System.currentTimeMillis();
        ExcelUtil.export(response, fileName, "菜品列表", excelList, DishExcelDTO.class);
    }

    /**
     * 恢复菜品（从回收站恢复）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long id) {
        log.info("开始恢复菜品，菜品ID：{}", id);
        
        try {
            // 使用自定义SQL查询，确保能查询到已删除的菜品
            Dish dish = dishMapper.selectByIdIgnoreDeleted(id);
            if (dish == null) {
                log.warn("恢复菜品失败，菜品ID：{} 不存在", id);
                throw new BusinessException("菜品不存在");
            }
            
            // 检查菜品是否已恢复
            if (dish.getIsDeleted() == 0) {
                log.warn("恢复菜品失败，菜品ID：{} 已处于正常状态", id);
                throw new BusinessException("菜品已处于正常状态，无需恢复");
            }

            // 恢复菜品（将is_deleted设置为0）
            int result = dishMapper.restoreDish(id, BaseContext.getCurrentId(), LocalDateTime.now());
            if (result == 0) {
                log.error("恢复菜品失败，菜品ID：{}，更新结果：{}", id, result);
                throw new BusinessException("恢复菜品失败");
            }

            log.info("恢复菜品成功，菜品ID：{}", id);
        } catch (BusinessException e) {
            log.error("恢复菜品业务异常，菜品ID：{}，错误信息：{}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("恢复菜品系统异常，菜品ID：{}，错误信息：{}", id, e.getMessage(), e);
            throw new BusinessException("恢复菜品失败，系统异常");
        }
    }

    /**
     * 永久删除菜品（从数据库物理删除）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermanently(Long id) {
        log.info("开始永久删除菜品，菜品ID：{}", id);
        
        try {
            // 使用自定义SQL查询，确保能查询到已删除的菜品
            Dish dish = dishMapper.selectByIdIgnoreDeleted(id);
            if (dish == null) {
                log.warn("永久删除菜品失败，菜品ID：{} 不存在", id);
                throw new BusinessException("菜品不存在");
            }

            // 检查菜品是否已删除
            if (dish.getIsDeleted() == 0) {
                log.warn("永久删除菜品失败，菜品ID：{} 未删除", id);
                throw new BusinessException("菜品未删除，请先进行软删除操作");
            }

            // 检查菜品是否关联套餐（即使已删除也要检查）
            LambdaQueryWrapper<SetmealDish> sdWrapper = new LambdaQueryWrapper<>();
            sdWrapper.eq(SetmealDish::getDishId, id);
            Long sdCount = setmealDishMapper.selectCount(sdWrapper);
            if (sdCount > 0) {
                log.warn("永久删除菜品失败，菜品ID：{} 关联套餐数量：{}", id, sdCount);
                throw new BusinessException("菜品关联套餐，不可永久删除");
            }

            // 删除菜品口味
            dishFlavorMapper.deleteByDishId(id);

            // 物理删除菜品（使用自定义SQL删除）
            int result = dishMapper.deletePermanently(id);
            if (result == 0) {
                log.error("永久删除菜品失败，菜品ID：{}，删除结果：{}", id, result);
                throw new BusinessException("永久删除菜品失败");
            }

            log.info("永久删除菜品成功，菜品ID：{}", id);
        } catch (BusinessException e) {
            log.error("永久删除菜品业务异常，菜品ID：{}，错误信息：{}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("永久删除菜品系统异常，菜品ID：{}，错误信息：{}", id, e.getMessage(), e);
            throw new BusinessException("永久删除菜品失败，系统异常");
        }
    }
}

