@echo off
REM =====================================================================
REM 刷新测试数据库 dorm_manager_test（从演示库 dorm_manager 克隆结构与数据）
REM 用途：测试使用独立库，避免 mvn test 污染演示数据。
REM 当 init.sql / 演示库 schema 变更后，运行本脚本重建测试库即可与演示库同步。
REM 用法：双击 或 在项目根目录执行  docs\sql\refresh-test-db.cmd
REM =====================================================================
set MYSQL_DIR="C:\Program Files\MySQL\MySQL Server 8.0\bin"

"%MYSQL_DIR%\mysql.exe" -uroot -p123456 -e "DROP DATABASE IF EXISTS dorm_manager_test; CREATE DATABASE dorm_manager_test DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" 
"%MYSQL_DIR%\mysqldump.exe" -uroot -p123456 --default-character-set=utf8mb4 dorm_manager | "%MYSQL_DIR%\mysql.exe" -uroot -p123456 dorm_manager_test

echo 测试库 dorm_manager_test 已刷新完毕。