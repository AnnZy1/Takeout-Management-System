package com.delivery.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.delivery.management.dto.PermissionVO;
import com.delivery.management.entity.Permission;
import com.delivery.management.mapper.PermissionMapper;
import com.delivery.management.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 获取权限树
     */
    @Override
    public List<PermissionVO> getPermissionTree() {
        // 1. 查询所有启用状态的权限
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Permission::getStatus, 1);
        queryWrapper.orderByAsc(Permission::getSort);
        List<Permission> permissionList = permissionMapper.selectList(queryWrapper);

        // 2. 转换为VO列表
        List<PermissionVO> voList = permissionList.stream().map(p -> {
            PermissionVO vo = new PermissionVO();
            BeanUtils.copyProperties(p, vo);
            return vo;
        }).collect(Collectors.toList());

        // 3. 构建树形结构
        return buildTree(voList);
    }

    /**
     * 构建树形结构
     */
    private List<PermissionVO> buildTree(List<PermissionVO> list) {
        List<PermissionVO> tree = new ArrayList<>();
        Map<Long, PermissionVO> map = list.stream().collect(Collectors.toMap(PermissionVO::getId, p -> p));

        for (PermissionVO node : list) {
            if (node.getParentId() == null) {
                tree.add(node);
            } else {
                PermissionVO parent = map.get(node.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        }
        return tree;
    }
}
