-- 创建数据库
CREATE DATABASE IF NOT EXISTS training_server DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- 使用数据库
USE training_server;

-- 创建企业表
CREATE TABLE IF NOT EXISTS enterprise (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '企业名称',
    contact VARCHAR(50) NOT NULL COMMENT '联系人',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(100) NOT NULL COMMENT '电子邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';

-- 创建用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    gender VARCHAR(10) COMMENT '性别',
    avatar LONGBLOB COMMENT '头像(BLOB存储)',
    enterprise_id BIGINT COMMENT '企业ID',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建角色表
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建权限表
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(50) NOT NULL COMMENT '权限编码',
    description VARCHAR(200) COMMENT '权限描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 清空并重新插入初始角色数据
DELETE FROM role;
INSERT INTO role (name, code, description) VALUES 
('超级管理员', 'ROLE_ADMIN', '系统超级管理员'),
('企业用户', 'ROLE_ENTERPRISE', '企业普通用户');

-- 清空并重新插入初始权限数据
DELETE FROM permission;
INSERT INTO permission (name, code, description) VALUES 
('用户管理', 'USER_MANAGE', '用户管理权限'),
('企业管理', 'ENTERPRISE_MANAGE', '企业管理权限'),
('个人信息管理', 'PROFILE_MANAGE', '个人信息管理权限'),
('行业动态发布', 'NEWS_PUBLISH', '发布行业动态'),
('行业动态审核', 'NEWS_AUDIT', '审核行业动态'),
('行业动态编辑', 'NEWS_EDIT', '编辑行业动态'),
('行业动态删除', 'NEWS_DELETE', '删除行业动态'),
('行业动态查看', 'NEWS_VIEW', '查看行业动态');

-- 清空并重新为超级管理员分配所有权限
DELETE FROM role_permission WHERE role_id = 1;
INSERT INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission;

-- 清空并重新为企业用户分配个人信息管理权限
DELETE FROM role_permission WHERE role_id = 2;
INSERT INTO role_permission (role_id, permission_id)
SELECT 2, id FROM permission WHERE code = 'PROFILE_MANAGE';

-- 超级管理员分配所有行业动态权限
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT 1, id FROM permission WHERE code IN ('NEWS_PUBLISH', 'NEWS_AUDIT', 'NEWS_EDIT', 'NEWS_DELETE', 'NEWS_VIEW');

-- 企业用户分配部分行业动态权限（不含审核）
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT 2, id FROM permission WHERE code IN ('NEWS_PUBLISH', 'NEWS_EDIT', 'NEWS_DELETE', 'NEWS_VIEW');

-- 插入测试企业数据
DELETE FROM enterprise;
INSERT INTO enterprise (name, contact, phone, email, status) VALUES
('阿里巴巴', '马云', '13800138001', 'alibaba@example.com', 1),
('腾讯科技', '马化腾', '13800138002', 'tencent@example.com', 1),
('百度在线', '李彦宏', '13800138003', 'baidu@example.com', 1),
('京东集团', '刘强东', '13800138004', 'jd@example.com', 1),
('小米科技', '雷军', '13800138005', 'xiaomi@example.com', 1);

-- 插入测试用户数据（密码都是123456）
DELETE FROM user;
INSERT INTO user (username, password, nickname, phone, email, gender, enterprise_id, status) VALUES
('admin', '123456', '系统管理员', '13900139001', 'admin@example.com', '男', NULL, 1),
('alibaba_user', '123456', '阿里员工', '13900139002', 'alibaba_user@example.com', '男', 1, 1),
('tencent_user', '123456', '腾讯员工', '13900139003', 'tencent_user@example.com', '女', 2, 1),
('baidu_user', '123456', '百度员工', '13900139004', 'baidu_user@example.com', '男', 3, 1),
('jd_user', '123456', '京东员工', '13900139005', 'jd_user@example.com', '女', 4, 1),
('xiaomi_user', '123456', '小米员工', '13900139006', 'xiaomi_user@example.com', '男', 5, 1);

-- 插入用户角色关联数据
DELETE FROM user_role;
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),  -- admin 是超级管理员
(2, 2),  -- 其他用户都是企业用户
(3, 2),
(4, 2),
(5, 2),
(6, 2);

-- 确保当前admin用户（user_id=7）分配为超级管理员
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (7, 1);

SET FOREIGN_KEY_CHECKS = 1;