-- =============================================
-- 学生宿舍管理系统 数据库建表脚本
-- 数据库：MySQL 8.x
-- 字符集：utf8mb4
-- 说明：本库使用逻辑外键（不建 FOREIGN KEY 约束），
--       关联完整性由后端 Service 层统一校验。
-- =============================================

CREATE DATABASE IF NOT EXISTS `dormitory` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `dormitory`;

-- =============================================
-- 1. 用户表 sys_user
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `role`        VARCHAR(20)  NOT NULL COMMENT '角色：ADMIN-管理员/STUDENT-学生',
    `student_id`  BIGINT       DEFAULT NULL COMMENT '关联学生ID（学生角色时）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用/0-禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 学生信息表 student
-- =============================================
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_no`        VARCHAR(20)  NOT NULL COMMENT '学号',
    `name`              VARCHAR(50)  NOT NULL COMMENT '姓名',
    `gender`            VARCHAR(10)  NOT NULL COMMENT '性别：男/女',
    `college`           VARCHAR(50)  NOT NULL COMMENT '学院',
    `major`             VARCHAR(50)  NOT NULL COMMENT '专业',
    `class_name`        VARCHAR(50)  NOT NULL COMMENT '班级',
    `phone`             VARCHAR(20)  DEFAULT NULL COMMENT '联系方式',
    `emergency_contact` VARCHAR(50)  DEFAULT NULL COMMENT '紧急联系人',
    `emergency_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '紧急联系电话',
    `status`            VARCHAR(20)  NOT NULL DEFAULT '在校' COMMENT '状态：在校/毕业/退学/休学',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_name` (`name`),
    KEY `idx_college` (`college`),
    KEY `idx_class_name` (`class_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生信息表';

