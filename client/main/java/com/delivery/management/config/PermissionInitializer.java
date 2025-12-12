package com.delivery.management.config;

import com.delivery.management.entity.Permission;
import com.delivery.management.mapper.PermissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限数据初始化
 */
@Slf4j
@Component
public class PermissionInitializer implements CommandLineRunner {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(String... args) throws Exception {
        if (permissionMapper.selectCount(null) > 0) {
            return;
        }

        log.info("开始初始化权限数据...");

        // 1. 员工管理
        Permission p1 = createPermission(1L, "员工管理", 1, null, 1);
        permissionMapper.insert(p1);
        permissionMapper.insert(createPermission(11L, "查看员工", 2, 1L, 1));
        permissionMapper.insert(createPermission(12L, "添加员工", 2, 1L, 2));
        permissionMapper.insert(createPermission(13L, "修改员工", 2, 1L, 3));
        permissionMapper.insert(createPermission(14L, "删除员工", 2, 1L, 4));

        // 2. 分类管理
        Permission p2 = createPermission(2L, "分类管理", 1, null, 2);
        permissionMapper.insert(p2);
        permissionMapper.insert(createPermission(21L, "查看分类", 2, 2L, 1));
        permissionMapper.insert(createPermission(22L, "添加分类", 2, 2L, 2));
        permissionMapper.insert(createPermission(23L, "修改分类", 2, 2L, 3));
        permissionMapper.insert(createPermission(24L, "删除分类", 2, 2L, 4));

        // 3. 菜品管理
        Permission p3 = createPermission(3L, "菜品管理", 1, null, 3);
        permissionMapper.insert(p3);
        permissionMapper.insert(createPermission(31L, "查看菜品", 2, 3L, 1));
        permissionMapper.insert(createPermission(32L, "添加菜品", 2, 3L, 2));
        permissionMapper.insert(createPermission(33L, "修改菜品", 2, 3L, 3));
        permissionMapper.insert(createPermission(34L, "删除菜品", 2, 3L, 4));
        permissionMapper.insert(createPermission(35L, "启停售", 2, 3L, 5));

        // 4. 套餐管理
        Permission p4 = createPermission(4L, "套餐管理", 1, null, 4);
        permissionMapper.insert(p4);
        permissionMapper.insert(createPermission(41L, "查看套餐", 2, 4L, 1));
        permissionMapper.insert(createPermission(42L, "添加套餐", 2, 4L, 2));
        permissionMapper.insert(createPermission(43L, "修改套餐", 2, 4L, 3));
        permissionMapper.insert(createPermission(44L, "删除套餐", 2, 4L, 4));
        permissionMapper.insert(createPermission(45L, "启停售", 2, 4L, 5));

        // 5. 订单管理
        Permission p5 = createPermission(5L, "订单管理", 1, null, 5);
        permissionMapper.insert(p5);
        permissionMapper.insert(createPermission(51L, "查看订单", 2, 5L, 1));
        permissionMapper.insert(createPermission(52L, "订单处理", 2, 5L, 2));

        // 6. 分店管理
        Permission p6 = createPermission(6L, "分店管理", 1, null, 6);
        permissionMapper.insert(p6);
        permissionMapper.insert(createPermission(61L, "查看分店", 2, 6L, 1));
        permissionMapper.insert(createPermission(62L, "添加分店", 2, 6L, 2));
        permissionMapper.insert(createPermission(63L, "修改分店", 2, 6L, 3));
        permissionMapper.insert(createPermission(64L, "删除分店", 2, 6L, 4));

        log.info("权限数据初始化完成");
    }

    private Permission createPermission(Long id, String name, int type, Long parentId, int sort) {
        Permission p = new Permission();
        p.setId(id);
        p.setName(name);
        p.setType(type);
        p.setParentId(parentId);
        p.setSort(sort);
        p.setStatus(1);
        return p;
    }
}
