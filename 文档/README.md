# 外卖管理系统

## 项目简介

本项目是一个基于 Spring Boot + Vue 的外卖管理系统，包含管理后台和移动端两部分。

## 技术栈

### 后端技术栈
- Spring Boot 3.1.10
- Spring Security
- MyBatis Plus
- MySQL 8.0
- Redis
- JWT
- Lombok
- Knife4j (Swagger)

### 前端技术栈
- Vue 3
- Element Plus
- Axios
- Vue Router
- Pinia

## 项目启动

### 后端启动
1. 导入 [init.sql](file:///Users/mac/Documents/project/deliverManagement/init.sql) 和 [test_data.sql](file:///Users/mac/Documents/project/deliverManagement/test_data.sql) 到 MySQL 数据库
2. 修改 [application.yml](file:///Users/mac/Documents/project/deliverManagement/src/main/resources/application.yml) 中的数据库连接配置
3. 运行 [DeliverManagementApplication.java](file:///Users/mac/Documents/project/deliverManagement/src/main/java/com/delivery/management/DeliverManagementApplication.java)

### 前端启动
```bash
npm install
npm run dev
```

## 部署指南

### Linux 环境部署

#### 1. 环境准备
- JDK 17+
- MySQL 8.0+
- Node.js 16+

#### 2. 后端部署
1. 构建项目：
   ```bash
   mvn clean package -Pprod
   ```

2. 创建日志目录并授权：
   ```bash
   sudo mkdir -p /var/log/deliver-management
   sudo chown $USER:$USER /var/log/deliver-management
   ```

3. 运行应用：
   ```bash
   java -jar target/deliver-management-1.0.0.jar --spring.profiles.active=prod
   ```

#### 3. 前端部署
1. 构建项目：
   ```bash
   npm run build
   ```

2. 将 `dist` 目录下的文件部署到 Nginx 或其他 Web 服务器

### 日志配置说明

应用默认会在以下位置生成日志文件：

1. 开发环境：项目根目录下的 `logs/deliver-management.log`
2. 生产环境：`/var/log/deliver-management/deliver-management.log`

日志文件会按大小(10MB)自动轮转，并保留最近30个历史文件。
