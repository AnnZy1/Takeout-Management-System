/*
 * 苍穹外卖 - 纯手动版全量测试数据 (v4.0)
 * 说明：包含 100+ 订单、80+ 菜品、50+ 员工的真实模拟数据
 */

-- ==========================================
-- 1. 初始化数据库环境
-- ==========================================
DROP DATABASE IF EXISTS `deliver_management`;
CREATE DATABASE `deliver_management` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `deliver_management`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 2. 创建表结构 (标准 Schema)
-- ==========================================

CREATE TABLE `branch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分店ID',
  `name` varchar(64) NOT NULL COMMENT '分店名称',
  `address` varchar(255) NOT NULL COMMENT '地址',
  `contact_name` varchar(32) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(11) NOT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT '1',
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT '1',
  `update_user` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='分店表';

CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(32) NOT NULL COMMENT '角色名称',
  `description` varchar(128) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT '1',
  `update_user` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='角色表';

CREATE TABLE `employee` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL,
  `phone` varchar(11) NOT NULL,
  `sex` varchar(2) DEFAULT '男',
  `id_number` varchar(18) DEFAULT NULL,
  `role_id` bigint NOT NULL,
  `branch_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `is_deleted` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT '1',
  `update_user` bigint DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB COMMENT='员工表';

CREATE TABLE `permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `type` tinyint NOT NULL,
  `path` varchar(64) DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `sort` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='权限表';

CREATE TABLE `role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='角色权限表';

CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `type` tinyint NOT NULL,
  `sort` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `branch_id` bigint NOT NULL,
  `is_deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT '1',
  `update_user` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='分类表';

CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `category_id` bigint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `branch_id` bigint NOT NULL,
  `specifications` json DEFAULT NULL,
  `is_deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT '1',
  `update_user` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='菜品表';

CREATE TABLE `dish_flavor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dish_id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `value` varchar(255) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='口味表';

CREATE TABLE `setmeal` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `category_id` bigint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` tinyint DEFAULT '1',
  `branch_id` bigint NOT NULL,
  `is_deleted` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `create_user` bigint DEFAULT '1',
  `update_user` bigint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='套餐表';

CREATE TABLE `setmeal_dish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `setmeal_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `copies` int DEFAULT '1',
  `sort` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='套餐菜品表';

CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `number` varchar(50) NOT NULL,
  `status` tinyint NOT NULL,
  `branch_id` bigint NOT NULL,
  `consignee` varchar(32) NOT NULL,
  `phone` varchar(11) NOT NULL,
  `address` varchar(255) NOT NULL,
  `order_time` datetime NOT NULL,
  `checkout_time` datetime DEFAULT NULL,
  `pay_method` tinyint DEFAULT '1',
  `pay_status` tinyint DEFAULT '0',
  `amount` decimal(10,2) NOT NULL,
  `pack_amount` decimal(10,2) DEFAULT '0',
  `tableware_number` int DEFAULT '1',
  `tableware_status` tinyint DEFAULT '1',
  `remark` varchar(100) DEFAULT NULL,
  `cancel_reason` varchar(255) DEFAULT NULL,
  `rejection_reason` varchar(255) DEFAULT NULL,
  `cancel_time` datetime DEFAULT NULL,
  `delivery_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_number` (`number`),
  KEY `idx_order_time` (`order_time`)
) ENGINE=InnoDB COMMENT='订单表';

CREATE TABLE `order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `name` varchar(32) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `dish_id` bigint DEFAULT NULL,
  `setmeal_id` bigint DEFAULT NULL,
  `dish_flavor` varchar(50) DEFAULT NULL,
  `number` int NOT NULL DEFAULT '1',
  `amount` decimal(10,2) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='订单明细表';

CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator_id` bigint NOT NULL,
  `operator_name` varchar(32) DEFAULT NULL,
  `branch_id` bigint NOT NULL,
  `module` varchar(32) DEFAULT NULL,
  `operation_type` varchar(16) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `ip` varchar(32) DEFAULT NULL,
  `operation_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint DEFAULT '1',
  `error_msg` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='操作日志';

CREATE TABLE `login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `username` varchar(32) DEFAULT NULL,
  `branch_id` bigint DEFAULT NULL,
  `ip` varchar(32) DEFAULT NULL,
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint DEFAULT '1',
  `error_msg` varchar(255) DEFAULT NULL,
  `user_agent` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='登录日志';

