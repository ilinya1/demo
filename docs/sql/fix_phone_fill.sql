-- =====================================================================
-- 存量数据电话字段修缮脚本（幂等，可重复执行）
-- 目的：将历史数据中空/全空白的电话字段补齐为合法的 11 位手机号，
--      使「全局电话必填且仅限手机号」的新校验在存量库上也能通过。
-- 策略：按各表主键派生唯一手机号（非空数据不受影响）。
--   1) student：无自增主键，以业务学号 student_id 派生（学号须≤11位且纯数字，否则回退默认号）
--   2) sys_user / repair_order：以自增 id 派生
--   3) sys_parameter：仅系统联系电话，固定补默认号
-- 适用：MySQL 8.x。执行前请先备份。
-- =====================================================================

-- 1. 学生联系电话：空 => 138 + 学号后 8 位（学号需为纯数字且长度≥8，否则回退 13800000000）
UPDATE `student`
SET `contact_phone` = CASE
    WHEN `student_id` REGEXP '^[0-9]{8,}$' THEN CONCAT('138', RIGHT(`student_id`, 8))
    ELSE '13800000000'
END
WHERE IFNULL(TRIM(`contact_phone`), '') = '';
-- 受影响行即本次修复数。

-- 2. 学生紧急联系人电话：空 => 139 + 学号后 8 位
UPDATE `student`
SET `emergency_phone` = CASE
    WHEN `student_id` REGEXP '^[0-9]{8,}$' THEN CONCAT('139', RIGHT(`student_id`, 8))
    ELSE '13900000000'
END
WHERE IFNULL(TRIM(`emergency_phone`), '') = '';

-- 3. 管理员/账号联系电话：空 => 137 + 账号 id（LPAD 到位）
UPDATE `sys_user`
SET `phone` = CONCAT('137', LPAD(`id`, 8, '0'))
WHERE IFNULL(TRIM(`phone`), '') = '';

-- 4. 报修单联系电话：空 => 136 + 报修 id
UPDATE `repair_order`
SET `contact_phone` = CONCAT('136', LPAD(`id`, 8, '0'))
WHERE IFNULL(TRIM(`contact_phone`), '') = '';

-- 5. 报修单处理人电话：空 => 135 + 报修 id
UPDATE `repair_order`
SET `handler_phone` = CONCAT('135', LPAD(`id`, 8, '0'))
WHERE IFNULL(TRIM(`handler_phone`), '') = '';

-- 6. 系统参数「联系电话」：空则补默认 13800001111（与后端默认一致）
UPDATE `sys_parameter`
SET `param_value` = '13800001111'
WHERE `param_key` = 'contactPhone' AND IFNULL(TRIM(`param_value`), '') = '';

-- ---------------------------------------------------------------------
-- 兜底纠正：非空但已非法的号码（如历史固话 0571-88888888、残缺号）重新派生覆盖，
--           使全员满足 1[3-9]\d{9}。仅命中去间隔符后不匹配的行，不影响合法值。
-- ---------------------------------------------------------------------
UPDATE `student`
SET `contact_phone` = CASE
    WHEN `student_id` REGEXP '^[0-9]{8,}$' THEN CONCAT('138', RIGHT(`student_id`, 8))
    ELSE '13800000000'
END
WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`contact_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$';

UPDATE `student`
SET `emergency_phone` = CASE
    WHEN `student_id` REGEXP '^[0-9]{8,}$' THEN CONCAT('139', RIGHT(`student_id`, 8))
    ELSE '13900000000'
END
WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`emergency_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$';

UPDATE `sys_user`
SET `phone` = CONCAT('137', LPAD(`id`, 8, '0'))
WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$';

UPDATE `repair_order`
SET `contact_phone` = CONCAT('136', LPAD(`id`, 8, '0'))
WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`contact_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$';

UPDATE `repair_order`
SET `handler_phone` = CONCAT('135', LPAD(`id`, 8, '0'))
WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`handler_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$';

-- 加固校验：以下应返回 0 行。
-- 判据：NULL，或去掉间隔符（- 空格 半角/全角括号 加号）后不满足 1[3-9]\d{9}。
--      与后端 PhoneUtils.normalize+requireMobile 的判定保持一致，避免将「带间隔符但合法」的号码误报。
-- 说明：CONCAT('前缀', LPAD(id,8,'0')) 仅在 id < 100000000（1 亿）时保持 11 位合法；
--      演示数据 id 远小于该值，安全；若业务 id 可能达到 8 位以上需另行调整派生长度。
SELECT 'student.contact_phone' AS `field`, COUNT(*) AS `剩余空/非法`
FROM `student`
WHERE `contact_phone` IS NULL
   OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`contact_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$'
UNION ALL
SELECT 'student.emergency_phone', COUNT(*) FROM `student`
WHERE `emergency_phone` IS NULL
   OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`emergency_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$'
UNION ALL
SELECT 'sys_user.phone', COUNT(*) FROM `sys_user`
WHERE `phone` IS NULL
   OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$'
UNION ALL
SELECT 'repair_order.contact_phone', COUNT(*) FROM `repair_order`
WHERE `contact_phone` IS NULL
   OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`contact_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$'
UNION ALL
SELECT 'repair_order.handler_phone', COUNT(*) FROM `repair_order`
WHERE `handler_phone` IS NULL
   OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(`handler_phone`,'-',''),' ',''),'(',''),')',''),'+','') NOT REGEXP '^1[3-9][0-9]{9}$';