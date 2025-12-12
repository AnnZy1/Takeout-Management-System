-- ============================================
-- 外卖管理系统数据库初始化脚本
-- 数据库版本: MySQL 8.0+
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 创建数据库
DROP DATABASE IF EXISTS `deliver_management`;
CREATE DATABASE `deliver_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `deliver_management`;

-- ============================================
-- 1. 分店表（branch）- 多分店管理表
-- ============================================
CREATE TABLE `branch` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分店ID',
    `name` VARCHAR(64) NOT NULL COMMENT '分店名称',
    `address` VARCHAR(255) NOT NULL COMMENT '分店地址',
    `contact_name` VARCHAR(32) NOT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(11) NOT NULL COMMENT '联系人手机号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '分店状态：1-启用 0-禁用',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标识：1-已删 0-未删',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `create_user` BIGINT NOT NULL COMMENT '创建人ID',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_branch_name` (`name`),
    KEY `idx_branch_status` (`status`),
    KEY `idx_branch_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分店表';

-- ============================================
-- 2. 角色表（role）- 权限控制核心表
-- ============================================
CREATE TABLE `role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `name` VARCHAR(32) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(128) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '角色状态：1-启用 0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `create_user` BIGINT NOT NULL COMMENT '创建人ID',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`name`),
    KEY `idx_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================
-- 3. 员工表（employee）- 核心管理端用户表
-- ============================================
CREATE TABLE `employee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `username` VARCHAR(32) NOT NULL COMMENT '用户名',
    `password` VARCHAR(64) NOT NULL COMMENT '密码（BCrypt加密）',
    `name` VARCHAR(32) NOT NULL COMMENT '员工姓名',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `sex` VARCHAR(2) DEFAULT NULL COMMENT '性别：男/女/未知',
    `id_number` VARCHAR(18) DEFAULT NULL COMMENT '身份证号（加密存储）',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `branch_id` BIGINT NOT NULL COMMENT '分店ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常 0-锁定',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标识：1-已删 0-未删',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `create_user` BIGINT NOT NULL COMMENT '创建人ID',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_username` (`username`),
    UNIQUE KEY `uk_emp_phone` (`phone`),
    KEY `idx_emp_role_branch_status` (`role_id`, `branch_id`, `status`),
    KEY `idx_emp_deleted` (`is_deleted`),
    CONSTRAINT `fk_emp_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_emp_branch` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工表';

