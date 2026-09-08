# 学生宿舍管理系统

一个前后端分离的宿舍管理演示系统：学生入住/退宿、卫生检查、报修、统计报表、个人中心与系统设置。

## 技术栈
- 后端：Spring Boot 4.1.1、JDK 17+（本机实测 21.0.7）、MyBatis-Plus 3.5.17（`mybatis-plus-spring-boot4-starter` + `mybatis-plus-jsqlparser`）、MySQL 8.0、JWT 鉴权
- 前端：Vue ^3.5、Vite ^8、Element Plus ^2.14、Pinia ^3、Vue Router ^4、ECharts
- 数据库：`dorm_manager`，共 13 张表（`docs/sql/init.sql` 为唯一建库脚本）

## 环境要求
- JDK 21（本机 `D:\JAVA\jdk-21.0.7`，若系统 JAVA_HOME 为 1.8 请在 IDE/命令指定 SDK）
- Maven（后端依赖）
- Node.js（前端，PowerShell 用 `npm.cmd`）
- MySQL 8.0 服务运行中，root 密码 `123456`

## 数据库初始化
重建演示库（含建表 + 演示数据，幂等可重复执行）：

```
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot -p123456 -e "source D:/IDEA/project/demo/docs/sql/init.sql"
```

## 启动
> 一键启动：直接**双击**项目根目录 `start-dev.cmd`（自动检查 MySQL80 → 启动后端 :8080/api → 启动前端 :3001，各开一个工作窗口，关闭窗口即停止）。

分别开两个终端（手动方式）：

后端（端口 8080，context-path `/api`）：
```
mvn spring-boot:run
```

前端（端口 3001，/api 与 /uploads 均代理到 8080）：
```
cd frontend
npm.cmd install     # 首次
npm.cmd run dev
```

访问 http://localhost:3001

## 演示账号
- 管理员：`admin / 123456`
- 学生：`2023010101 / 123456`（王小明）、`2023010102`、`2023010103`
- 其余学生无账号，可由管理员在「学生管理 → 重置密码」自动创建默认账号（123456）。

## 前端 mock ↔ 真实后端切换
- `frontend/src/api/request.js` 依据 `VITE_USE_MOCK` 决定走本地 mock 还是后端。
- 切真实后端：`frontend/.env.development` 写 `VITE_USE_MOCK=false`（已配置）。
- 走本地 mock：改为 `VITE_USE_MOCK=true`（或删除该行）。mock 数据源在 `frontend/src/mock/`。
- 学院管理已接真实后端；本地 mock 均保留可回退。

## 运行测试
```
mvn test
```
- 测试走**独立测试库** `dorm_manager_test`，不会污染演示库 `dorm_manager`（测试类均带 `@ActiveProfiles("test")`，连接 `src/test/resources/application-test.yaml` 的 URL）。
- 当 `init.sql` 或演示库结构变化后，运行 `docs/sql/refresh-test-db.cmd` 重建测试库基线。

## 目录结构（后端关键包）
```
src/main/java/com/gzlg/dorm
  └─ controller/      REST 接口
     service/        业务（实现在 service/impl）
     mapper/         MyBatis-Plus Mapper
     entity/         实体
     dto/ vo/        入参 / 视图对象
     common/
       ├─ result/    统一 Result / PageResult
       ├─ exception/ BizException + 全局异常处理
       ├─ jwt/       JWT 拦截器 + WebMvcConfig（鉴权与静态资源 /uploads）
       ├─ util/      工具（如 BedNoUtil）
       └─ config/    MyBatis-Plus 分页、密码编码
```
数据库设计见 `docs/数据库设计说明.md`；变更记录见 `docs/HTML原型修改记录.md`。

## 说明
- 静态图片演示：种子数据中的卫生照片指向 `/uploads/hygiene/*.jpg`，后端已从工作区 `uploads/` 目录托管（免鉴权）；开发期用本地占位图，真实上传可替换为 OSS 等。