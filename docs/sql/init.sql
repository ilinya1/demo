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
    `emergency_phone` VARCHAR(20)         COMMENT '紧急联系人电话',
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
    `phone`      VARCHAR(20)          COMMENT '联系电话（个人中心展示/修改）',
    `email`      VARCHAR(50)          COMMENT '联系邮箱（个人中心展示/修改）',
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

-- ---------------------------------------------------------------------
-- 12. 系统参数 sys_parameter
--    个人中心页面上的系统设置：系统名称/登录欢迎语/联系电话/联系邮箱。
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `sys_parameter`;
CREATE TABLE `sys_parameter` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '参数ID',
    `param_key`   VARCHAR(50) NOT NULL COMMENT '参数键，业务唯一',
    `param_name`  VARCHAR(100)         COMMENT '参数名称',
    `param_value` VARCHAR(255)         COMMENT '参数值',
    `updated_at`  DATETIME             COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_param_key` (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数';

-- ---------------------------------------------------------------------
-- 13. 学院 t_college
--    学院字典表。class.college / student.college 以其 name 作字符串软关联（无物理外键），
--    完整性由后端 CollegeService 校验（改名级联班级/学生、删除前引用拦截）。
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS `t_college`;
CREATE TABLE `t_college` (
    `id`         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '学院ID',
    `name`       VARCHAR(50) NOT NULL COMMENT '学院名称，唯一',
    `sort`       INT         NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
    `created_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_college_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院';

-- =====================================================================
-- 示例数据（演示账号 / 基础数据，便于一键演示）
-- =====================================================================

-- 系统参数（内置默认值，与原型一致）
INSERT INTO `sys_parameter` (`param_key`, `param_name`, `param_value`) VALUES
('systemName', '系统名称', '学生宿舍管理系统'),
('welcomeMessage', '登录欢迎语', '欢迎使用学生宿舍管理系统'),
('contactPhone', '联系电话', '0571-88888888'),
('contactEmail', '联系邮箱', 'dorm@example.edu.cn')
ON DUPLICATE KEY UPDATE `param_name` = VALUES(`param_name`);

-- 报修类型字典（对应原型 repair-add 下拉：灯管/水龙头/空调/门锁/床铺/桌椅/其他）
INSERT INTO `repair_type` (`id`, `name`, `sort`) VALUES
(1, '灯管', 1),
(2, '水龙头', 2),
(3, '空调', 3),
(4, '门锁', 4),
(5, '床铺', 5),
(6, '桌椅', 6),
(7, '其他', 99);

-- 学院（对齐 class.college / student.college 现有值，保证下拉与现存班级吻合）
INSERT INTO `t_college` (`name`, `sort`) VALUES
('计算机学院', 1),
('机电学院', 2),
('外国语学院', 3)
ON DUPLICATE KEY UPDATE `sort` = VALUES(`sort`);

-- 班级（8 个，覆盖 计算机/机电/外国语 学院，2023/2024/2022 级）
INSERT INTO `class` (`id`, `class_name`, `college`, `major`, `grade`, `head_teacher`) VALUES
(1, '软工2301', '计算机学院', '软件工程', '2023', '张老师'),
(2, '软工2302', '计算机学院', '软件工程', '2023', '李老师'),
(3, '计科2301', '计算机学院', '计算机科学与技术', '2023', '王老师'),
(4, '计科2302', '计算机学院', '计算机科学与技术', '2023', '刘老师'),
(5, '机设2301', '机电学院', '机械设计制造及其自动化', '2023', '赵老师'),
(6, '机电2301', '机电学院', '电气工程及其自动化', '2024', '周老师'),
(7, '英语2201', '外国语学院', '英语', '2022', '孙老师'),
(8, '日语2201', '外国语学院', '日语', '2022', '吴老师');

