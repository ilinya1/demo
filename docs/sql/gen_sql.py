# -*- coding: utf-8 -*-
"""Generate docs/sql/init.sql with clean content (no invisible chars)."""
import pathlib

# Chinese literals built from unicode escapes to guarantee clean bytes
DORM = '\u5b66\u751f\u5bbf\u820d\u7ba1\u7406\u7cfb\u7edf'  # 学生宿舍管理系统
ZHUJIANID = '\u4e3b\u952eID'  # 主键ID
DENG_LU = '\u767b\u5f55\u8d26\u53f7'  # 登录账号
PWD = '\u5bc6\u7801\uff08BCrypt\u52a0\u5bc6\uff09'  # 密码（BCrypt加密）
ROLE_ADMIN = 'ADMIN-\u7ba1\u7406\u5458/STUDENT-\u5b66\u751f'  # ADMIN-管理员/STUDENT-学生
GL_STUDENT_ID = '\u5173\u8054\u5b66\u751fID\uff08\u5b66\u751f\u89d2\u8272\u65f6\uff09'
ZT_1 = '\u72b6\u6001\uff1a1-\u542f\u7528/0-\u7981\u7528'  # 状态：1-启用/0-禁用
CJSJ = '\u521b\u5efa\u65f6\u95f4'  # 创建时间
GXSJ = '\u66f4\u65b0\u65f6\u95f4'  # 更新时间
XH = '\u5b66\u53f7'  # 学号
XM = '\u59d3\u540d'  # 姓名
XB = '\u6027\u522b\uff1a\u7537/\u5973'  # 性别：男/女
XY = '\u5b66\u9662'  # 学院
ZY = '\u4e13\u4e1a'  # 专业
BJ = '\u73ed\u7ea7'  # 班级
LXFS = '\u8054\u7cfb\u65b9\u5f0f'  # 联系方式
JJLXR = '\u7d27\u6025\u8054\u7cfb\u4eba'  # 紧急联系人
JJLXDH = '\u7d27\u6025\u8054\u7cfb\u7535\u8bdd'  # 紧急联系电话
ZT_ZX = '\u5728\u6821/\u6bd5\u4e1a/\u9000\u5b66/\u4f11\u5b66'  # 在校/毕业/退学/休学
LD_BH = '\u697c\u680b\u7f16\u53f7'  # 楼栋编号
LD_MC = '\u697c\u680b\u540d\u79f0'  # 楼栋名称
LC_S = '\u697c\u5c42\u6570'  # 楼层数
LD_GLY = '\u697c\u680b\u7ba1\u7406\u5458'  # 楼栋管理员
SS_LD = '\u6240\u5c5e\u697c\u680bID'  # 所属楼栋ID
FJ_H = '\u623f\u95f4\u53f7'  # 房间号
LC = '\u697c\u5c42'  # 楼层
FJ_LX = '\u623f\u95f4\u7c7b\u578b\uff1a\u56db\u4eba\u95f4/\u516d\u4eba\u95f4/\u516b\u4eba\u95f4'
CW_RL = '\u5e8a\u4f4d\u5bb9\u91cf'  # 床位容量
FJ_ZT = '\u623f\u95f4\u72b6\u6001\uff1a\u7a7a\u95f2/\u90e8\u5206\u5165\u4f4f/\u5df2\u6ee1/\u7ef4\u4fee\u4e2d\uff08\u524d\u4e09\u79cd\u81ea\u52a8\u8ba1\u7b97\uff0c\u7ef4\u4fee\u4e2d\u624b\u52a8\u6807\u8bb0\uff09'
CW_H = '\u5e8a\u4f4d\u53f7\uff08\u5982\uff1a1\u53f7\u5e8a\uff09'  # 床位号（如：1号床）
RZ_RQ = '\u5165\u4f4f\u65e5\u671f'  # 入住日期
RZ_BZ = '\u5165\u4f4f\u5907\u6ce8'  # 入住备注
ZT_ZZ = '\u5728\u4f4f/\u5df2\u9000\u5bbf'  # 在住/已退宿
TS_RQ = '\u9000\u5bbf\u65e5\u671f'  # 退宿日期
TS_YY = '\u9000\u5bbf\u539f\u56e0\uff1a\u6bd5\u4e1a\u79bb\u6821/\u4f11\u5b66/\u9000\u5b66/\u8c03\u5bbf/\u5176\u4ed6'
TS_BZ = '\u9000\u5bbf\u5907\u6ce8'  # 退宿备注
JC_RQ = '\u68c0\u67e5\u65e5\u671f'  # 检查日期
PF = '\u8bc4\u5206\uff080-100\uff09'  # 评分（0-100）
JG = '\u7ed3\u679c\uff1a\u4f18\u79c0/\u5408\u683c/\u4e0d\u5408\u683c\uff08\u7531\u8bc4\u5206\u81ea\u52a8\u5224\u65ad\uff0c\u53ef\u624b\u52a8\u8c03\u6574\uff09'
JCR = '\u68c0\u67e5\u4eba'  # 检查人
KFX = '\u6263\u5206\u9879\uff08\u9017\u53f7\u5206\u9694\uff09'  # 扣分项（逗号分隔）
PY = '\u8bc4\u8bed'  # 评语
ZT_CG = '\u8349\u7a3f/\u5df2\u53d1\u5e03'  # 草稿/已发布
BXDH = '\u62a5\u4fee\u5355\u53f7\uff08\u5982\uff1aBX20260901001\uff09'
BXW = '\u62a5\u4fee\u7269\u54c1\uff1a\u706f\u7ba1/\u6c34\u9f99\u5934/\u7a7a\u8c03/\u95e8\u9501/\u5e8a\u94fa/\u684c\u6905/\u5176\u4ed6'
WTMS = '\u95ee\u9898\u63cf\u8ff0'  # 问题描述
TP = '\u56fe\u7247\u8def\u5f84\uff08JSON\u6570\u7ec4\uff09'  # 图片路径（JSON数组）
ZT_DCL = '\u5f85\u5904\u7406/\u5904\u7406\u4e2d/\u5df2\u5b8c\u6210'  # 待处理/处理中/已完成
CLR = '\u5904\u7406\u4eba'  # 处理人
CLSM = '\u5904\u7406\u8bf4\u660e'  # 处理说明
CLSJ = '\u5904\u7406\u65f6\u95f4'  # 处理时间
BT = '\u6807\u9898'  # 标题
NR = '\u5185\u5bb9'  # 内容
FBR = '\u53d1\u5e03\u4eba'  # 发布人
FBSJ = '\u53d1\u5e03\u65f6\u95f4'  # 发布时间