-- =============================================
-- 3. 楼栋表 dorm_building
-- =============================================
DROP TABLE IF EXISTS `dorm_building`;
CREATE TABLE `dorm_building` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `building_no`   VARCHAR(20) NOT NULL COMMENT '楼栋编号',
    `building_name` VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    `floor_count`   INT         NOT NULL COMMENT '楼层数',
    `manager`       VARCHAR(50) DEFAULT NULL COMMENT '楼栋管理员',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_no` (`building_no`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋表';

-- =============================================
-- 4. 房间表 dorm_room
-- =============================================
DROP TABLE IF EXISTS `dorm_room`;
CREATE TABLE `dorm_room` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `building_id` BIGINT      NOT NULL COMMENT '所属楼栋ID',
    `room_no`     VARCHAR(20) NOT NULL COMMENT '房间号',
    `floor`       INT         NOT NULL COMMENT '楼层',
    `room_type`   VARCHAR(20) NOT NULL COMMENT '房间类型：四人间/六人间/八人间',
    `bed_count`   INT         NOT NULL COMMENT '床位容量',
    `status`      VARCHAR(20) NOT NULL DEFAULT '空闲' COMMENT '房间状态：空闲/部分入住/已满/维修中（前三种自动计算，维修中手动标记）',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_room` (`building_id`, `room_no`),
    KEY `idx_floor` (`floor`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- =============================================
-- 5. 入住记录表 check_in（含退宿信息）
-- =============================================
DROP TABLE IF EXISTS `check_in`;
CREATE TABLE `check_in` (
    `id`                BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id`        BIGINT      NOT NULL COMMENT '学生ID',
    `room_id`           BIGINT      NOT NULL COMMENT '房间ID',
    `bed_no`            VARCHAR(20) NOT NULL COMMENT '床位号（如：1号床）',
    `check_in_date`     DATE        NOT NULL COMMENT '入住日期',
    `remark`            VARCHAR(255) DEFAULT NULL COMMENT '入住备注',
    `status`            VARCHAR(20) NOT NULL DEFAULT '在住' COMMENT '状态：在住/已退宿',
    `check_out_date`    DATE        DEFAULT NULL COMMENT '退宿日期',
    `check_out_reason`  VARCHAR(50) DEFAULT NULL COMMENT '退宿原因：毕业离校/休学/退学/调宿/其他',
    `check_out_remark`  VARCHAR(255) DEFAULT NULL COMMENT '退宿备注',
    `create_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_status` (`status`),
    KEY `idx_check_in_date` (`check_in_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入住记录表（含退宿信息）';

-- =============================================
-- 6. 卫生检查记录表 hygiene_check
-- =============================================
DROP TABLE IF EXISTS `hygiene_check`;
CREATE TABLE `hygiene_check` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `check_date`      DATE         NOT NULL COMMENT '检查日期',
    `building_id`     BIGINT       NOT NULL COMMENT '楼栋ID',
    `room_id`         BIGINT       NOT NULL COMMENT '房间ID',
    `score`           INT          NOT NULL COMMENT '评分（0-100）',
    `result`          VARCHAR(20)  NOT NULL COMMENT '结果：优秀/合格/不合格（由评分自动判断，可手动调整）',
    `inspector`       VARCHAR(50)  NOT NULL COMMENT '检查人',
    `deduction_items` VARCHAR(255) DEFAULT NULL COMMENT '扣分项（逗号分隔，如：地面不干净,桌面杂乱）',
    `comment`         VARCHAR(500) DEFAULT NULL COMMENT '评语',
    `status`          VARCHAR(20)  NOT NULL DEFAULT '草稿' COMMENT '状态：草稿/已发布',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_building_id` (`building_id`),
    KEY `idx_check_date` (`check_date`),
    KEY `idx_result` (`result`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卫生检查记录表';

-- =============================================
-- 7. 报修单表 repair_order
-- =============================================
DROP TABLE IF EXISTS `repair_order`;
CREATE TABLE `repair_order` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `repair_no`     VARCHAR(30)   NOT NULL COMMENT '报修单号（如：BX20260901001）',
    `student_id`    BIGINT        NOT NULL COMMENT '报修学生ID',
    `room_id`       BIGINT        NOT NULL COMMENT '房间ID',
    `contact_phone` VARCHAR(20)   DEFAULT NULL COMMENT '联系电话（报修人提交时留存）',
    `item`          VARCHAR(50)   NOT NULL COMMENT '报修物品：灯管/水龙头/空调/门锁/床铺/桌椅/其他',
    `description`   VARCHAR(500)  NOT NULL COMMENT '问题描述',
    `images`        VARCHAR(1000) DEFAULT NULL COMMENT '图片路径（JSON数组）',
    `status`        VARCHAR(20)   NOT NULL DEFAULT '待处理' COMMENT '状态：待处理/处理中/已完成',
    `handler`       VARCHAR(50)   DEFAULT NULL COMMENT '处理人',
    `handle_remark` VARCHAR(500)  DEFAULT NULL COMMENT '处理说明',
    `handle_time`   DATETIME      DEFAULT NULL COMMENT '处理时间',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_repair_no` (`repair_no`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修单表';

-- =============================================
-- 8. 通知公告表 notice（预留，二期扩展）
-- =============================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `title`        VARCHAR(100) NOT NULL COMMENT '标题',
    `content`      TEXT         NULL COMMENT '内容',
    `publisher`    VARCHAR(50)  DEFAULT NULL COMMENT '发布人',
    `publish_time` DATETIME     DEFAULT NULL COMMENT '发布时间',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表（预留）';

-- =============================================
-- 初始化数据
-- =============================================

-- 默认管理员账号
-- 注意：以下 password 为占位哈希，实际部署时需通过后端注册/初始化接口生成真实 BCrypt 哈希
INSERT INTO `sys_user` (`username`, `password`, `role`, `status`) VALUES
('admin', 'PLACEHOLDER_BCRYPT_HASH', 'ADMIN', 1);

-- 示例楼栋数据
INSERT INTO `dorm_building` (`building_no`, `building_name`, `floor_count`, `manager`) VALUES
('1', '1号楼', 6, '张宿管'),
('2', '2号楼', 6, '李宿管'),
('3', '3号楼', 5, '王宿管'),
('4', '4号楼', 5, '赵宿管');