-- ============================================
-- 4. 权限表（permission）- 菜单/按钮权限表
-- ============================================
CREATE TABLE `permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `name` VARCHAR(32) NOT NULL COMMENT '权限名称',
    `type` TINYINT NOT NULL COMMENT '权限类型：1-菜单权限 2-按钮权限',
    `path` VARCHAR(64) DEFAULT NULL COMMENT '路由路径/按钮标识',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父权限ID',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '权限状态：1-启用 0-禁用',
    PRIMARY KEY (`id`),
    KEY `idx_permission_type` (`type`),
    KEY `idx_permission_parent` (`parent_id`),
    KEY `idx_permission_status` (`status`),
    CONSTRAINT `fk_permission_parent` FOREIGN KEY (`parent_id`) REFERENCES `permission` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ============================================
-- 5. 角色权限关联表（role_permission）- 多对多关联
-- ============================================
CREATE TABLE `role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================
-- 6. 分类表（category）- 菜品/套餐分类
-- ============================================
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(32) NOT NULL COMMENT '分类名称',
    `type` TINYINT NOT NULL COMMENT '分类类型：1-菜品分类 2-套餐分类',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '分类状态：1-启用 0-禁用',
    `branch_id` BIGINT NOT NULL COMMENT '分店ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标识：1-已删 0-未删',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `create_user` BIGINT NOT NULL COMMENT '创建人ID',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cat_name_type_branch` (`name`, `type`, `branch_id`),
    KEY `idx_cat_type_status_branch` (`type`, `status`, `branch_id`),
    KEY `idx_cat_deleted` (`is_deleted`),
    CONSTRAINT `fk_cat_branch` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ============================================
-- 7. 菜品表（dish）- 核心商品表
-- ============================================
CREATE TABLE `dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
    `name` VARCHAR(32) NOT NULL COMMENT '菜品名称',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '基础单价',
    `specifications` JSON DEFAULT NULL COMMENT '规格配置',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '菜品图片地址（OSS）',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '菜品描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '售卖状态：1-起售 0-停售',
    `branch_id` BIGINT NOT NULL COMMENT '分店ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标识：1-已删 0-未删',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `create_user` BIGINT NOT NULL COMMENT '创建人ID',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dish_name_branch` (`name`, `branch_id`),
    KEY `idx_dish_cat_status_branch` (`category_id`, `status`, `branch_id`),
    KEY `idx_dish_deleted` (`is_deleted`),
    CONSTRAINT `fk_dish_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_dish_branch` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品表';

-- ============================================
-- 8. 菜品口味表（dish_flavor）- 菜品多口味配置
-- ============================================
CREATE TABLE `dish_flavor` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '口味ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `name` VARCHAR(32) NOT NULL COMMENT '口味名称',
    `value` VARCHAR(255) NOT NULL COMMENT '口味值',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    KEY `idx_flavor_dish` (`dish_id`),
    CONSTRAINT `fk_flavor_dish` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜品口味表';

-- ============================================
-- 9. 套餐表（setmeal）- 组合商品表
-- ============================================
CREATE TABLE `setmeal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
    `name` VARCHAR(32) NOT NULL COMMENT '套餐名称',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `price` DECIMAL(10,2) NOT NULL COMMENT '套餐总价',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '套餐图片地址（OSS）',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '套餐描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '售卖状态：1-起售 0-停售',
    `branch_id` BIGINT NOT NULL COMMENT '分店ID',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标识：1-已删 0-未删',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `create_user` BIGINT NOT NULL COMMENT '创建人ID',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_setmeal_name_branch` (`name`, `branch_id`),
    KEY `idx_setmeal_cat_status_branch` (`category_id`, `status`, `branch_id`),
    KEY `idx_setmeal_deleted` (`is_deleted`),
    CONSTRAINT `fk_setmeal_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_setmeal_branch` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐表';

-- ============================================
-- 10. 套餐菜品关系表（setmeal_dish）- 多对多关联
-- ============================================
CREATE TABLE `setmeal_dish` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `setmeal_id` BIGINT NOT NULL COMMENT '套餐ID',
    `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
    `name` VARCHAR(32) NOT NULL COMMENT '菜品名称（冗余）',
    `price` DECIMAL(10,2) NOT NULL COMMENT '菜品单价（冗余）',
    `copies` INT NOT NULL COMMENT '菜品份数',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    PRIMARY KEY (`id`),
    KEY `idx_sd_setmeal` (`setmeal_id`),
    KEY `idx_sd_dish` (`dish_id`),
    CONSTRAINT `fk_sd_setmeal` FOREIGN KEY (`setmeal_id`) REFERENCES `setmeal` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_sd_dish` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐菜品关系表';