BUILDINGS = [
    ('1', '1' + '\u53f7\u697c', 6, '\u5f20\u5bbf\u7ba1'),
    ('2', '2' + '\u53f7\u697c', 6, '\u674e\u5bbf\u7ba1'),
    ('3', '3' + '\u53f7\u697c', 5, '\u738b\u5bbf\u7ba1'),
    ('4', '4' + '\u53f7\u697c', 5, '\u8d75\u5bbf\u7ba1'),
]

L = []
a = L.append

a('-- =============================================')
a('-- ' + DORM + ' \u6570\u636e\u5e93\u5efa\u8868\u811a\u672c')
a('-- MySQL 8.x / utf8mb4')
a('-- =============================================')
a('')
a("CREATE DATABASE IF NOT EXISTS `dormitory` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;")
a('USE `dormitory`;')
a('')

def col(name, typ, null, extra, comment):
    n = null if null else 'NOT NULL'
    return '    `' + name + '` ' + typ + (' ' + extra if extra else '') + ' ' + n + ' COMMENT \'' + comment + '\''

def table(title, name, cols, keys, fks):
    a('-- =============================================')
    a('-- ' + title)
    a('-- =============================================')
    a('DROP TABLE IF EXISTS `' + name + '`;')
    a('CREATE TABLE `' + name + '` (')
    body = []
    for c in cols:
        body.append(col(*c))
    body.extend(keys)
    if fks:
        body.extend(fks)
    a(',\n'.join(body))
    a(") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='" + title + "';")
    a('')

TS = 'DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT \'' + GXSJ + '\''

table('\u7528\u6237\u8868', 'sys_user', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('username', 'VARCHAR(50)', None, None, DENG_LU),
    ('password', 'VARCHAR(100)', None, None, PWD),
    ('role', 'VARCHAR(20)', None, None, ROLE_ADMIN),
    ('student_id', 'BIGINT', 'DEFAULT NULL', None, GL_STUDENT_ID),
    ('status', 'TINYINT', None, 'NOT NULL DEFAULT 1', ZT_1),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    UNIQUE KEY `uk_username` (`username`)',
    '    KEY `idx_student_id` (`student_id`)',
], [])

