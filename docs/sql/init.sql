-- =====================================================================
-- 学生宿舍管理系统 数据库初始化脚本
-- MySQL 8.0+ / utf8mb4 / InnoDB
-- 规范：所有关联使用【逻辑外键】（不加 FOREIGN KEY），
--       字段名以 _id 结尾的为逻辑外键，完整性由后端 Service 校验。
--       字段字典 / ER 关系请同步维护《docs/数据库设计说明.md》。
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `dorm_manager`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE `dorm_manager`;

-- ---------------------------------------------------------------------
-- 1. 班级 class
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '班级ID',
    `class_name`   VARCHAR(50)  NOT NULL COMMENT '班级名称，如 软工2301',
    `college`      VARCHAR(50)           COMMENT '所属学院',
    `major`        VARCHAR(50)           COMMENT '专业',
    `grade`        VARCHAR(10)           COMMENT '年级',
    `head_teacher` VARCHAR(50)           COMMENT '班主任',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_name` (`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级';

-- ---------------------------------------------------------------------
-- 2. 学生 student
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
    `student_id`     VARCHAR(20) NOT NULL COMMENT '学号，主键',
    `name`           VARCHAR(50) NOT NULL COMMENT '姓名',
    `gender`         VARCHAR(2)  NOT NULL COMMENT '性别：男/女',
    `college`        VARCHAR(50)          COMMENT '学院',
    `major`          VARCHAR(50)          COMMENT '专业',
    `class_id`       BIGINT               COMMENT '逻辑外键 -> class.id',
    `contact_phone`  VARCHAR(20)          COMMENT '联系方式',
    `emergency_contact` VARCHAR(50)       COMMENT '紧急联系人',
    `academic_status` VARCHAR(10) NOT NULL DEFAULT '在校' COMMENT '学籍状态：在校/毕业/退学/休学',
    `housing_status` VARCHAR(10)  NOT NULL DEFAULT '未住' COMMENT '住宿状态：在住/已退宿/未住（冗余，随 check_in 同步）',
    `created_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`student_id`),
    KEY `idx_student_class` (`class_id`),
    KEY `idx_student_phone` (`contact_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生';

-- ---------------------------------------------------------------------
-- 3. 登录账号 sys_user
--    管理员 role=ADMIN（student_id 为 NULL）；学生 role=STUDENT（关联学号）
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '账号ID',
    `username`   VARCHAR(50) NOT NULL COMMENT '登录名：管理员账号 / 学生学号',
    `password`   VARCHAR(100) NOT NULL COMMENT '密码（演示样本为 {noop} 明文，生产用 BCrypt 密文）',
    `role`       VARCHAR(10) NOT NULL COMMENT '角色：ADMIN / STUDENT（预留扩展 TEACHER）',
    `student_id` VARCHAR(20)          COMMENT '逻辑外键 -> student.student_id；管理员为 NULL',
    `status`     TINYINT     NOT NULL DEFAULT 1 COMMENT '状态：1启用 / 0停用',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_user_student` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录账号';

-- ---------------------------------------------------------------------
-- 4. 楼栋 dorm_building
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dorm_building`;
CREATE TABLE `dorm_building` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
    `building_name` VARCHAR(50) NOT NULL COMMENT '楼栋名称，如 1号楼',
    `floor_count`   INT         NOT NULL DEFAULT 0 COMMENT '楼层数',
    `room_count`    INT         NOT NULL DEFAULT 0 COMMENT '房间数',
    `manager`       VARCHAR(50)          COMMENT '楼栋管理员',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_name` (`building_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋';

