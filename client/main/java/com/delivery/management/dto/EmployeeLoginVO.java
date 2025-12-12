package com.delivery.management.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 员工登录响应VO
 * 
 * @author system
 * @date 2025-01-15
 */
@Data
public class EmployeeLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfo userInfo;

    @Data
    public static class UserInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 员工ID
         */
        private Long id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 员工姓名
         */
        private String name;

        /**
         * 角色ID
         */
        private Long roleId;

        /**
         * 角色名称
         */
        private String roleName;

        /**
         * 分店ID
         */
        private Long branchId;

        /**
         * 分店名称
         */
        private String branchName;

        /**
         * 按钮权限列表
         */
        private List<String> permissions;
    }
}