table('\u5b66\u751f\u4fe1\u606f\u8868', 'student', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('student_no', 'VARCHAR(20)', None, None, XH),
    ('name', 'VARCHAR(50)', None, None, XM),
    ('gender', 'VARCHAR(10)', None, None, XB),
    ('college', 'VARCHAR(50)', None, None, XY),
    ('major', 'VARCHAR(50)', None, None, ZY),
    ('class_name', 'VARCHAR(50)', None, None, BJ),
    ('phone', 'VARCHAR(20)', 'DEFAULT NULL', None, LXFS),
    ('emergency_contact', 'VARCHAR(50)', 'DEFAULT NULL', None, JJLXR),
    ('emergency_phone', 'VARCHAR(20)', 'DEFAULT NULL', None, JJLXDH),
    ('status', 'VARCHAR(20)', None, "NOT NULL DEFAULT '\u5728\u6821'", ZT_ZX),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    UNIQUE KEY `uk_student_no` (`student_no`)',
    '    KEY `idx_name` (`name`)',
    '    KEY `idx_college` (`college`)',
    '    KEY `idx_class_name` (`class_name`)',
], [])

table('\u697c\u680b\u8868', 'dorm_building', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('building_no', 'VARCHAR(20)', None, None, LD_BH),
    ('building_name', 'VARCHAR(50)', None, None, LD_MC),
    ('floor_count', 'INT', None, None, LC_S),
    ('manager', 'VARCHAR(50)', 'DEFAULT NULL', None, LD_GLY),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    UNIQUE KEY `uk_building_no` (`building_no`)',
], [])

table('\u623f\u95f4\u8868', 'dorm_room', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('building_id', 'BIGINT', None, None, SS_LD),
    ('room_no', 'VARCHAR(20)', None, None, FJ_H),
    ('floor', 'INT', None, None, LC),
    ('room_type', 'VARCHAR(20)', None, None, FJ_LX),
    ('bed_count', 'INT', None, None, CW_RL),
    ('status', 'VARCHAR(20)', None, "NOT NULL DEFAULT '\u7a7a\u95f2'", FJ_ZT),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    UNIQUE KEY `uk_building_room` (`building_id`, `room_no`)',
    '    KEY `idx_floor` (`floor`)',
    '    KEY `idx_status` (`status`)',
], [
    "    CONSTRAINT `fk_room_building` FOREIGN KEY (`building_id`) REFERENCES `dorm_building` (`id`)",
])

table('\u5165\u4f4f\u8bb0\u5f55\u8868\uff08\u542b\u9000\u5bbf\u4fe1\u606f\uff09', 'check_in', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('student_id', 'BIGINT', None, None, '\u5b66\u751fID'),
    ('room_id', 'BIGINT', None, None, '\u623f\u95f4ID'),
    ('bed_no', 'VARCHAR(20)', None, None, CW_H),
    ('check_in_date', 'DATE', None, None, RZ_RQ),
    ('remark', 'VARCHAR(255)', 'DEFAULT NULL', None, RZ_BZ),
    ('status', 'VARCHAR(20)', None, "NOT NULL DEFAULT '\u5728\u4f4f'", ZT_ZZ),
    ('check_out_date', 'DATE', 'DEFAULT NULL', None, TS_RQ),
    ('check_out_reason', 'VARCHAR(50)', 'DEFAULT NULL', None, TS_YY),
    ('check_out_remark', 'VARCHAR(255)', 'DEFAULT NULL', None, TS_BZ),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    KEY `idx_student_id` (`student_id`)',
    '    KEY `idx_room_id` (`room_id`)',
    '    KEY `idx_status` (`status`)',
    '    KEY `idx_check_in_date` (`check_in_date`)',
], [
    "    CONSTRAINT `fk_checkin_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)",
    "    CONSTRAINT `fk_checkin_room` FOREIGN KEY (`room_id`) REFERENCES `dorm_room` (`id`)",
])

table('\u536b\u751f\u68c0\u67e5\u8bb0\u5f55\u8868', 'hygiene_check', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('check_date', 'DATE', None, None, JC_RQ),
    ('building_id', 'BIGINT', None, None, '\u697c\u680bID'),
    ('room_id', 'BIGINT', None, None, '\u623f\u95f4ID'),
    ('score', 'INT', None, None, PF),
    ('result', 'VARCHAR(20)', None, None, JG),
    ('inspector', 'VARCHAR(50)', None, None, JCR),
    ('deduction_items', 'VARCHAR(255)', 'DEFAULT NULL', None, KFX),
    ('comment', 'VARCHAR(500)', 'DEFAULT NULL', None, PY),
    ('status', 'VARCHAR(20)', None, "NOT NULL DEFAULT '\u8349\u7a3f'", ZT_CG),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    KEY `idx_room_id` (`room_id`)',
    '    KEY `idx_building_id` (`building_id`)',
    '    KEY `idx_check_date` (`check_date`)',
    '    KEY `idx_result` (`result`)',
], [
    "    CONSTRAINT `fk_hygiene_room` FOREIGN KEY (`room_id`) REFERENCES `dorm_room` (`id`)",
    "    CONSTRAINT `fk_hygiene_building` FOREIGN KEY (`building_id`) REFERENCES `dorm_building` (`id`)",
])