-- ============================================
-- 11. 订单表（orders）- 核心业务表
-- ============================================
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `number` VARCHAR(50) NOT NULL COMMENT '订单号',
    `status` TINYINT NOT NULL COMMENT '订单状态：1-待付款 2-待接单 3-已接单 4-派送中 5-已完成 6-已取消',
    `branch_id` BIGINT NOT NULL COMMENT '分店ID',
    `consignee` VARCHAR(32) NOT NULL COMMENT '收货人',
    `phone` VARCHAR(11) NOT NULL COMMENT '收货人手机号',
    `address` VARCHAR(255) NOT NULL COMMENT '详细地址',
    `order_time` DATETIME NOT NULL COMMENT '下单时间',
    `checkout_time` DATETIME DEFAULT NULL COMMENT '付款时间',
    `pay_method` TINYINT DEFAULT NULL COMMENT '支付方式：1-微信支付 2-支付宝支付',
    `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付 1-已支付 2-退款',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `pack_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '打包费',
    `tableware_number` INT NOT NULL DEFAULT 0 COMMENT '餐具数量',
    `tableware_status` TINYINT NOT NULL DEFAULT 1 COMMENT '餐具状态：1-按餐量提供 0-自定义数量',
    `remark` VARCHAR(100) DEFAULT NULL COMMENT '订单备注',
    `cancel_reason` VARCHAR(255) DEFAULT NULL COMMENT '取消原因',
    `rejection_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒单原因',
    `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
    `estimated_delivery_time` DATETIME DEFAULT NULL COMMENT '预计送达时间',
    `delivery_time` DATETIME DEFAULT NULL COMMENT '实际送达时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    `update_user` BIGINT NOT NULL COMMENT '最后修改人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_number` (`number`),
    KEY `idx_order_status_time_branch` (`status`, `order_time`, `branch_id`),
    KEY `idx_order_consignee_phone` (`consignee`, `phone`),
    KEY `idx_order_branch` (`branch_id`),
    CONSTRAINT `fk_order_branch` FOREIGN KEY (`branch_id`) REFERENCES `branch` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 12. 订单明细表（order_detail）- 订单商品明细
-- ============================================
CREATE TABLE `order_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `name` VARCHAR(32) NOT NULL COMMENT '商品名称',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片地址（冗余）',
    `dish_id` BIGINT DEFAULT NULL COMMENT '菜品ID',
    `setmeal_id` BIGINT DEFAULT NULL COMMENT '套餐ID',
    `dish_flavor` VARCHAR(50) DEFAULT NULL COMMENT '菜品口味',
    `number` INT NOT NULL COMMENT '商品数量',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总价',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_od_order_id` (`order_id`),
    KEY `idx_od_dish_id` (`dish_id`),
    KEY `idx_od_setmeal_id` (`setmeal_id`),
    CONSTRAINT `fk_od_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_od_dish` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`) ON UPDATE CASCADE,
    CONSTRAINT `fk_od_setmeal` FOREIGN KEY (`setmeal_id`) REFERENCES `setmeal` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ============================================
-- 13. 操作日志表（operation_log）- 敏感操作审计
-- ============================================
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(32) NOT NULL COMMENT '操作人姓名（冗余）',
    `branch_id` BIGINT NOT NULL COMMENT '操作分店ID',
    `module` VARCHAR(32) NOT NULL COMMENT '操作模块',
    `operation_type` VARCHAR(16) NOT NULL COMMENT '操作类型',
    `content` VARCHAR(255) NOT NULL COMMENT '操作内容',
    `ip` VARCHAR(32) NOT NULL COMMENT '操作IP地址',
    `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态：1-成功 0-失败',
    `error_msg` VARCHAR(512) DEFAULT NULL COMMENT '错误信息',
    PRIMARY KEY (`id`),
    KEY `idx_oplog_operator_time` (`operator_id`, `operation_time`),
    KEY `idx_oplog_module_type` (`module`, `operation_type`),
    KEY `idx_oplog_branch` (`branch_id`),
    KEY `idx_oplog_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 14. 登录日志表（login_log）- 登录审计
-- ============================================
CREATE TABLE `login_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `employee_id` BIGINT NOT NULL DEFAULT 0 COMMENT '员工ID',
    `username` VARCHAR(32) NOT NULL COMMENT '登录用户名',
    `branch_id` BIGINT DEFAULT NULL COMMENT '所属分店ID',
    `ip` VARCHAR(32) NOT NULL COMMENT '登录IP地址',
    `login_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `status` TINYINT NOT NULL COMMENT '登录状态：1-成功 0-失败',
    `error_msg` VARCHAR(255) DEFAULT NULL COMMENT '失败原因',
    `user_agent` VARCHAR(512) DEFAULT NULL COMMENT '浏览器/设备信息',
    PRIMARY KEY (`id`),
    KEY `idx_loginlog_emp_time` (`employee_id`, `login_time`),
    KEY `idx_loginlog_username_status` (`username`, `status`),
    KEY `idx_loginlog_time` (`login_time`),
    KEY `idx_loginlog_branch` (`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ============================================