-- 学生（24 人，每班 3 人；演示账号 2023010101~0103 保留登录，其余无账号可重置密码自动建号）
INSERT INTO `student` (`student_id`, `name`, `gender`, `college`, `major`, `class_id`, `contact_phone`, `emergency_contact`, `emergency_phone`, `academic_status`, `housing_status`) VALUES
('2023010101', '王小明', '男', '计算机学院', '软件工程', 1, '13800000001', '王父',   '13911110001', '在校', '在住'),
('2023010102', '李小红', '女', '计算机学院', '计算机科学与技术', 3, '13800000002', '李父', '13911110002', '在校', '在住'),
('2023010103', '陈强',   '男', '计算机学院', '软件工程', 1, '13800000003', '陈父',   '13911110003', '在校', '在住'),
('2023010201', '张伟',   '男', '计算机学院', '软件工程', 2, '13800010001', '张父',   '13911120001', '在校', '在住'),
('2023010202', '刘芳',   '女', '计算机学院', '软件工程', 2, '13800010002', '刘父',   '13911120002', '在校', '已退宿'),
('2023010203', '赵磊',   '男', '计算机学院', '软件工程', 2, '13800010003', '赵父',   '13911120003', '在校', '未住'),
('2023010301', '孙丽',   '女', '计算机学院', '计算机科学与技术', 3, '13800020001', '孙父', '13911130001', '在校', '在住'),
('2023010302', '周涛',   '男', '计算机学院', '计算机科学与技术', 3, '13800020002', '周父', '13911130002', '在校', '在住'),
('2023010303', '吴丹',   '女', '计算机学院', '计算机科学与技术', 3, '13800020003', '吴父', '13911130003', '在校', '已退宿'),
('2023010401', '郑强',   '男', '计算机学院', '计算机科学与技术', 4, '13800030001', '郑父', '13911140001', '在校', '在住'),
('2023010402', '王敏',   '女', '计算机学院', '计算机科学与技术', 4, '13800030002', '王父', '13911140002', '在校', '未住'),
('2023010403', '李娜',   '女', '计算机学院', '计算机科学与技术', 4, '13800030003', '李父', '13911140003', '在校', '在住'),
('2023010501', '钱进',   '男', '机电学院', '机械设计制造及其自动化', 5, '13800040001', '钱父', '13911150001', '在校', '在住'),
('2023010502', '孙瑶',   '女', '机电学院', '机械设计制造及其自动化', 5, '13800040002', '孙父', '13911150002', '在校', '已退宿'),
('2023010503', '冯波',   '男', '机电学院', '机械设计制造及其自动化', 5, '13800040003', '冯父', '13911150003', '在校', '在住'),
('2024010601', '杜梅',   '女', '机电学院', '电气工程及其自动化', 6, '13800050001', '杜父', '13911160001', '在校', '在住'),
('2024010602', '郭强',   '男', '机电学院', '电气工程及其自动化', 6, '13800050002', '郭父', '13911160002', '在校', '未住'),
('2024010603', '何亮',   '男', '机电学院', '电气工程及其自动化', 6, '13800050003', '何父', '13911160003', '在校', '在住'),
('2022010701', '高媛',   '女', '外国语学院', '英语', 7, '13800060001', '高父', '13911170001', '在校', '在住'),
('2022010702', '林峰',   '男', '外国语学院', '英语', 7, '13800060002', '林父', '13911170002', '在校', '已退宿'),
('2022010703', '徐静',   '女', '外国语学院', '英语', 7, '13800060003', '徐父', '13911170003', '在校', '在住'),
('2022010801', '欧阳俊', '男', '外国语学院', '日语', 8, '13800070001', '欧阳父', '13911180001', '在校', '在住'),
('2022010802', '魏华',   '女', '外国语学院', '日语', 8, '13800070002', '魏父', '13911180002', '在校', '未住'),
('2022010803', '蒋雪',   '女', '外国语学院', '日语', 8, '13800070003', '蒋父', '13911180003', '在校', '在住');

-- 登录账号（演示密码均为 123456，用 {noop} 明文占位；生产请改 BCrypt 密文）
-- 保留 3 个演示学生账号；其余学生无账号，可由管理员「重置密码」自动建号
INSERT INTO `sys_user` (`username`, `password`, `role`, `student_id`) VALUES
('admin', '{noop}123456', 'ADMIN', NULL),
('2023010101', '{noop}123456', 'STUDENT', '2023010101'),
('2023010102', '{noop}123456', 'STUDENT', '2023010102'),
('2023010103', '{noop}123456', 'STUDENT', '2023010103');