table('\u62a5\u4fee\u5355\u8868', 'repair_order', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('repair_no', 'VARCHAR(30)', None, None, BXDH),
    ('student_id', 'BIGINT', None, None, '\u62a5\u4fee\u5b66\u751fID'),
    ('room_id', 'BIGINT', None, None, '\u623f\u95f4ID'),
    ('item', 'VARCHAR(50)', None, None, BXW),
    ('description', 'VARCHAR(500)', None, None, WTMS),
    ('images', 'VARCHAR(1000)', 'DEFAULT NULL', None, TP),
    ('status', 'VARCHAR(20)', None, "NOT NULL DEFAULT '\u5f85\u5904\u7406'", ZT_DCL),
    ('handler', 'VARCHAR(50)', 'DEFAULT NULL', None, CLR),
    ('handle_remark', 'VARCHAR(500)', 'DEFAULT NULL', None, CLSM),
    ('handle_time', 'DATETIME', 'DEFAULT NULL', None, CLSJ),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
    '    UNIQUE KEY `uk_repair_no` (`repair_no`)',
    '    KEY `idx_student_id` (`student_id`)',
    '    KEY `idx_room_id` (`room_id`)',
    '    KEY `idx_status` (`status`)',
], [
    "    CONSTRAINT `fk_repair_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`)",
    "    CONSTRAINT `fk_repair_room` FOREIGN KEY (`room_id`) REFERENCES `dorm_room` (`id`)",
])

table('\u901a\u77e5\u516c\u544a\u8868\uff08\u9884\u7559\uff09', 'notice', [
    ('id', 'BIGINT', None, 'AUTO_INCREMENT', ZHUJIANID),
    ('title', 'VARCHAR(100)', None, None, BT),
    ('content', 'TEXT', 'NULL', None, NR),
    ('publisher', 'VARCHAR(50)', 'DEFAULT NULL', None, FBR),
    ('publish_time', 'DATETIME', 'DEFAULT NULL', None, FBSJ),
    ('create_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP', CJSJ),
    ('update_time', 'DATETIME', None, 'NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', GXSJ),
], [
    '    PRIMARY KEY (`id`)',
], [])

# Init data
a('-- =============================================')
a('-- \u521d\u59cb\u5316\u6570\u636e')
a('-- =============================================')
a('')
a('-- \u9ed8\u8ba4\u7ba1\u7406\u5458\u8d26\u53f7\uff08\u5bc6\u7801\u5360\u4f4d\uff0c\u5b9e\u9645\u90e8\u7f72\u65f6\u7528\u540e\u7aef\u751f\u6210 BCrypt \u54c8\u5e0c\uff09')
a("INSERT INTO `sys_user` (`username`, `password`, `role`, `status`) VALUES")
a("('admin', 'PLACEHOLDER_BCRYPT_HASH', 'ADMIN', 1);")
a('')
a('-- \u793a\u4f8b\u697c\u680b\u6570\u636e')
a("INSERT INTO `dorm_building` (`building_no`, `building_name`, `floor_count`, `manager`) VALUES")
for i, (no, nm, fl, mg) in enumerate(BUILDINGS):
    sep = ';' if i == len(BUILDINGS) - 1 else ','
    a("('" + no + "', '" + nm + "', " + str(fl) + ", '" + mg + "')" + sep)

out = '\n'.join(L)
# final safety: strip any stray invisible chars
for ch in ['\u200b', '\u200c', '\u200d', '\ufeff']:
    out = out.replace(ch, '')

# Write to a NEW filename to bypass any stale file state
p = pathlib.Path('docs/sql/init_v2.sql')
p.write_text(out, encoding='utf-8', newline='\n')

# Built-in verification on the bytes we just wrote
b = p.read_bytes()
checks = {
    'zero_width_u200b': b'\xe2\x80\x8b' in b,
    'zero_width_u200c': b'\xe2\x80\x8c' in b,
    'zero_width_u200d': b'\xe2\x80\x8d' in b,
    'bom_feff': b'\xef\xbb\xbf' in b,
    'double_comma': b',,' in b,
    'typo_500e': b'VARCHAR(500)e' in b,
}
print('init_v2.sql written, bytes:', len(b))
print('checks:', checks)

# Print the building insert lines (last 6 lines) for visual check
lines_v = p.read_text(encoding='utf-8').splitlines()
for ln in lines_v[-6:]:
    print('LINE:', repr(ln))