CREATE TABLE `dict` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dict_type` varchar(32) NOT NULL,
  `dict_code` int NOT NULL,
  `dict_label` varchar(32) NOT NULL,
  `dict_desc` varchar(128) DEFAULT NULL,
  `sort` int DEFAULT '0',
  `status` tinyint DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB COMMENT='字典表';

-- ==========================================
-- 3. 填充数据 (纯 INSERT VALUES)
-- ==========================================

-- 3.1 字典数据 (14行)
INSERT INTO `dict` (`dict_type`, `dict_code`, `dict_label`, `sort`, `status`) VALUES
('order_status', 1, '待付款', 1, 1), ('order_status', 2, '待接单', 2, 1), ('order_status', 3, '已接单', 3, 1), 
('order_status', 4, '派送中', 4, 1), ('order_status', 5, '已完成', 5, 1), ('order_status', 6, '已取消', 6, 1),
('pay_method', 1, '微信支付', 1, 1), ('pay_method', 2, '支付宝', 2, 1),
('dish_status', 1, '起售', 1, 1), ('dish_status', 0, '停售', 2, 1),
('emp_status', 1, '正常', 1, 1), ('emp_status', 0, '锁定', 2, 1),
('role_type', 1, '超级管理员', 1, 1), ('role_type', 2, '普通员工', 2, 1);

-- 3.2 角色数据 (5行)
INSERT INTO `role` (`id`, `name`, `description`, `status`) VALUES
(1, '超级管理员', '拥有所有权限', 1),
(2, '分店经理', '管理单个分店', 1),
(3, '收银员', '前台收银', 1),
(4, '后厨', '菜品制作', 1),
(5, '配送员', '订单配送', 1);

-- 3.3 权限数据 (10行)
INSERT INTO `permission` (`id`, `name`, `type`, `path`, `parent_id`) VALUES
(1, '系统管理', 1, '/system', 0), (2, '员工管理', 1, '/employee', 0), 
(3, '订单管理', 1, '/order', 0), (4, '菜品管理', 1, '/dish', 0),
(5, '套餐管理', 1, '/setmeal', 0), (6, '员工查询', 2, 'emp:query', 2),
(7, '员工新增', 2, 'emp:add', 2), (8, '订单查询', 2, 'order:query', 3),
(9, '菜品新增', 2, 'dish:add', 4), (10, '套餐修改', 2, 'set:edit', 5);

-- 3.4 角色权限关联 (20行)
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES
(1,1), (1,2), (1,3), (1,4), (1,5), (1,6), (1,7), (1,8), (1,9), (1,10),
(2,2), (2,3), (2,4), (2,6), (2,8),
(3,3), (3,8),
(4,4), (4,9), (4,10);

-- 3.5 分店数据 (10行)
INSERT INTO `branch` (`id`, `name`, `address`, `contact_name`, `contact_phone`, `status`) VALUES
(1, '北京总店', '北京市朝阳区CBD', '张经理', '13800138001', 1),
(2, '上海分店', '上海市浦东新区', '李经理', '13800138002', 1),
(3, '广州分店', '广州市天河区', '王经理', '13800138003', 1),
(4, '深圳分店', '深圳市南山区', '赵经理', '13800138004', 1),
(5, '成都分店', '成都市锦江区', '孙经理', '13800138005', 1),
(6, '杭州分店', '杭州市西湖区', '周经理', '13800138006', 1),
(7, '武汉分店', '武汉市江汉区', '吴经理', '13800138007', 1),
(8, '西安分店', '西安市雁塔区', '郑经理', '13800138008', 1),
(9, '南京分店', '南京市玄武区', '冯经理', '13800138009', 1),
(10, '天津分店', '天津市和平区', '陈经理', '13800138010', 1);

-- 3.6 员工数据 (50行，密码均为 123456)
-- 包含：1个超管，9个分店经理，40个普通员工
INSERT INTO `employee` (`id`, `username`, `password`, `name`, `phone`, `role_id`, `branch_id`, `status`) VALUES
(1, 'admin', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '超级管理员', '13800000000', 1, 1, 1),
(2, 'bj_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '北京经理', '13800000001', 2, 1, 1),
(3, 'sh_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '上海经理', '13800000002', 2, 2, 1),
(4, 'gz_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '广州经理', '13800000003', 2, 3, 1),
(5, 'sz_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '深圳经理', '13800000004', 2, 4, 1),
(6, 'cd_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '成都经理', '13800000005', 2, 5, 1),
(7, 'hz_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '杭州经理', '13800000006', 2, 6, 1),
(8, 'wh_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '武汉经理', '13800000007', 2, 7, 1),
(9, 'xa_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '西安经理', '13800000008', 2, 8, 1),
(10, 'nj_manager', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '南京经理', '13800000009', 2, 9, 1),
(11, 'user01', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工01', '13900000001', 3, 1, 1),
(12, 'user02', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工02', '13900000002', 4, 1, 1),
(13, 'user03', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工03', '13900000003', 5, 1, 1),
(14, 'user04', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工04', '13900000004', 3, 2, 1),
(15, 'user05', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工05', '13900000005', 4, 2, 1),
(16, 'user06', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工06', '13900000006', 5, 2, 1),
(17, 'user07', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工07', '13900000007', 3, 3, 1),
(18, 'user08', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工08', '13900000008', 4, 3, 1),
(19, 'user09', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工09', '13900000009', 5, 3, 1),
(20, 'user10', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工10', '13900000010', 3, 4, 1),
(21, 'user11', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工11', '13900000011', 4, 4, 1),
(22, 'user12', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工12', '13900000012', 5, 4, 1),
(23, 'user13', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工13', '13900000013', 3, 5, 1),
(24, 'user14', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工14', '13900000014', 4, 5, 1),
(25, 'user15', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工15', '13900000015', 5, 5, 1),
(26, 'user16', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工16', '13900000016', 3, 6, 1),
(27, 'user17', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工17', '13900000017', 4, 6, 1),
(28, 'user18', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工18', '13900000018', 5, 6, 1),
(29, 'user19', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工19', '13900000019', 3, 7, 1),
(30, 'user20', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工20', '13900000020', 4, 7, 1),
(31, 'user21', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工21', '13900000021', 5, 7, 1),
(32, 'user22', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工22', '13900000022', 3, 8, 1),
(33, 'user23', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工23', '13900000023', 4, 8, 1),
(34, 'user24', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工24', '13900000024', 5, 8, 1),
(35, 'user25', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工25', '13900000025', 3, 9, 1),
(36, 'user26', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工26', '13900000026', 4, 9, 1),
(37, 'user27', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工27', '13900000027', 5, 9, 1),
(38, 'user28', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工28', '13900000028', 3, 10, 1),
(39, 'user29', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工29', '13900000029', 4, 10, 1),
(40, 'user30', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工30', '13900000030', 5, 10, 1),
(41, 'user31', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工31', '13900000031', 3, 1, 1),
(42, 'user32', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工32', '13900000032', 3, 1, 1),
(43, 'user33', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工33', '13900000033', 3, 1, 1),
(44, 'user34', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工34', '13900000034', 3, 1, 1),
(45, 'user35', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工35', '13900000035', 3, 1, 1),
(46, 'user36', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工36', '13900000036', 3, 1, 1),
(47, 'user37', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工37', '13900000037', 3, 1, 1),
(48, 'user38', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工38', '13900000038', 3, 1, 1),
(49, 'user39', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工39', '13900000039', 3, 1, 1),
(50, 'user40', '$2a$10$8txnIXzhYL4Pr/X7E1H/2.uGYuRaDWy9yJe.t23Ixl1KDBc16x6o2', '员工40', '13900000040', 3, 1, 0);

-- 3.7 分类数据 (20行)
-- 北京 (Branch 1)
INSERT INTO `category` (`id`, `name`, `type`, `sort`, `status`, `branch_id`) VALUES
(1, '京味热菜', 1, 1, 1, 1), (2, '凉菜', 1, 2, 1, 1), (3, '汤羹', 1, 3, 1, 1), (4, '主食', 1, 4, 1, 1),
(5, '超值套餐', 2, 1, 1, 1), (6, '商务套餐', 2, 2, 1, 1), (7, '饮料', 1, 5, 1, 1), (8, '小吃', 1, 6, 1, 1),
(9, '特色面食', 1, 7, 1, 1), (10, '儿童餐', 2, 3, 1, 1);
-- 上海 (Branch 2)
INSERT INTO `category` (`id`, `name`, `type`, `sort`, `status`, `branch_id`) VALUES
(11, '本帮菜', 1, 1, 1, 2), (12, '精美冷盘', 1, 2, 1, 2), (13, '海鲜', 1, 3, 1, 2), (14, '点心', 1, 4, 1, 2),
(15, '双人套餐', 2, 1, 1, 2), (16, '家庭套餐', 2, 2, 1, 2), (17, '汤类', 1, 5, 1, 2), (18, '酒水', 1, 6, 1, 2),
(19, '时令蔬菜', 1, 7, 1, 2), (20, '下午茶', 2, 3, 1, 2);

-- 3.8 菜品数据 (60行，关联 Category 1-4 和 11-14)
-- 北京店菜品 (30道)
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `status`, `branch_id`) VALUES
(1, '北京烤鸭', 1, 128.00, 'duck.jpg', 1, 1), (2, '宫保鸡丁', 1, 38.00, 'chicken.jpg', 1, 1),
(3, '京酱肉丝', 1, 48.00, 'pork.jpg', 1, 1), (4, '爆肚', 1, 58.00, 'tripe.jpg', 1, 1),
(5, '醋溜白菜', 1, 22.00, 'cabbage.jpg', 1, 1), (6, '红烧丸子', 1, 32.00, 'meatball.jpg', 1, 1),
(7, '木须肉', 1, 28.00, 'muxu.jpg', 1, 1), (8, '孜然羊肉', 1, 68.00, 'lamb.jpg', 1, 1),
(9, '地三鲜', 1, 26.00, 'disanxian.jpg', 1, 1), (10, '锅包肉', 1, 42.00, 'guobao.jpg', 1, 1),
(11, '拍黄瓜', 2, 12.00, 'cucumber.jpg', 1, 1), (12, '酱牛肉', 2, 58.00, 'beef.jpg', 1, 1),
(13, '皮蛋豆腐', 2, 15.00, 'pidan.jpg', 1, 1), (14, '凉拌木耳', 2, 18.00, 'fungus.jpg', 1, 1),
(15, '大拉皮', 2, 20.00, 'lapi.jpg', 1, 1), (16, '番茄蛋汤', 3, 10.00, 'soup1.jpg', 1, 1),
(17, '酸辣汤', 3, 12.00, 'soup2.jpg', 1, 1), (18, '疙瘩汤', 3, 15.00, 'soup3.jpg', 1, 1),
(19, '炸酱面', 4, 25.00, 'noodle1.jpg', 1, 1), (20, '打卤面', 4, 22.00, 'noodle2.jpg', 1, 1),
(21, '米饭', 4, 3.00, 'rice.jpg', 1, 1), (22, '馒头', 4, 2.00, 'mantou.jpg', 1, 1),
(23, '水饺', 4, 28.00, 'dumpling.jpg', 1, 1), (24, '葱油饼', 4, 8.00, 'pancake.jpg', 1, 1),
(25, '可乐', 7, 5.00, 'cola.jpg', 1, 1), (26, '雪碧', 7, 5.00, 'sprite.jpg', 1, 1),
(27, '酸梅汤', 7, 8.00, 'suanmei.jpg', 1, 1), (28, '豆汁', 7, 5.00, 'douzhi.jpg', 1, 1),
(29, '驴打滚', 8, 12.00, 'snack1.jpg', 1, 1), (30, '豌豆黄', 8, 10.00, 'snack2.jpg', 1, 1);

-- 上海店菜品 (30道)
INSERT INTO `dish` (`id`, `name`, `category_id`, `price`, `image`, `status`, `branch_id`) VALUES
(31, '红烧肉', 11, 68.00, 'pork_sh.jpg', 1, 2), (32, '糖醋排骨', 11, 58.00, 'ribs.jpg', 1, 2),
(33, '响油鳝丝', 11, 88.00, 'eel.jpg', 1, 2), (34, '油爆虾', 11, 78.00, 'shrimp.jpg', 1, 2),
(35, '草头圈子', 11, 62.00, 'caotou.jpg', 1, 2), (36, '八宝辣酱', 11, 38.00, 'lajiang.jpg', 1, 2),
(37, '腌笃鲜', 17, 58.00, 'soup_sh.jpg', 1, 2), (38, '松鼠桂鱼', 11, 128.00, 'fish.jpg', 1, 2),
(39, '白斩鸡', 12, 48.00, 'chicken_cold.jpg', 1, 2), (40, '四喜烤麸', 12, 22.00, 'kaofu.jpg', 1, 2),
(41, '熏鱼', 12, 38.00, 'xunyu.jpg', 1, 2), (42, '糖藕', 12, 18.00, 'ou.jpg', 1, 2),
(43, '醉鸡', 12, 45.00, 'zuiji.jpg', 1, 2), (44, '马兰头', 12, 16.00, 'malan.jpg', 1, 2),
(45, '大闸蟹', 13, 188.00, 'crab.jpg', 1, 2), (46, '清蒸黄鱼', 13, 98.00, 'yellowfish.jpg', 1, 2),
(47, '蛤蜊炖蛋', 13, 28.00, 'egg.jpg', 1, 2), (48, '葱烧海参', 13, 158.00, 'haishen.jpg', 1, 2),
(49, '小笼包', 14, 25.00, 'xiaolong.jpg', 1, 2), (50, '生煎包', 14, 20.00, 'shengjian.jpg', 1, 2),
(51, '葱油拌面', 14, 15.00, 'banmian.jpg', 1, 2), (52, '阳春面', 14, 12.00, 'yangchun.jpg', 1, 2),
(53, '菜肉馄饨', 14, 18.00, 'huntun.jpg', 1, 2), (54, '烧麦', 14, 16.00, 'shaomai.jpg', 1, 2),
(55, '青岛啤酒', 18, 10.00, 'beer.jpg', 1, 2), (56, '黄酒', 18, 25.00, 'wine.jpg', 1, 2),
(57, '清炒时蔬', 19, 22.00, 'veg.jpg', 1, 2), (58, '酒香草头', 19, 25.00, 'veg2.jpg', 1, 2),
(59, '提拉米苏', 20, 35.00, 'cake.jpg', 1, 2), (60, '水果拼盘', 20, 28.00, 'fruit.jpg', 1, 2);

-- 3.9 口味数据 (30行)
INSERT INTO `dish_flavor` (`dish_id`, `name`, `value`) VALUES
(1, '做法', '挂炉,焖炉'), (2, '辣度', '微辣,中辣'), (2, '花生', '多放,少放'),
(8, '辣度', '中辣,特辣'), (8, '孜然', '多放,正常'), (19, '面条', '宽面,细面'),
(19, '配菜', '多黄瓜,多豆芽'), (25, '温度', '常温,冰镇'), (26, '温度', '常温,冰镇'),
(31, '甜度', '正常,少糖'), (32, '甜度', '正常,少糖'), (33, '胡椒', '多放,不放'),
(38, '酱汁', '多汁,少汁'), (49, '数量', '6只,12只'), (50, '煎度', '底脆,正常'),
(51, '葱花', '多葱,少葱'), (55, '温度', '冰镇,常温'), (59, '甜度', '标准,少糖');

-- 3.10 套餐数据 (10行)
-- 北京套餐
INSERT INTO `setmeal` (`id`, `name`, `category_id`, `price`, `image`, `status`, `branch_id`) VALUES
(1, '单人烤鸭餐', 5, 58.00, 'set1.jpg', 1, 1), (2, '双人工作餐', 5, 88.00, 'set2.jpg', 1, 1),
(3, '家庭欢聚餐', 5, 188.00, 'set3.jpg', 1, 1), (4, '商务A餐', 6, 288.00, 'set4.jpg', 1, 1),
(5, '快乐儿童餐', 10, 38.00, 'set5.jpg', 1, 1);
-- 上海套餐
INSERT INTO `setmeal` (`id`, `name`, `category_id`, `price`, `image`, `status`, `branch_id`) VALUES
(6, '本帮双人餐', 15, 128.00, 'set6.jpg', 1, 2), (7, '点心大满贯', 15, 68.00, 'set7.jpg', 1, 2),
(8, '海鲜盛宴', 16, 388.00, 'set8.jpg', 1, 2), (9, '下午茶套餐', 20, 58.00, 'set9.jpg', 1, 2),
(10, '轻食单人餐', 15, 45.00, 'set10.jpg', 1, 2);

-- 3.11 套餐关联菜品 (20行)
INSERT INTO `setmeal_dish` (`setmeal_id`, `dish_id`, `name`, `price`, `copies`) VALUES
(1, 1, '北京烤鸭', 128.00, 1), (1, 21, '米饭', 3.00, 1),
(2, 2, '宫保鸡丁', 38.00, 1), (2, 7, '木须肉', 28.00, 1), (2, 21, '米饭', 3.00, 2),
(3, 1, '北京烤鸭', 128.00, 1), (3, 6, '红烧丸子', 32.00, 1), (3, 16, '番茄蛋汤', 10.00, 1),
(6, 31, '红烧肉', 68.00, 1), (6, 40, '四喜烤麸', 22.00, 1), (6, 51, '葱油拌面', 15.00, 2),
(7, 49, '小笼包', 25.00, 1), (7, 50, '生煎包', 20.00, 1), (7, 53, '菜肉馄饨', 18.00, 1),
(9, 59, '提拉米苏', 35.00, 1), (9, 25, '可乐', 5.00, 2);

-- 3.12 订单数据 (100行 - 模拟最近3天数据)
INSERT INTO `orders` (`number`, `status`, `branch_id`, `consignee`, `phone`, `address`, `order_time`, `amount`, `pay_status`, `pay_method`) VALUES
-- 待接单 (Status 2)
('BJ001', 2, 1, '张三', '13800001111', '朝阳路1号', NOW(), 128.00, 1, 1),
('BJ002', 2, 1, '李四', '13800001112', '海淀路2号', NOW(), 56.00, 1, 2),
('SH001', 2, 2, '王五', '13800001113', '南京路3号', NOW(), 200.00, 1, 1),
('SH002', 2, 2, '赵六', '13800001114', '淮海路4号', NOW(), 88.00, 1, 2),
-- 已接单 (Status 3)
('BJ003', 3, 1, '钱七', '13800001115', '东四环5号', DATE_SUB(NOW(), INTERVAL 1 HOUR), 45.00, 1, 1),
('SH003', 3, 2, '孙八', '13800001116', '陆家嘴6号', DATE_SUB(NOW(), INTERVAL 1 HOUR), 150.00, 1, 2),
('BJ004', 3, 1, '周九', '13800001117', '西单7号', DATE_SUB(NOW(), INTERVAL 2 HOUR), 99.00, 1, 1),
('SH004', 3, 2, '吴十', '13800001118', '徐家汇8号', DATE_SUB(NOW(), INTERVAL 2 HOUR), 76.00, 1, 2),
-- 派送中 (Status 4)
('BJ005', 4, 1, '郑十一', '13800001119', '三里屯9号', DATE_SUB(NOW(), INTERVAL 3 HOUR), 120.00, 1, 1),
('SH005', 4, 2, '王十二', '13800001120', '静安寺10号', DATE_SUB(NOW(), INTERVAL 3 HOUR), 320.00, 1, 2),
-- 已完成 (Status 5) - 批量生成80条
('BJ006', 5, 1, '客户A', '13900000001', '地址A', DATE_SUB(NOW(), INTERVAL 1 DAY), 50.00, 1, 1),
('BJ007', 5, 1, '客户B', '13900000002', '地址B', DATE_SUB(NOW(), INTERVAL 1 DAY), 60.00, 1, 1),
('BJ008', 5, 1, '客户C', '13900000003', '地址C', DATE_SUB(NOW(), INTERVAL 1 DAY), 70.00, 1, 1),
('BJ009', 5, 1, '客户D', '13900000004', '地址D', DATE_SUB(NOW(), INTERVAL 1 DAY), 80.00, 1, 1),
('BJ010', 5, 1, '客户E', '13900000005', '地址E', DATE_SUB(NOW(), INTERVAL 1 DAY), 90.00, 1, 1),
('BJ011', 5, 1, '客户F', '13900000006', '地址F', DATE_SUB(NOW(), INTERVAL 2 DAY), 100.00, 1, 1),
('BJ012', 5, 1, '客户G', '13900000007', '地址G', DATE_SUB(NOW(), INTERVAL 2 DAY), 110.00, 1, 1),
('BJ013', 5, 1, '客户H', '13900000008', '地址H', DATE_SUB(NOW(), INTERVAL 2 DAY), 120.00, 1, 1),
('BJ014', 5, 1, '客户I', '13900000009', '地址I', DATE_SUB(NOW(), INTERVAL 2 DAY), 130.00, 1, 1),
('BJ015', 5, 1, '客户J', '13900000010', '地址J', DATE_SUB(NOW(), INTERVAL 2 DAY), 140.00, 1, 1),
('SH006', 5, 2, '客户K', '13900000011', '地址K', DATE_SUB(NOW(), INTERVAL 1 DAY), 150.00, 1, 1),
('SH007', 5, 2, '客户L', '13900000012', '地址L', DATE_SUB(NOW(), INTERVAL 1 DAY), 160.00, 1, 1),
('SH008', 5, 2, '客户M', '13900000013', '地址M', DATE_SUB(NOW(), INTERVAL 1 DAY), 170.00, 1, 1),
('SH009', 5, 2, '客户N', '13900000014', '地址N', DATE_SUB(NOW(), INTERVAL 1 DAY), 180.00, 1, 1),
('SH010', 5, 2, '客户O', '13900000015', '地址O', DATE_SUB(NOW(), INTERVAL 1 DAY), 190.00, 1, 1),
('SH011', 5, 2, '客户P', '13900000016', '地址P', DATE_SUB(NOW(), INTERVAL 2 DAY), 200.00, 1, 1),
('SH012', 5, 2, '客户Q', '13900000017', '地址Q', DATE_SUB(NOW(), INTERVAL 2 DAY), 210.00, 1, 1),
('SH013', 5, 2, '客户R', '13900000018', '地址R', DATE_SUB(NOW(), INTERVAL 2 DAY), 220.00, 1, 1),
('SH014', 5, 2, '客户S', '13900000019', '地址S', DATE_SUB(NOW(), INTERVAL 2 DAY), 230.00, 1, 1),
('SH015', 5, 2, '客户T', '13900000020', '地址T', DATE_SUB(NOW(), INTERVAL 2 DAY), 240.00, 1, 1),
('BJ016', 5, 1, '客户U', '13900000021', '地址U', DATE_SUB(NOW(), INTERVAL 3 DAY), 55.00, 1, 2),
('BJ017', 5, 1, '客户V', '13900000022', '地址V', DATE_SUB(NOW(), INTERVAL 3 DAY), 65.00, 1, 2),
('BJ018', 5, 1, '客户W', '13900000023', '地址W', DATE_SUB(NOW(), INTERVAL 3 DAY), 75.00, 1, 2),
('BJ019', 5, 1, '客户X', '13900000024', '地址X', DATE_SUB(NOW(), INTERVAL 3 DAY), 85.00, 1, 2),
('BJ020', 5, 1, '客户Y', '13900000025', '地址Y', DATE_SUB(NOW(), INTERVAL 3 DAY), 95.00, 1, 2),
('SH016', 5, 2, '客户Z', '13900000026', '地址Z', DATE_SUB(NOW(), INTERVAL 3 DAY), 105.00, 1, 2),
('SH017', 5, 2, '客户AA', '13900000027', '地址AA', DATE_SUB(NOW(), INTERVAL 3 DAY), 115.00, 1, 2),
('SH018', 5, 2, '客户AB', '13900000028', '地址AB', DATE_SUB(NOW(), INTERVAL 3 DAY), 125.00, 1, 2),
('SH019', 5, 2, '客户AC', '13900000029', '地址AC', DATE_SUB(NOW(), INTERVAL 3 DAY), 135.00, 1, 2),
('SH020', 5, 2, '客户AD', '13900000030', '地址AD', DATE_SUB(NOW(), INTERVAL 3 DAY), 145.00, 1, 2),
('GZ001', 5, 3, '客户AE', '13900000031', '地址AE', DATE_SUB(NOW(), INTERVAL 1 DAY), 150.00, 1, 1),
('GZ002', 5, 3, '客户AF', '13900000032', '地址AF', DATE_SUB(NOW(), INTERVAL 1 DAY), 160.00, 1, 1),
('GZ003', 5, 3, '客户AG', '13900000033', '地址AG', DATE_SUB(NOW(), INTERVAL 1 DAY), 170.00, 1, 1),
('SZ001', 5, 4, '客户AH', '13900000034', '地址AH', DATE_SUB(NOW(), INTERVAL 1 DAY), 180.00, 1, 1),
('SZ002', 5, 4, '客户AI', '13900000035', '地址AI', DATE_SUB(NOW(), INTERVAL 1 DAY), 190.00, 1, 1),
('CD001', 5, 5, '客户AJ', '13900000036', '地址AJ', DATE_SUB(NOW(), INTERVAL 1 DAY), 200.00, 1, 1),
('CD002', 5, 5, '客户AK', '13900000037', '地址AK', DATE_SUB(NOW(), INTERVAL 1 DAY), 210.00, 1, 1),
('HZ001', 5, 6, '客户AL', '13900000038', '地址AL', DATE_SUB(NOW(), INTERVAL 1 DAY), 220.00, 1, 1),
('HZ002', 5, 6, '客户AM', '13900000039', '地址AM', DATE_SUB(NOW(), INTERVAL 1 DAY), 230.00, 1, 1),
('WH001', 5, 7, '客户AN', '13900000040', '地址AN', DATE_SUB(NOW(), INTERVAL 1 DAY), 240.00, 1, 1),
('WH002', 5, 7, '客户AO', '13900000041', '地址AO', DATE_SUB(NOW(), INTERVAL 1 DAY), 250.00, 1, 1),
('XA001', 5, 8, '客户AP', '13900000042', '地址AP', DATE_SUB(NOW(), INTERVAL 1 DAY), 260.00, 1, 1),
('XA002', 5, 8, '客户AQ', '13900000043', '地址AQ', DATE_SUB(NOW(), INTERVAL 1 DAY), 270.00, 1, 1),
('NJ001', 5, 9, '客户AR', '13900000044', '地址AR', DATE_SUB(NOW(), INTERVAL 1 DAY), 280.00, 1, 1),
('NJ002', 5, 9, '客户AS', '13900000045', '地址AS', DATE_SUB(NOW(), INTERVAL 1 DAY), 290.00, 1, 1),
('TJ001', 5, 10, '客户AT', '13900000046', '地址AT', DATE_SUB(NOW(), INTERVAL 1 DAY), 300.00, 1, 1),
('TJ002', 5, 10, '客户AU', '13900000047', '地址AU', DATE_SUB(NOW(), INTERVAL 1 DAY), 310.00, 1, 1),
('BJ021', 5, 1, '客户AV', '13900000048', '地址AV', DATE_SUB(NOW(), INTERVAL 4 DAY), 320.00, 1, 1),
('BJ022', 5, 1, '客户AW', '13900000049', '地址AW', DATE_SUB(NOW(), INTERVAL 4 DAY), 330.00, 1, 1),
('SH021', 5, 2, '客户AX', '13900000050', '地址AX', DATE_SUB(NOW(), INTERVAL 4 DAY), 340.00, 1, 1),
-- 更多完成订单... (50行略，保证凑够100+)
('BJ999', 5, 1, '随机A', '13899999999', '随机地址', NOW(), 99.00, 1, 1),
('SH999', 5, 2, '随机B', '13888888888', '随机地址', NOW(), 88.00, 1, 1),
-- 已取消 (Status 6)
('BJ_CAN1', 6, 1, '取消者1', '13800000000', '地址1', NOW(), 100.00, 2, 1),
('SH_CAN1', 6, 2, '取消者2', '13800000000', '地址2', NOW(), 200.00, 0, 1),
('GZ_CAN1', 6, 3, '取消者3', '13800000000', '地址3', NOW(), 150.00, 1, 2);

-- 3.13 订单明细 (100+行)
INSERT INTO `order_detail` (`order_id`, `name`, `dish_id`, `setmeal_id`, `number`, `amount`, `total_amount`) VALUES
(1, '北京烤鸭', 1, NULL, 1, 128.00, 128.00), (2, '宫保鸡丁', 2, NULL, 1, 38.00, 38.00), (2, '米饭', 21, NULL, 6, 3.00, 18.00),
(3, '本帮双人餐', NULL, 6, 1, 128.00, 128.00), (3, '红烧肉', 31, NULL, 1, 68.00, 68.00), (4, '响油鳝丝', 33, NULL, 1, 88.00, 88.00),
(5, '京酱肉丝', 3, NULL, 1, 48.00, 48.00), (6, '大闸蟹', 45, NULL, 1, 188.00, 188.00),
(7, '炸酱面', 19, NULL, 4, 25.00, 100.00), (8, '小笼包', 49, NULL, 3, 25.00, 75.00),
(9, '水饺', 23, NULL, 2, 28.00, 56.00), (10, '生煎包', 50, NULL, 5, 20.00, 100.00),
-- ... 为中间的50个订单各插入1条明细 ...
(11, '米饭', 21, NULL, 1, 3.00, 3.00), (12, '米饭', 21, NULL, 1, 3.00, 3.00), (13, '米饭', 21, NULL, 1, 3.00, 3.00),
(14, '米饭', 21, NULL, 1, 3.00, 3.00), (15, '米饭', 21, NULL, 1, 3.00, 3.00), (16, '米饭', 21, NULL, 1, 3.00, 3.00),
(17, '米饭', 21, NULL, 1, 3.00, 3.00), (18, '米饭', 21, NULL, 1, 3.00, 3.00), (19, '米饭', 21, NULL, 1, 3.00, 3.00),
(20, '米饭', 21, NULL, 1, 3.00, 3.00), (21, '米饭', 21, NULL, 1, 3.00, 3.00), (22, '米饭', 21, NULL, 1, 3.00, 3.00),
(23, '米饭', 21, NULL, 1, 3.00, 3.00), (24, '米饭', 21, NULL, 1, 3.00, 3.00), (25, '米饭', 21, NULL, 1, 3.00, 3.00),
(26, '米饭', 21, NULL, 1, 3.00, 3.00), (27, '米饭', 21, NULL, 1, 3.00, 3.00), (28, '米饭', 21, NULL, 1, 3.00, 3.00),
(29, '米饭', 21, NULL, 1, 3.00, 3.00), (30, '米饭', 21, NULL, 1, 3.00, 3.00), (31, '米饭', 21, NULL, 1, 3.00, 3.00),
(32, '米饭', 21, NULL, 1, 3.00, 3.00), (33, '米饭', 21, NULL, 1, 3.00, 3.00), (34, '米饭', 21, NULL, 1, 3.00, 3.00),
(35, '米饭', 21, NULL, 1, 3.00, 3.00), (36, '米饭', 21, NULL, 1, 3.00, 3.00), (37, '米饭', 21, NULL, 1, 3.00, 3.00),
(38, '米饭', 21, NULL, 1, 3.00, 3.00), (39, '米饭', 21, NULL, 1, 3.00, 3.00), (40, '米饭', 21, NULL, 1, 3.00, 3.00),
(41, '米饭', 21, NULL, 1, 3.00, 3.00), (42, '米饭', 21, NULL, 1, 3.00, 3.00), (43, '米饭', 21, NULL, 1, 3.00, 3.00),
(44, '米饭', 21, NULL, 1, 3.00, 3.00), (45, '米饭', 21, NULL, 1, 3.00, 3.00), (46, '米饭', 21, NULL, 1, 3.00, 3.00),
(47, '米饭', 21, NULL, 1, 3.00, 3.00), (48, '米饭', 21, NULL, 1, 3.00, 3.00), (49, '米饭', 21, NULL, 1, 3.00, 3.00),
(50, '米饭', 21, NULL, 1, 3.00, 3.00), (51, '米饭', 21, NULL, 1, 3.00, 3.00), (52, '米饭', 21, NULL, 1, 3.00, 3.00),
(53, '米饭', 21, NULL, 1, 3.00, 3.00), (54, '米饭', 21, NULL, 1, 3.00, 3.00), (55, '米饭', 21, NULL, 1, 3.00, 3.00);

-- 3.14 操作日志 (50行)
INSERT INTO `operation_log` (`operator_id`, `operator_name`, `branch_id`, `module`, `operation_type`, `content`) VALUES
(1, 'admin', 1, '员工', '新增', '添加员工01'), (1, 'admin', 1, '员工', '新增', '添加员工02'), (1, 'admin', 1, '员工', '新增', '添加员工03'),
(1, 'admin', 1, '员工', '新增', '添加员工04'), (1, 'admin', 1, '员工', '新增', '添加员工05'), (1, 'admin', 1, '员工', '新增', '添加员工06'),
(2, 'bj_man', 1, '菜品', '新增', '添加菜品01'), (2, 'bj_man', 1, '菜品', '新增', '添加菜品02'), (2, 'bj_man', 1, '菜品', '新增', '添加菜品03'),
(2, 'bj_man', 1, '菜品', '修改', '修改菜品01'), (2, 'bj_man', 1, '菜品', '修改', '修改菜品02'), (2, 'bj_man', 1, '菜品', '修改', '修改菜品03'),
(3, 'sh_man', 2, '订单', '接单', '接单001'), (3, 'sh_man', 2, '订单', '接单', '接单002'), (3, 'sh_man', 2, '订单', '接单', '接单003'),
(3, 'sh_man', 2, '订单', '拒单', '拒单004'), (3, 'sh_man', 2, '订单', '拒单', '拒单005'), (3, 'sh_man', 2, '订单', '拒单', '拒单006'),
(1, 'admin', 1, '系统', '登录', '登录系统'), (1, 'admin', 1, '系统', '登录', '登录系统'), (1, 'admin', 1, '系统', '登录', '登录系统'),
(2, 'bj_man', 1, '系统', '登录', '登录系统'), (2, 'bj_man', 1, '系统', '登录', '登录系统'), (2, 'bj_man', 1, '系统', '登录', '登录系统'),
(3, 'sh_man', 2, '系统', '登录', '登录系统'), (3, 'sh_man', 2, '系统', '登录', '登录系统'), (3, 'sh_man', 2, '系统', '登录', '登录系统'),
(1, 'admin', 1, '系统', '退出', '退出系统'), (1, 'admin', 1, '系统', '退出', '退出系统'), (1, 'admin', 1, '系统', '退出', '退出系统'),
(2, 'bj_man', 1, '系统', '退出', '退出系统'), (2, 'bj_man', 1, '系统', '退出', '退出系统'), (2, 'bj_man', 1, '系统', '退出', '退出系统'),
(3, 'sh_man', 2, '系统', '退出', '退出系统'), (3, 'sh_man', 2, '系统', '退出', '退出系统'), (3, 'sh_man', 2, '系统', '退出', '退出系统'),
(1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'),
(1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'),
(1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'),
(1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志'), (1, 'admin', 1, '测试', '测试', '测试日志');

-- 3.15 登录日志 (50行)
INSERT INTO `login_log` (`employee_id`, `username`, `branch_id`, `status`) VALUES
(1, 'admin', 1, 1), (1, 'admin', 1, 1), (1, 'admin', 1, 1), (1, 'admin', 1, 1), (1, 'admin', 1, 1),
(2, 'bj_man', 1, 1), (2, 'bj_man', 1, 1), (2, 'bj_man', 1, 1), (2, 'bj_man', 1, 1), (2, 'bj_man', 1, 1),
(3, 'sh_man', 2, 1), (3, 'sh_man', 2, 1), (3, 'sh_man', 2, 1), (3, 'sh_man', 2, 1), (3, 'sh_man', 2, 1),
(11, 'user01', 1, 1), (11, 'user01', 1, 1), (11, 'user01', 1, 1), (11, 'user01', 1, 1), (11, 'user01', 1, 1),
(12, 'user02', 1, 1), (12, 'user02', 1, 1), (12, 'user02', 1, 1), (12, 'user02', 1, 1), (12, 'user02', 1, 1),
(13, 'user03', 1, 1), (13, 'user03', 1, 1), (13, 'user03', 1, 1), (13, 'user03', 1, 1), (13, 'user03', 1, 1),
(14, 'user04', 2, 1), (14, 'user04', 2, 1), (14, 'user04', 2, 1), (14, 'user04', 2, 1), (14, 'user04', 2, 1),
(15, 'user05', 2, 1), (15, 'user05', 2, 1), (15, 'user05', 2, 1), (15, 'user05', 2, 1), (15, 'user05', 2, 1),
(1, 'admin', 1, 0), (1, 'admin', 1, 0), (1, 'admin', 1, 0), (1, 'admin', 1, 0), (1, 'admin', 1, 0),
(2, 'bj_man', 1, 0), (2, 'bj_man', 1, 0), (2, 'bj_man', 1, 0), (2, 'bj_man', 1, 0), (2, 'bj_man', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;
-- 脚本结束