-- 楼栋 / 房间 / 床位（3 栋楼，每栋 2 层 6 间，每间 4 床）
INSERT INTO `dorm_building` (`id`, `building_name`, `floor_count`, `room_count`, `manager`) VALUES
(1, '1号楼', 2, 6, '张宿管'),
(2, '2号楼', 2, 6, '李宿管'),
(3, '3号楼', 2, 6, '王宿管');

INSERT INTO `dorm_room` (`id`, `building_id`, `floor`, `room_no`, `capacity`, `room_type`, `status`) VALUES
(1, 1, 1, '101', 4, '四人间', '部分入住'),
(2, 1, 1, '102', 4, '四人间', '部分入住'),
(3, 1, 1, '103', 4, '四人间', '部分入住'),
(4, 1, 2, '201', 4, '四人间', '部分入住'),
(5, 1, 2, '202', 4, '四人间', '部分入住'),
(6, 1, 2, '203', 4, '四人间', '空闲'),
(7, 2, 1, '101', 4, '四人间', '部分入住'),
(8, 2, 1, '102', 4, '四人间', '部分入住'),
(9, 2, 1, '103', 4, '四人间', '部分入住'),
(10, 2, 2, '201', 4, '四人间', '部分入住'),
(11, 2, 2, '202', 4, '四人间', '空闲'),
(12, 2, 2, '203', 4, '四人间', '空闲'),
(13, 3, 1, '101', 4, '四人间', '空闲'),
(14, 3, 1, '102', 4, '四人间', '空闲'),
(15, 3, 1, '103', 4, '四人间', '空闲'),
(16, 3, 2, '201', 4, '四人间', '空闲'),
(17, 3, 2, '202', 4, '四人间', '空闲'),
(18, 3, 2, '203', 4, '四人间', '空闲');

-- 床位（72 个；1号楼101/102/103/201/202、2号楼101/102/103/201 部分被占用，其余空闲；退宿过的 4 号/7号/14号/27号床为空闲）
INSERT INTO `dorm_bed` (`id`, `room_id`, `bed_no`, `status`) VALUES
(1, 1, '1号床', '占用'), (2, 1, '2号床', '占用'), (3, 1, '3号床', '占用'), (4, 1, '4号床', '空闲'),
(5, 2, '1号床', '占用'), (6, 2, '2号床', '占用'), (7, 2, '3号床', '空闲'), (8, 2, '4号床', '空闲'),
(9, 3, '1号床', '占用'), (10, 3, '2号床', '空闲'), (11, 3, '3号床', '空闲'), (12, 3, '4号床', '空闲'),
(13, 4, '1号床', '占用'), (14, 4, '2号床', '空闲'), (15, 4, '3号床', '空闲'), (16, 4, '4号床', '空闲'),
(17, 5, '1号床', '占用'), (18, 5, '2号床', '占用'), (19, 5, '3号床', '空闲'), (20, 5, '4号床', '空闲'),
(21, 6, '1号床', '空闲'), (22, 6, '2号床', '空闲'), (23, 6, '3号床', '空闲'), (24, 6, '4号床', '空闲'),
(25, 7, '1号床', '占用'), (26, 7, '2号床', '占用'), (27, 7, '3号床', '空闲'), (28, 7, '4号床', '空闲'),
(29, 8, '1号床', '占用'), (30, 8, '2号床', '空闲'), (31, 8, '3号床', '空闲'), (32, 8, '4号床', '空闲'),
(33, 9, '1号床', '占用'), (34, 9, '2号床', '占用'), (35, 9, '3号床', '空闲'), (36, 9, '4号床', '空闲'),
(37, 10, '1号床', '占用'), (38, 10, '2号床', '占用'), (39, 10, '3号床', '空闲'), (40, 10, '4号床', '空闲'),
(41, 11, '1号床', '空闲'), (42, 11, '2号床', '空闲'), (43, 11, '3号床', '空闲'), (44, 11, '4号床', '空闲'),
(45, 12, '1号床', '空闲'), (46, 12, '2号床', '空闲'), (47, 12, '3号床', '空闲'), (48, 12, '4号床', '空闲'),
(49, 13, '1号床', '空闲'), (50, 13, '2号床', '空闲'), (51, 13, '3号床', '空闲'), (52, 13, '4号床', '空闲'),
(53, 14, '1号床', '空闲'), (54, 14, '2号床', '空闲'), (55, 14, '3号床', '空闲'), (56, 14, '4号床', '空闲'),
(57, 15, '1号床', '空闲'), (58, 15, '2号床', '空闲'), (59, 15, '3号床', '空闲'), (60, 15, '4号床', '空闲'),
(61, 16, '1号床', '空闲'), (62, 16, '2号床', '空闲'), (63, 16, '3号床', '空闲'), (64, 16, '4号床', '空闲'),
(65, 17, '1号床', '空闲'), (66, 17, '2号床', '空闲'), (67, 17, '3号床', '空闲'), (68, 17, '4号床', '空闲'),
(69, 18, '1号床', '空闲'), (70, 18, '2号床', '空闲'), (71, 18, '3号床', '空闲'), (72, 18, '4号床', '空闲');