-- ---------------------------------------------------------------------
-- 5. 房间 dorm_room
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dorm_room`;
CREATE TABLE `dorm_room` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    `building_id` BIGINT      NOT NULL COMMENT '逻辑外键 -> dorm_building.id',
    `floor`       INT         NOT NULL COMMENT '楼层',
    `room_no`     VARCHAR(20) NOT NULL COMMENT '房间号，如 102',
    `capacity`    INT         NOT NULL DEFAULT 4 COMMENT '床位容量',
    `room_type`   VARCHAR(20)          COMMENT '房间类型，如 四人间',
    `status`      VARCHAR(10) NOT NULL DEFAULT '空闲' COMMENT '状态：空闲/部分入住/已满/维修中',
    `created_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_room` (`building_id`, `room_no`),
    KEY `idx_room_building` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间';

-- ---------------------------------------------------------------------
-- 6. 床位 dorm_bed
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `dorm_bed`;
CREATE TABLE `dorm_bed` (
    `id`      BIGINT      NOT NULL AUTO_INCREMENT COMMENT '床位ID',
    `room_id` BIGINT      NOT NULL COMMENT '逻辑外键 -> dorm_room.id',
    `bed_no`  VARCHAR(10) NOT NULL COMMENT '床位号，如 1号床',
    `status`  VARCHAR(10) NOT NULL DEFAULT '空闲' COMMENT '状态：空闲/占用/维修',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bed` (`room_id`, `bed_no`),
    KEY `idx_bed_room` (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位';

-- ---------------------------------------------------------------------
-- 7. 入住/退宿记录 check_in
--    同时承载入住与退宿：含 check_out_time/source 区分退宿来源。
--    快照字段：入住时写入当时的名称快照，退宿不覆盖，保证历史可还原。
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `check_in`;
CREATE TABLE `check_in` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    -- 关联
    `student_id`    VARCHAR(20) NOT NULL COMMENT '逻辑外键 -> student.student_id',
    `building_id`   BIGINT      NOT NULL COMMENT '逻辑外键 -> dorm_building.id',
    `room_id`       BIGINT      NOT NULL COMMENT '逻辑外键 -> dorm_room.id',
    `bed_id`        BIGINT      NOT NULL COMMENT '逻辑外键 -> dorm_bed.id',
    -- 快照（入住时写入，不随主数据变更）
    `student_name`  VARCHAR(50)          COMMENT '快照：学生姓名',
    `class_name`    VARCHAR(50)          COMMENT '快照：班级名称',
    `building_name` VARCHAR(50)          COMMENT '快照：楼栋名称',
    `room_no`       VARCHAR(20)          COMMENT '快照：房间号',
    `bed_no`        VARCHAR(10)          COMMENT '快照：床位号',
    -- 业务
    `check_in_time` DATETIME    NOT NULL COMMENT '入住时间',
    `check_out_time` DATETIME            COMMENT '退宿时间（未退宿为 NULL）',
    `source`        VARCHAR(10)          COMMENT '退宿来源：apply(学生申请审核通过)/direct(管理员直接退宿)',
    `status`        VARCHAR(10) NOT NULL DEFAULT '在住' COMMENT '状态：在住/已退宿',
    `remark`        VARCHAR(200)         COMMENT '备注',
    `created_at`    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_ci_student` (`student_id`, `status`),
    KEY `idx_ci_room` (`room_id`),
    KEY `idx_ci_bed` (`bed_id`),
    KEY `idx_ci_status` (`status`, `check_out_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住/退宿记录';

-- ---------------------------------------------------------------------
-- 8. 退宿申请 checkout_apply
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `checkout_apply`;
CREATE TABLE `checkout_apply` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `apply_no`     VARCHAR(30)  NOT NULL COMMENT '申请编号，业务唯一',
    `student_id`   VARCHAR(20)  NOT NULL COMMENT '逻辑外键 -> student.student_id',
    `reason`       VARCHAR(500)          COMMENT '退宿原因',
    `plan_date`    DATE                  COMMENT '计划退宿日期',
    `description`  VARCHAR(500)          COMMENT '申请说明',
    `status`       VARCHAR(10)  NOT NULL DEFAULT '待审核' COMMENT '状态：待审核/已通过/已驳回',
    `reject_reason` VARCHAR(500)         COMMENT '驳回意见',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `audit_time`   DATETIME              COMMENT '审核时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_apply_no` (`apply_no`),
    KEY `idx_ca_student` (`student_id`),
    KEY `idx_ca_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退宿申请';

-- ---------------------------------------------------------------------
-- 9. 卫生检查 hygiene_record
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `hygiene_record`;
CREATE TABLE `hygiene_record` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `check_date`   DATE         NOT NULL COMMENT '检查日期',
    `checker`      VARCHAR(50)           COMMENT '检查人',
    `building_id`  BIGINT       NOT NULL COMMENT '逻辑外键 -> dorm_building.id',
    `room_id`      BIGINT       NOT NULL COMMENT '逻辑外键 -> dorm_room.id',
    `score`        INT                   COMMENT '评分（100 起扣）',
    `result`       VARCHAR(10)           COMMENT '结果：优秀/合格/不合格',
    `deduct_items` TEXT                  COMMENT '扣分项（JSON 数组，如 ["地面不干净","被子未叠"]）',
    `photos`       TEXT                  COMMENT '照片路径列表（JSON 数组，一期本地目录/二期 OSS）',
    `comment`      VARCHAR(500)          COMMENT '评语',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_h_room` (`room_id`, `check_date`),
    KEY `idx_h_building` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卫生检查';

-- ---------------------------------------------------------------------
-- 10. 报修类型字典 repair_type
--    报修"报修物品/类型"独立成表，便于下拉从字典读取与按类别统计。
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `repair_type`;
CREATE TABLE `repair_type` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '报修类型ID',
    `name`       VARCHAR(50) NOT NULL COMMENT '报修物品/类型名称，如 灯管',
    `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rt_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修类型';

-- ---------------------------------------------------------------------
-- 11. 报修 repair_order
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `repair_order`;
CREATE TABLE `repair_order` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报修ID',
    `order_no`      VARCHAR(30)  NOT NULL COMMENT '报修单号，业务唯一',
    `student_id`    VARCHAR(20)  NOT NULL COMMENT '逻辑外键 -> student.student_id',
    `building_id`   BIGINT       NOT NULL COMMENT '逻辑外键 -> dorm_building.id',
    `room_id`       BIGINT       NOT NULL COMMENT '逻辑外键 -> dorm_room.id',
    `type_id`       BIGINT                COMMENT '逻辑外键 -> repair_type.id；报修物品/类型',
    `description`   VARCHAR(500)          COMMENT '问题描述',
    `contact_phone` VARCHAR(20)           COMMENT '联系电话（提交时留的电话）',
    `images`        TEXT                  COMMENT '图片路径列表（JSON 数组）',
    `status`        VARCHAR(10)  NOT NULL DEFAULT '待处理' COMMENT '状态：待处理/处理中/已完成',
    `handler_name`  VARCHAR(50)           COMMENT '处理人',
    `handler_phone` VARCHAR(20)           COMMENT '处理人电话',
    `handle_desc`   VARCHAR(500)          COMMENT '处理说明',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    `handle_time`   DATETIME              COMMENT '处理时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_ro_student` (`student_id`),
    KEY `idx_ro_status` (`status`),
    KEY `idx_ro_room` (`room_id`),
    KEY `idx_ro_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修';

-- =====================================================================
-- 示例数据（演示账号 / 基础数据，便于一键演示）
-- =====================================================================

-- 报修类型字典（对应原型 repair-add 下拉：灯管/水龙头/空调/门锁/床铺/桌椅/其他）
INSERT INTO `repair_type` (`id`, `name`, `sort`) VALUES
(1, '灯管', 1),
(2, '水龙头', 2),
(3, '空调', 3),
(4, '门锁', 4),
(5, '床铺', 5),
(6, '桌椅', 6),
(7, '其他', 99);

-- 班级
INSERT INTO `class` (`id`, `class_name`, `college`, `major`, `grade`, `head_teacher`) VALUES
(1, '软工2301', '计算机学院', '软件工程', '2023', '张老师'),
(2, '软工2302', '计算机学院', '软件工程', '2023', '李老师'),
(3, '计科2301', '计算机学院', '计算机科学与技术', '2023', '王老师'),
(4, '机设2301', '机电学院', '机械设计制造及其自动化', '2023', '赵老师'),
(5, '英语2201', '外国语学院', '英语', '2022', '孙老师');

-- 学生（与原型演示一致：2023010101 / 123456；三人入住 1号楼 102 室）
INSERT INTO `student` (`student_id`, `name`, `gender`, `college`, `major`, `class_id`, `contact_phone`, `emergency_contact`, `academic_status`, `housing_status`) VALUES
('2023010101', '王小明', '男', '计算机学院', '软件工程', 1, '13800000001', '王父',    '在校', '在住'),
('2023010102', '李小红', '女', '计算机学院', '计算机科学与技术', 3, '13800000002', '李父', '在校', '在住'),
('2023010103', '陈强',   '男', '计算机学院', '软件工程', 1, '13800000003', '陈父',    '在校', '在住');

-- 登录账号（演示密码均为 123456，用 {noop} 明文占位；生产请改 BCrypt 密文）
INSERT INTO `sys_user` (`username`, `password`, `role`, `student_id`) VALUES
('admin', '{noop}123456', 'ADMIN', NULL),
('2023010101', '{noop}123456', 'STUDENT', '2023010101'),
('2023010102', '{noop}123456', 'STUDENT', '2023010102'),
('2023010103', '{noop}123456', 'STUDENT', '2023010103');

-- 楼栋 / 房间 / 床位（1号楼 1层；102 四人间入住 3 人，与原型 room-detail 一致）
INSERT INTO `dorm_building` (`id`, `building_name`, `floor_count`, `room_count`, `manager`) VALUES
(1, '1号楼', 5, 20, '张宿管'),
(2, '2号楼', 5, 20, '李宿管');

INSERT INTO `dorm_room` (`id`, `building_id`, `floor`, `room_no`, `capacity`, `room_type`, `status`) VALUES
(1, 1, 1, '101', 4, '四人间', '空闲'),
(2, 1, 1, '102', 4, '四人间', '部分入住'),
(3, 1, 1, '103', 4, '四人间', '空闲');

INSERT INTO `dorm_bed` (`id`, `room_id`, `bed_no`, `status`) VALUES
(1, 1, '1号床', '空闲'), (2, 1, '2号床', '空闲'), (3, 1, '3号床', '空闲'), (4, 1, '4号床', '空闲'),
(5, 2, '1号床', '占用'), (6, 2, '2号床', '占用'), (7, 2, '3号床', '占用'), (8, 2, '4号床', '空闲'),
(9, 3, '1号床', '空闲'), (10, 3, '2号床', '空闲'), (11, 3, '3号床', '空闲'), (12, 3, '4号床', '空闲');

-- 入住记录（复刻原型 room-detail：102 室 1~3 号床，三人均【在住】，含快照）
INSERT INTO `check_in`
(`id`, `student_id`, `building_id`, `room_id`, `bed_id`,
 `student_name`, `class_name`, `building_name`, `room_no`, `bed_no`,
 `check_in_time`, `check_out_time`, `source`, `status`, `remark`) VALUES
(1, '2023010101', 1, 2, 5, '王小明', '软工2301', '1号楼', '102', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(2, '2023010102', 1, 2, 6, '李小红', '计科2301', '1号楼', '102', '2号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(3, '2023010103', 1, 2, 7, '陈强',   '软工2301', '1号楼', '102', '3号床', '2023-09-02 08:00:00', NULL, NULL, '在住', NULL);

-- =====================================================================
-- 备注（供后续开发）
-- 1. 逻辑外键对应关系（由后端 Service 校验，无物理约束）：
--    student.class_id          -> class.id
--    sys_user.student_id       -> student.student_id
--    dorm_room.building_id     -> dorm_building.id
--    dorm_bed.room_id          -> dorm_room.id
--    check_in.{student_id, building_id, room_id, bed_id}
--    checkout_apply.student_id -> student.student_id
--    hygiene_record.{building_id, room_id}
--    repair_order.{student_id, building_id, room_id, type_id -> repair_type.id}
-- 2. 状态枚举统一：
--    学籍：在校/毕业/退学/休学；住宿：在住/已退宿/未住
--    房间：空闲/部分入住/已满/维修中；床位：空闲/占用/维修
--    退宿申请：待审核/已通过/已驳回；报修：待处理/处理中/已完成
--    卫生：优秀/合格/不合格；账号角色：ADMIN/STUDENT
-- 3. 后勤冗余：student.housing_status 由 check_in 同步维护。
-- 4. 加固项：check_in/hygiene_record/repair_order 关联的 building/room/bed_id 均为 NOT NULL（业务必含）；sys_user.student_id 唯一（防重复建号）。
-- 5. 一致性约定：dorm_room.capacity 与 dorm_bed 行数应一致，由 Service 在新建房间时保证（无物理约束）。
-- =====================================================================