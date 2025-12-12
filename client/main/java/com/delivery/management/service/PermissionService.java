package com.delivery.management.service;

import com.delivery.management.dto.PermissionVO;
import java.util.List;

/**
 * 权限服务接口
 * 
 * @author system
 * @date 2025-01-15
 */
public interface PermissionService {

    /**
     * 获取权限树
     * 
     * @return 权限树列表
     */
    List<PermissionVO> getPermissionTree();
}
