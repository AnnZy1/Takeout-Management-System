package com.delivery.management.controller;

import com.delivery.management.common.result.Result;
import com.delivery.management.dto.PermissionVO;
import com.delivery.management.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理员权限控制器
 * 
 * @author system
 * @date 2025-01-15
 */
@Slf4j
@RestController
@RequestMapping("/admin/permission")
public class AdminPermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限树
     * 
     * @return 权限树
     */
    @GetMapping("/tree")
    public Result<List<PermissionVO>> getPermissionTree() {
        log.info("获取权限树");
        List<PermissionVO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }
}