-- 15. 数据字典表（dict）- 统一状态管理
-- ============================================
CREATE TABLE `dict` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `dict_type` VARCHAR(32) NOT NULL COMMENT '字典类型',
    `dict_code` INT NOT NULL COMMENT '字典编码',
    `dict_label` VARCHAR(32) NOT NULL COMMENT '字典标签',
    `dict_desc` VARCHAR(128) DEFAULT NULL COMMENT '字典描述',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '字典状态：1-启用 0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type_code` (`dict_type`, `dict_code`),
    KEY `idx_dict_type` (`dict_type`),
    KEY `idx_dict_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认分店（总店）
INSERT INTO `branch` (`id`, `name`, `address`, `contact_name`, `contact_phone`, `status`, `is_deleted`, `create_user`, `update_user`) 
VALUES (1, '总店', '系统默认地址', '系统管理员', '13800138000', 1, 0, 1, 1);

-- 插入默认角色（管理员）
INSERT INTO `role` (`id`, `name`, `description`, `status`, `create_user`, `update_user`) 
VALUES (1, '管理员', '系统最高权限，可操作所有模块', 1, 1, 1);

-- 插入默认管理员账号（用户名：admin，密码：123456，需要BCrypt加密后存储）
-- 注意：实际使用时需要将密码进行BCrypt加密，这里仅作示例
-- BCrypt加密后的密码示例：$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2
INSERT INTO `employee` (`id`, `username`, `password`, `name`, `phone`, `sex`, `role_id`, `branch_id`, `status`, `is_deleted`, `create_user`, `update_user`) 
VALUES (1, 'admin', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '系统管理员', '13800138000', '男', 1, 1, 1, 0, 1, 1);

-- 插入数据字典数据
-- 订单状态
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('order_status', 1, '待付款', '订单状态：待付款', 1, 1),
('order_status', 2, '待接单', '订单状态：待接单', 2, 1),
('order_status', 3, '已接单', '订单状态：已接单', 3, 1),
('order_status', 4, '派送中', '订单状态：派送中', 4, 1),
('order_status', 5, '已完成', '订单状态：已完成', 5, 1),
('order_status', 6, '已取消', '订单状态：已取消', 6, 1);

-- 支付方式
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('pay_method', 1, '微信支付', '支付方式：微信支付', 1, 1),
('pay_method', 2, '支付宝支付', '支付方式：支付宝支付', 2, 1);

-- 支付状态
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('pay_status', 0, '未支付', '支付状态：未支付', 1, 1),
('pay_status', 1, '已支付', '支付状态：已支付', 2, 1),
('pay_status', 2, '退款', '支付状态：退款', 3, 1);

-- 菜品/套餐状态
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('dish_status', 1, '起售', '菜品状态：起售', 1, 1),
('dish_status', 0, '停售', '菜品状态：停售', 2, 1);

-- 分类类型
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('category_type', 1, '菜品分类', '分类类型：菜品分类', 1, 1),
('category_type', 2, '套餐分类', '分类类型：套餐分类', 2, 1);

-- 账号状态
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('account_status', 1, '正常', '账号状态：正常', 1, 1),
('account_status', 0, '锁定', '账号状态：锁定', 2, 1);

-- 通用状态
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `dict_desc`, `sort`, `status`) VALUES
('common_status', 1, '启用', '通用状态：启用', 1, 1),
('common_status', 0, '禁用', '通用状态：禁用', 2, 1);

-- ============================================
-- 脚本执行完成
-- ============================================