-- 入住/退宿记录（20 条：在住 16、已退宿 4；快照与主数据一致；退宿来源 apply/direct）
INSERT INTO `check_in`
(`id`, `student_id`, `building_id`, `room_id`, `bed_id`,
 `student_name`, `class_name`, `building_name`, `room_no`, `bed_no`,
 `check_in_time`, `check_out_time`, `source`, `status`, `remark`) VALUES
(1,  '2023010101', 1, 1, 1,  '王小明', '软工2301', '1号楼', '101', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(2,  '2023010102', 1, 1, 2,  '李小红', '计科2301', '1号楼', '101', '2号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(3,  '2023010103', 1, 1, 3,  '陈强',   '软工2301', '1号楼', '101', '3号床', '2023-09-02 08:00:00', NULL, NULL, '在住', NULL),
(4,  '2023010201', 1, 2, 5,  '张伟',   '软工2302', '1号楼', '102', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(5,  '2023010302', 1, 2, 6,  '周涛',   '计科2301', '1号楼', '102', '2号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(6,  '2023010501', 1, 3, 9,  '钱进',   '机设2301', '1号楼', '103', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(7,  '2023010301', 1, 4, 13, '孙丽',   '计科2301', '1号楼', '201', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(8,  '2023010401', 1, 5, 17, '郑强',   '计科2302', '1号楼', '202', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(9,  '2023010403', 1, 5, 18, '李娜',   '计科2302', '1号楼', '202', '2号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(10, '2024010601', 2, 7, 25, '杜梅',   '机电2301', '2号楼', '101', '1号床', '2024-09-01 08:00:00', NULL, NULL, '在住', NULL),
(11, '2024010603', 2, 7, 26, '何亮',   '机电2301', '2号楼', '101', '2号床', '2024-09-01 08:00:00', NULL, NULL, '在住', NULL),
(12, '2023010503', 2, 8, 29, '冯波',   '机设2301', '2号楼', '102', '1号床', '2023-09-01 08:00:00', NULL, NULL, '在住', NULL),
(13, '2022010701', 2, 9, 33, '高媛',   '英语2201', '2号楼', '103', '1号床', '2022-09-01 08:00:00', NULL, NULL, '在住', NULL),
(14, '2022010703', 2, 9, 34, '徐静',   '英语2201', '2号楼', '103', '2号床', '2022-09-01 08:00:00', NULL, NULL, '在住', NULL),
(15, '2022010801', 2, 10, 37, '欧阳俊', '日语2201', '2号楼', '201', '1号床', '2022-09-01 08:00:00', NULL, NULL, '在住', NULL),
(16, '2022010803', 2, 10, 38, '蒋雪',   '日语2201', '2号楼', '201', '2号床', '2022-09-01 08:00:00', NULL, NULL, '在住', NULL),
(17, '2023010202', 1, 1, 4,  '刘芳',   '软工2302', '1号楼', '101', '4号床', '2023-09-01 08:00:00', '2026-06-28 10:00:00', 'apply', '已退宿', '毕业离校'),
(18, '2023010303', 1, 2, 7,  '吴丹',   '计科2301', '1号楼', '102', '3号床', '2023-09-02 08:00:00', '2026-06-25 15:00:00', 'direct', '已退宿', '管理员直接退宿'),
(19, '2023010502', 1, 4, 14, '孙瑶',   '机设2301', '1号楼', '201', '2号床', '2023-09-01 08:00:00', '2026-06-30 09:00:00', 'apply', '已退宿', '外出实习'),
(20, '2022010702', 2, 7, 27, '林峰',   '英语2201', '2号楼', '101', '3号床', '2022-09-01 08:00:00', '2026-06-22 14:00:00', 'direct', '已退宿', '管理员直接退宿');

-- 退宿申请（6 条：待审核 / 已通过 / 已驳回，引用在住学生）
INSERT INTO `checkout_apply`
(`id`, `apply_no`, `student_id`, `reason`, `plan_date`, `description`, `status`, `reject_reason`, `create_time`, `audit_time`) VALUES
(1, 'CK2026090001', '2023010302', '外出实习', '2026-06-30', '因外出实习需要退宿', '待审核', NULL, '2026-09-01 09:00:00', NULL),
(2, 'CK2026090002', '2023010403', '家庭原因', '2026-07-15', '家庭特殊情况，需回家住宿', '待审核', NULL, '2026-09-01 10:00:00', NULL),
(3, 'CK2026090003', '2024010603', '其他', '2026-06-20', '个人原因', '已通过', NULL, '2026-09-02 09:30:00', '2026-09-02 11:00:00'),
(4, 'CK2026090004', '2022010701', '毕业离校', '2026-06-25', '预计月底离校', '待审核', NULL, '2026-09-02 14:00:00', NULL),
(5, 'CK2026090005', '2022010801', '毕业离校', '2026-06-28', '毕业后离校', '待审核', NULL, '2026-09-03 09:00:00', NULL),
(6, 'CK2026090006', '2023010503', '疾病治疗', '2026-08-30', '因身体原因需回家治疗', '已驳回', '请补充医院证明材料', '2026-09-03 15:00:00', '2026-09-04 09:00:00');

-- 卫生检查（9 条：优秀/合格/不合格，扣分项与评分结果一致）
INSERT INTO `hygiene_record`
(`id`, `check_date`, `checker`, `building_id`, `room_id`, `score`, `result`, `deduct_items`, `photos`, `comment`) VALUES
(1, '2026-08-28', '张老师', 1, 1, 95, '优秀', '[]', '[]', '整洁'),
(2, '2026-08-28', '王老师', 1, 2, 78, '合格', '["被子未叠"]', '["/uploads/hygiene/h2.jpg"]', '被子未叠，其余尚可'),
(3, '2026-08-29', '李老师', 1, 3, 82, '合格', '["桌面杂乱"]', '["/uploads/hygiene/h3.jpg"]', '桌面略乱'),
(4, '2026-08-29', '张老师', 1, 4, 88, '合格', '["垃圾未倒"]', '["/uploads/hygiene/h4.jpg"]', '整体较好'),
(5, '2026-09-02', '张老师', 1, 5, 92, '优秀', '[]', '[]', '优秀'),
(6, '2026-09-03', '李老师', 2, 7, 96, '优秀', '[]', '[]', '出众'),
(7, '2026-09-03', '王老师', 2, 8, 58, '不合格', '["违规电器","地面不干净"]', '["/uploads/hygiene/h7.jpg"]', '发现违规电器'),
(8, '2026-09-04', '张老师', 2, 9, 74, '合格', '["垃圾未倒"]', '["/uploads/hygiene/h8.jpg"]', '垃圾未倒'),
(9, '2026-09-04', '李老师', 2, 10, 90, '优秀', '[]', '[]', '干净');

-- 报修（12 条：待处理/处理中/已完成，类型/房间/学生均有效）
INSERT INTO `repair_order`
(`id`, `order_no`, `student_id`, `building_id`, `room_id`, `type_id`, `description`, `contact_phone`, `images`, `status`, `handler_name`, `handler_phone`, `handle_desc`, `create_time`, `handle_time`) VALUES
(1,  'RO2026090001', '2023010101', 1, 1, 1, '灯管不亮，请求更换', '13800000001', '[]', '待处理', NULL, NULL, NULL, '2026-09-01 10:00:00', NULL),
(2,  'RO2026090002', '2023010302', 1, 2, 4, '房门锁芯松动，开关困难', '13800000002', '[]', '处理中', '张师傅', '13800000000', '已安排维修，正在处理', '2026-09-01 11:00:00', '2026-09-05 09:00:00'),
(3,  'RO2026090003', '2023010501', 1, 3, 2, '水龙头漏水不停', '13800000003', '[]', '处理中', '李师傅', '13800000000', '已安排维修', '2026-09-02 09:00:00', '2026-09-05 10:00:00'),
(4,  'RO2026090004', '2024010601', 2, 7, 3, '空调制冷效果差，出风不冷', '13800000004', '[]', '待处理', NULL, NULL, NULL, '2026-09-02 14:00:00', NULL),
(5,  'RO2026090005', '2022010701', 2, 9, 5, '上铺床板异响', '13800000005', '[]', '已完成', '赵师傅', '13800000000', '已加固床板', '2026-09-02 15:00:00', '2026-09-03 10:00:00'),
(6,  'RO2026090006', '2022010801', 2, 10, 6, '书桌抽屉滑轨脱落', '13800000006', '[]', '已完成', '赵师傅', '13800000000', '已更换滑轨', '2026-09-03 09:00:00', '2026-09-03 16:00:00'),
(7,  'RO2026090007', '2023010102', 1, 1, 1, '卫生间灯闪烁', '13800000007', '[]', '处理中', '李师傅', '13800000000', '正在排查线路', '2026-09-03 11:00:00', '2026-09-04 10:00:00'),
(8,  'RO2026090008', '2023010401', 1, 5, 7, '窗户关不严，透风', '13800000008', '[]', '待处理', NULL, NULL, NULL, '2026-09-04 09:00:00', NULL),
(9,  'RO2026090009', '2024010603', 2, 7, 2, '水龙头滴水', '13800000009', '[]', '已完成', '张师傅', '13800000000', '已更换密封圈', '2026-09-04 11:00:00', '2026-09-04 16:00:00'),
(10, 'RO2026090010', '2022010703', 2, 9, 4, '门吸破损', '13800000010', '[]', '待处理', NULL, NULL, NULL, '2026-09-05 09:00:00', NULL),
(11, 'RO2026090011', '2023010301', 1, 4, 3, '空调遥控失灵', '13800000011', '[]', '已完成', '李师傅', '13800000000', '已更换遥控器', '2026-09-05 10:00:00', '2026-09-06 09:00:00'),
(12, 'RO2026090012', '2023010201', 1, 2, 1, '阳台灯不亮', '13800000012', '[]', '处理中', '张师傅', '13800000000', '已领取灯泡', '2026-09-05 14:00:00', '2026-09-06 10:00:00');

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
--    t_college.name -> class.college / student.college（字典【字符串】软关联，非 _id 逻辑外键；改名级联、删除拦截由 CollegeService 校验）
-- 2. 状态枚举统一：
--    学籍：在校/毕业/退学/休学；住宿：在住/已退宿/未住
--    房间：空闲/部分入住/已满/维修中；床位：空闲/占用/维修
--    退宿申请：待审核/已通过/已驳回；报修：待处理/处理中/已完成
--    卫生：优秀/合格/不合格；账号角色：ADMIN/STUDENT
-- 3. 后勤冗余：student.housing_status 由 check_in 同步维护。
-- 4. 加固项：check_in/hygiene_record/repair_order 关联的 building/room/bed_id 均为 NOT NULL（业务必含）；sys_user.student_id 唯一（防重复建号）。
-- 5. 一致性约定：dorm_room.capacity 与 dorm_bed 行数应一致，由 Service 在新建房间时保证（无物理约束）。
-- =====================================================================