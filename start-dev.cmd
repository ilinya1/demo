@echo off
setlocal
chcp 65001 >nul
title 宿舍管理系统 - 一键启动
cd /d "%~dp0"

REM 本机若存在 JDK21 优先使用（避免系统 JAVA_HOME=1.8 导致后端无法启动）
if exist "D:\JAVA\jdk-21.0.7\bin\java.exe" (
    set "JAVA_HOME=D:\JAVA\jdk-21.0.7"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

REM 确保 MySQL80 服务运行
sc query MySQL80 2>nul | findstr /I "RUNNING" >nul
if errorlevel 1 net start MySQL80 >nul 2>nul

echo [1/3] DB ready: dorm_manager / dorm_manager_test
echo [2/3] start backend  Spring Boot :8080/api ... (new window)
start "dorm-backend-8080" cmd /k "set JAVA_HOME=%JAVA_HOME%&& cd /d %~dp0&& mvn -q spring-boot:run"
echo [3/3] start frontend Vite :3001 ... (new window)
start "dorm-frontend-3001" cmd /k "cd /d %~dp0frontend&& npm.cmd run dev"
echo.
echo Done:
echo   backend  API   : http://localhost:8080/api
echo   frontend pages : http://localhost:3001
echo   demo login     : admin / 123456   student 2023010101 / 123456
echo Close the two "dorm-*" windows to stop.
pause