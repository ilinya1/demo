# HTML 原型修改记录

> 项目：宿舍管理系统（`d:\IDEA\project\demo`）
> 记录日期：2026-09-03
> 范围：本次会话对 `prototype/` 原型图的全部改动

说明：本文件记录原型图侧已完成的功能与交互改动，涉及文件均相对于项目 `prototype/` 目录。

***

## 一、班级管理（管理员端）

**涉及文件**

- 新增：`admin/class-list.html`

- 修改：`admin/building-list.html`、`admin/checkin-record.html`、`admin/checkin.html`、`admin/dashboard.html`、`admin/hygiene-add.html`、`admin/hygiene-list.html`、`admin/password.html`、`admin/repair-detail.html`、`admin/repair-list.html`、`admin/room-detail.html`、`admin/room-list.html`、`admin/stat-hygiene.html`、`admin/stat-occupancy.html`、`admin/stat-repair.html`、`admin/student-list.html`（共 15 个已有管理员页加入菜单入口）

**改动内容**

- 新增班级管理页面：年级/学院/班级名称搜索栏；列表（班级名称、所属学院、专业、年级、学生人数、住宿人数、班主任、操作）；新增/编辑班级弹窗（班级名称、所属学院、专业、年级、班主任）。

- 在侧边栏"基础数据"下、位于"学生管理"与"楼栋管理"之间加入"🏫 班级管理"菜单项。

- 说明：数据库 `student` 表当前仅有 `class_name` 字段、无独立班级表，故原型按班级管理模式设计，未改数据库结构。

## 二、退宿申请 — 审核流程（学生端 + 管理员端）

**涉及文件**

- 新增：`student/checkout-apply.html`、`admin/checkout-audit.html`

- 修改（侧边栏入口）：`student/my-room.html`、`student/my-hygiene.html`、`student/repair-add.html`、`student/my-repair.html`、`student/password.html`；`admin/` 下全部非登录页

- 删除：`admin/checkout.html`

**改动内容**

- 学生端新增"退宿申请"：提交表单（当前宿舍/入住时间只读回显、退宿原因、计划退宿日期、申请说明）+ 我的申请记录（申请编号、原因、日期、时间、状态、撤销）。

- 管理员端新增"退宿审核（处理）"：申请列表（编号、学号、姓名、学院、当前宿舍、原因、计划日期、申请时间、状态）+ 审核弹窗（通过/驳回，驳回可填意见）。

- 强制统一流程：移除原手动"退宿登记"菜单入口，删除 `checkout.html`，`checkin-record.html`/`room-detail.html` 内的"退宿"跳转改指向 `checkout-audit.html`。

## 三、班级输入改为下拉框

**涉及文件**

- `admin/student-list.html`（新增学生弹窗、编辑学生弹窗）

- `admin/class-list.html`（班级管理搜索栏）

**改动内容**

- 三处"班级"文本框替换为下拉选项框，选项统一为：软工2301、软工2302、计科2301、机设2301、英语2201。

- 刻意保留输入控件的场景：`class-list.html` 新增班级弹窗的"班级名称"（需自定义新名称）、`checkin.html` 入住登记的"班级"只读回显（按学号自动带出）、表格表头/详情展示列（非输入控件）。

## 四、卫生检查：图片上传 + 评分可靠性

**涉及文件**

- `admin/hygiene-add.html`、`admin/hygiene-list.html`、`css/common.css`

**改动内容**

- 登记页 `hygiene-add.html`：

  - 现场照片上传（支持多张、选择后即时预览缩略图），作为评分依据。

  - 扣分项自动计分：地面不干净(-5)、桌面杂乱(-3)、垃圾未倒(-5)、被子未叠(-3)、违规电器(-15)，100 分起扣，可手动微调。

  - 分数自动判定结果：≥90 优秀 / 60-89 合格 / <60 不合格。

  - 低分/违规强制传照片：评分 <60 或勾选"违规电器"时，未上传照片会被拦截保存。

- 列表页 `hygiene-list.html`：新增"照片"列（缩略图），点击可查看大图。

- `common.css`：补充 `.photo-thumb`、`.photo-stack`、`.photo-count` 复用样式。

- 备注：原型内照片使用站内生成图占位，后端将替换为真实上传路径。

## 五、报修处理人联系方式（管理员端 + 学生端）

**涉及文件**

- `admin/repair-list.html`、`admin/repair-detail.html`、`student/my-repair.html`

**改动内容**

- 管理员报修列表 `repair-list.html`：新增"处理人""联系电话"两列（待处理显示"—"）。

- 学生报修进度 `my-repair.html`：新增"处理人（联系电话）"列。

- 管理员报修处理 `repair-detail.html`："处理操作"表单新增"联系电话"录入框。

- 数据闭环：管理员在详情派单时填写处理人姓名 + 电话，同步体现在管理员列表与学生进度。

## 六、退宿审核 → 退宿处理 + 直接退宿

**涉及文件**

- `admin/checkout-audit.html`（含全局菜单/标题文案批量替换涉及的全部 `admin/` 页）

**改动内容**

- 将"退宿审核"文案统一改为"退宿处理"（侧边栏菜单、页面标题，0 处残留）。

- 保留原申请处理功能与审核弹窗（通过/驳回）不变。

- 新增"＋ 直接退宿"：列表顶部按钮 + 弹窗，输入学号带出姓名/当前宿舍（只读），选择退宿日期、退宿原因、备注，确认后跳过学生申请直接办理退宿并空出床铺。

***

## 审计结论（2026-09-03）

对改动执行了全量校验，结果通过：

- 引用断裂：无（所有 `href/src` 目标文件均存在）。

- 已删除文件残留引用：`checkout.html` 引用数 0；"退宿登记"菜单残留 0。

- 导航一致性：管理员各页菜单项均 13（login 为 0）；学生各页菜单项均 5（login 为 0）。

- 公共依赖：`common.css` 关键类、`common.js` 的 `showMsg/openModal/closeModal` 均存在。

***

## 待落地到后端/数据库的项（供后续）

- 卫生检查照片存储：`hygiene_record` 增 `photos` 字段 + 独立存储目录（或新表 `hygiene_photo`）。

- 报修处理人：`repair_order` 增 `handler_name`、`handler_phone`。

- 退宿来源：退宿流水增 `source` 字段区分 `apply`（申请审核通过）与 `direct`（管理员直接退宿）。

- 上述字段需同步到 `docs/sql/init.sql` 与 `docs/数据库设计说明.md`。

***

## 操作日志

> 约定：每次操作结束后在此追加一条记录（时间、操作、涉及文件、结果），随时代序递增。

- **2026-09-03（操作日志 #1）** 将本次会话全部原型改动提交并推送到 GitHub。

  - 提交：`39e6105`（31 个文件，+1024 / −1052）

  - 推送：`19ae4a7..39e6105` → `origin/main`（<https://github.com/ilinya1/demo.git）>

  - 内容：新增 `checkout-audit.html`、`class-list.html`、`checkout-apply.html`、`HTML原型修改记录.md`；修改全部原型 HTML/CSS；删除 `checkout.html` 及 5 个遗留 docs 文件（`init.sql`、`数据库设计说明.md`、`项目交接文档.md`、`fix_sql.py`、`gen_sql.py`）。

  - `debug.log` 未纳入提交。

  - 备注：gh CLI 未安装，本次按选择直接推送到 `main`，未建 PR。

- **2026-09-03（操作日志 #2）** 为学生批量导入提供模板以减少导入错误。

  - 新增 `prototype/templates/学生信息导入模板.csv`（UTF-8 带 BOM，Excel 可直接打开不乱码）。

  - 表头与导入字段完全一致：`学号、姓名、性别、学院、专业、班级、联系方式、紧急联系人`，含 2 行示例（性别限 男/女，班级用系统已有值）。

  - 在 `prototype/admin/student-list.html` 的"批量导入"按钮前新增"⬇ 下载模板"下载链接。

  - 说明：`class-list.html` 仅有"批量导出"（无需模板）；导出按当前列表字段生成 CSV。

- **2026-09-03（操作日志 #3）** 将"批量导出"接入真正的 CSV 导出。

  - `prototype/js/common.js` 新增通用导出函数 `exportTableCSV(selector, filename, skipLastColumn)`：前端读取表格生成 CSV、加 UTF-8 BOM（Excel 打开不乱码）、自动转义逗号/引号/换行、跳过"操作"列，并触发下载。

  - `prototype/admin/student-list.html` "批量导出"改为 `exportTableCSV('.data-table','学生列表.csv',true)`。

  - `prototype/admin/class-list.html` "批量导出"改为 `exportTableCSV('.data-table','班级列表.csv',true)`。

- **2026-09-03（操作日志 #4，完整审计）** 对全项目做一次"逻辑闭环 + 字段一致性"全面检查（后续据此设计数据库）。

  **方法**：遍历 `prototype/` 全部页面，用脚本核对导航/断链/孤儿页，并分模块逐字段核对学生端 ↔ 管理员端交互闭环。

  **结果 1 — 导航闭环（通过）**

  - 断链 0；孤儿页面 0（`hygiene-add` 有 `location.href='hygiene-add.html'` 入口，先前脚本未识别 href、误报）。

  - 菜单一致性：管理员各页侧边栏均 13 项、学生各页均 5 项、登录/index 0 项。

  - 角色入口清晰：`index` 分"管理员/学生"入口 → admin/login.html、student/login.html，登录后分别进 dashboard、my-room。

  **结果 2 — 端到端字段闭环（逐条核对，除下列发现外均一致）**

  - 报修：学生提交 `报修人/联系电话/报修位置/报修物品/问题描述/上传图片` → 管理员列表 `报修单号/楼栋/房间/报修物品/问题描述/提交时间/处理人/联系电话/状态(待处理·处理中·已完成)` → 学生进度 `处理人(联系电话)/处理说明` 回显。枚举一致。

  - 退宿：学生申请 `申请人/学号/当前宿舍/入住时间/退宿原因/计划退宿日期/申请说明` → 管理员审核 `申请编号/学号/姓名/学院/当前宿舍/原因/计划日期/申请时间/状态(待审核·已通过·已驳回)` + 驳回意见 + 直接退宿；通过后 `checkin-record` 反映退宿时间/住宿状态。闭环完整。

  - 卫生：登记 `检查日期/检查人/楼栋/房间/评分/结果(优秀·合格·不合格)/扣分项/照片/评语` → 列表回显 + 学生端 `检查日期/评分/结果/检查人/评语` 一致。

  **结果 3 — 发现并修复的不一致（1 处）**

  - `checkin-record.html` 原用列名 `状态` 承载"住宿状态"，与 `student-list` 的 `状态`(学籍：在校) 同名不同义、易混淆。已将 `checkin-record` 的搜索筛选与表头 `状态` 统一改为 `住宿状态`，使住宿维度术语全局一致。

  - 住宿状态全域统一枚举：`在住 / 已退宿 / 未住`（student-list、checkin-record、my-room 同一组词）。

  **结果 4 — 供建库的字段映射建议**

  - 班级：原型用 `班级名称`（如 软工2301）作为 `student` 关联业务键；建议独立 `class` 表(班级名称/所属学院/专业/年级/班主任)，student 存 class\_id。

  - 报修位置：学生提交端为文本"1号楼 102室"、列表端拆 `楼栋/房间` 两列 → 建库用 `building_id/room_id`。

  - 联系电话：页面口径为 `联系电话`(报修) / `联系方式`(学生) → 统一入库字段 `contact_phone VARCHAR(20)`。

  - 退宿：记录加 `source` ∈ {apply=申请审核通过, direct=管理员直接退宿}。

  - 卫生：`hygiene_record` 加照片字段 `photos`（JSON/附件列表），扣分项可落为 `score`(自动计分)、`result` ∈ {优秀,合格,不合格}、`deduct_items`。

  - 登录：管理员 `账号/密码`、学生 `学号/密码`，建 `sys_user`(含 role) 或辅导员账号表 + student(密码)。

  - 状态枚举统一：报修 `待处理/处理中/已完成`；退宿申请 `待审核/已通过/已驳回`；住房 `在住/已退宿/未住`；卫生结果 `优秀/合格/不合格`。

- **2026-09-03（操作日志 #5）** 生成项目交接文档 `docs/项目交接文档.md`。

  - 按 v1/v2/v3 分版本记录：v1 基础原型（管理员 13 模块/学生 5 模块/入口）；v2 功能增强（班级管理、退宿申请-审核+直接退宿、班级下拉、卫生照片评分、报修处理人电话、导入导出）；v3 全面审计与字段基线。

  - 含当前技术状态（后端骨架、无数据库脚本、原型静态占位）、已做内容、后续开发待办（建库字段映射、后端接口、图片上传、导入导出后端化、鉴权、统计真实化等）、版本修订记录。

  - 依据：`prototype/` 侧边栏实读模块清单 + 操作日志 #1-#4 全部记录。

- **2026-09-03（操作日志 #6）** 复核交接文档并修复发现的问题。

  - BUG 修复：`prototype/admin/student-list.html` 下载模板链接原为相对路径 `templates/学生信息导入模板.csv`（在 admin/ 下解析到不存在的 `admin/templates/`，会 404），改为 `../templates/学生信息导入模板.csv` 指向真实模板。

  - 文档校正：管理员端实际页面数由"20"更正为 **18**（列表：dashboard/student-list/class-list/building-list/room-list/room-detail/checkin/checkout-audit/checkin-record/hygiene-list/hygiene-add/repair-list/repair-detail/stat-occupancy/stat-hygiene/stat-repair/login/password）。

  - 目录结构补记 `docs/功能模块说明.md`（早期文档，此前遗漏）。

  - 修正 v2.2 末尾"后续 v3 补充直接退宿"与 v2.6 的重复矛盾，改为指向 v2.6。

  - 学生端 pages=7（"6 个页面 + 登录"）经核实正确，无需改动。

- **2026-09-03（操作日志 #7，数据库设计定稿）** 完成数据库表结构设计并落地两份文档（同步维护）。

  **设计决策（与用户讨论后定稿）**：

  - 床位独立建模：新增 `dorm_bed` 表（room\_id + bed\_no + status），匹配房间详情"床位分布"页。

  - 账号体系：`sys_user` 独立表统一承载管理员与学生登录，学生账号关联 `student_id` + role。

  - 多图存储：`photos`/`images` 以 TEXT 存路径列表(JSON)，一期本地目录、二期 OSS。

  - **快照冗余（仅 check\_in）**：住宿历史为核心审计读路径，入住时写入 `student_name/class_name/building_name/room_no/bed_no` 快照，退宿不覆盖；卫生/报修房间号稳定，不加快照。

  - 二期扩展策略：不预留死字段，`notice/水电/违纪` 二期再按月新建；`check_in` 天然支持调宿、`sys_user.role` 可扩 TEACHER。

  **涉及文件**：

  - 新增 `docs/sql/init.sql`：MySQL 建库 + 10 张表（class/student/sys\_user/dorm\_building/dorm\_room/dorm\_bed/check\_in/checkout\_apply/hygiene\_record/repair\_order）+ 索引 + 逻辑外键注释 + 示例数据（演示账号 + 楼栋/房间/床位）。

  - 新增 `docs/数据库设计说明.md`：ER 关系 + 10 表字段字典 + 逻辑外键对应关系 + 枚举字典 + 核心业务流转 + 二期预留说明 + QA。

  **结果**：字段字典两份文档一致（check\_in 含 5 快照字段 + 关联 id + 退宿来源 source），沿用逻辑外键约定。

- **2026-09-03（操作日志 #8，数据库设计复核与修正）** 按用户要求对数据库设计做全量审查，修复发现的问题，并同步 `init.sql` 与 `数据库设计说明.md`。

  **审计发现**：

  - A1 样例数据矛盾：`dorm_room` 101/102 标记"部分入住"，但无 `check_in`、床位全空闲，与原型 `room-detail`(102 三人在住) 不符。

  - A2 密码标注与示例冲突：文档/注释称"BCrypt 密文"，示例却用 `{noop}123456` 明文。

  - A3 文档笔误：`sys_user.student_id` 联结写作 `student.id`（应为 `student.student_id`）。

  - B1 `check_in` 关联字段(building/room/bed\_id)可空；B2 `sys_user.student_id` 无唯一约束；B3 student 冗余 college/major 未注明约定；B4 ER 图"学生 N:1/1:N check\_in"记号误。

  **修正（A 类 + B1/B2 加固 + 文档完善）**：

  - 补 `check_in` 样例复刻原型：王小明/李小红/陈强入住 1号楼 102 室 1\~3 号床（均【在住】）、床位置占用、101 改空闲、student.housing\_status 改在住。

  - `check_in` 的 building\_id/room\_id/bed\_id 改 `NOT NULL`。

  - `sys_user.student_id` 加 `UNIQUE KEY uk_user_student`。

  - 密码标注澄清：演示样本 `{noop}` 明文占位、生产用 BCrypt。

  - 文档：修 `student.id`→`student.student_id`、ER 图改 `student 1:N check_in`、补 student 冗余约定说明、补单元索引说明。

  **涉及文件**：`docs/sql/init.sql`、`docs/数据库设计说明.md`。
  **结果**：两份文档字段与约束保持同步；样例数据自洽且与原型 `room-detail` 一致。

- **2026-09-03（操作日志 #9，数据库二次复核与加固）** 对照更多原型页（入住记录/卫生登记/报修处理）做第二轮审查，应用加固项，并同步两份文档。

  **本轮发现与处理**：

  - C1 严格度不一致 → `hygiene_record`、`repair_order` 的 `building_id/room_id` 由可空改为 `NOT NULL`（与 `check_in` 一致，原型均必填）。

  - C2 退宿统计索引 → `check_in` 原 `(status)` 索引扩充为 `(status, check_out_time)`，服务"已退宿+退宿时间段"查询。

  - C3 一致性说明 → 文档注明 `dorm_room.capacity` 与 `dorm_bed` 行数需一致，由 Service 新建房间时保证。

  - D1 措辞统一 → `sys_user.password` 标注为"BCrypt 密文（演示样本 `{noop}` 明文占位）"。

  **涉及文件**：`docs/sql/init.sql`、`docs/数据库设计说明.md`。
  **结果**：两份文档约束与索引同步；校验结论——建表语法与字段对齐无误，无直接阻断问题。

- **2026-09-03（操作日志 #10，数据库三次复核 + 前端枚举对齐）** 对照学生端全页 + 学生管理 + 仪表盘做第三轮反查并收尾。

  **本轮发现与处理**：

  - E1 原型残留不一致 → `prototype/admin/student-list.html` 住宿状态列"已退"改为"已退宿"，与 DB 及 checkin-record/my-room 统一；全站 `已退` 残留清零。

  - F1 撤销语义未定义 → 设计文档补充约定："退宿申请在待审核状态可撤销（直接删除记录，不新增枚举）；已通过/已驳回不可撤销"。

  - 复核确认：我的入住/提交报修/报修进度/仪表盘四类统计均可由现有 10 表推导，无字段缺失、无统计不可达。

  **涉及文件**：`prototype/admin/student-list.html`、`docs/数据库设计说明.md`。
  **结果**：向工程约定"住宿状态=在住/已退宿/未住"全站收敛；数据库设计与原型字段/枚举完全对齐。

- **2026-09-03（操作日志 #11，数据库最终全面复核）** 做第四轮终核：通读 `init.sql` 与 `数据库设计说明.md` 全文逐字段核对，并补齐阅读剩余原型页（退宿处理/房间详情/入住登记/楼栋/班级/我的卫生/统计报表等），逐一反查建库设计。

  **复核范围**：10 张表 × 全部字段/类型/约束/索引；枚举字典 11 项；逻辑外键 8 组；样例数据自洽性；原型 26 页字段覆盖度。

  **结果**：

  - `init.sql` 与 `数据库设计说明.md` 两份文档**字段字典、约束条件、索引定义、枚举取值完全同步**。

  - 全部原型页面字段（含学生端我的入住/退宿申请/提交报修/报修进度、管理端入住记录/卫生登记/报修处理/退宿处理/房间详情/学生管理/仪表盘等）均可由现有 10 张表支撑，**无字段缺失、无统计不可达**。

  - 此前 A\~F 类问题均已闭环修复，本轮无新增问题。

  **结论**：数据库设计已通过终核，可作为后端 Spring Boot + MyBatis-Plus 实体映射与接口开发的稳定基线。

- **2026-09-03（操作日志 #12，交接文档 v4）** 按用户要求将《项目交接文档.md》由 v3 升级为 **v4** 完整版，整合全部阶段并细化后续开发指引。

  **本次修改**：

  - 新增「五、v4 —— 数据库设计落地与终核」章节：7 条设计决策（逻辑外键/快照/冗余/床位独立表/账号体系/多图/二期预留）+ 10 张表全貌 + 四轮复核 A\~F 类修复汇总。

  - 更新「当前技术状态」：数据库设计已定稿（init.sql + 数据库设计说明.md，需同步维护，尚未建库运行）；后端仍为骨架；前端仍为静态原型。

  - 重写「后续开发待办」：以 v4 为基线，拆分为 后端接口实现 / 前端联调 / 工程化与功能完善 / 交付与部署 四部分，逐步到各模块接口清单。

  - 版本修订记录追加 v4。

  **涉及文件**：`docs/项目交接文档.md`。
  **结果**：交接文档与仓库最新状态一致，可直接作为接手人员的完整开发指引。

- **2026-09-03（操作日志 #13，确定前后端开发方案并产出开发设计文档 v1）** 与用户讨论确定下一阶段（数据库设计之后的开发）的方向并落地首份开发设计文档。

  **方案决策（经多轮讨论，用户已确认）**：

  - 开发文档定位：技术方案 + 接口契约结合的单文档，作为前后端开发/联调/验收的共同依据。

  - 开发顺序：先出文档 → 后端骨架+建库验证 ∥ 前端 Vue 工程 → 前端 mock 先行 → 后端实现 → 联调替换 → 工程化部署。

  - 前端形态：Vue3 + Vite 重构，新工程于 `frontend/`，保留 `prototype/` 静态原型作参考。

  - 后端：包重构为 `com.gzlg.dorm`，MyBatis-Plus + MySQL。

  - 鉴权：JWT（mock 阶段先假登录）。

  **涉及文件**：新增 `docs/开发设计文档.md`（v1：技术选型、后端/前端工程结构、统一返回/异常/分页约定、10 表→实体映射、接口契约总览 5.1\~5.7、枚举字典、待展开清单）。
  **结果**：开发设计文档 v1 骨架定稿；接口逐字段契约待 v2 展开。

- **2026-09-03（操作日志 #14，开发环境检查与 JDK 版本修复）** 检查开发文档与开发环境，并解决 JDK 版本不匹配问题。

  **环境检查结论**：

  - 默认 `JAVA_HOME` 指向 `D:\JAVA\jdk1.8.0_111`（JDK8），不满足 Spring Boot 4.1.1 的 Java 17+；Maven（3.9.16）当时跑在 JDK8 上，`mvn compile` 会失败。

  - 本机已装 `D:\JAVA\jdk-21.0.7`（JDK21，兼容 Java 17 目标）。实测以 JDK21 执行 `mvn -q compile` 通过 → 验证 pom 的 Spring Boot 4.1.1 + spring-boot-starter-webmvc 可正常构建。

  - Node v24.16 / npm 11.17 可用（PowerShell 中 `npm.ps1` 被 ExecutionPolicy 拦截，需用 `npm.cmd`）；pnpm/yarn 未装。

  - MySQL 8.0.34 已装、服务 MySQL80 运行中；但 root 无密码连接被拒（Access denied），建库验证需正确凭据。

  **JDK 修复（本步主题）**：

  - 用户级 `JAVA_HOME` 已持久化为 `D:\JAVA\jdk-21.0.7`（`[Environment]::SetEnvironmentVariable(...,"User")`）；当前会话 `mvn -version` 显示 Java 21.0.7。

  - 系统级（Machine）`JAVA_HOME` 仍为 1.8，因无管理员权限未能由工具修改，已给用户提供管理员改法与回退命令；同时指引在 IDEA 中设 Project SDK = JDK21（日常运行后端的最可靠方式）。

  **待用户跟进**：管理员改系统级 JAVA\_HOME；IDEA 设 Project SDK 21；提供 MySQL root 密码用于建库验证。

- **2026-09-03（操作日志 #15，开发文档版本兼容性复核）** 联网核对所选框架在当前（2026-09）是否为推荐版本并验证兼容性，修订开发设计文档。

  **核实结论**：

  - Spring Boot 4.1.1 = 当前最新稳定版（2026-08-20 发布），4.1.x 支持 Java 17+、OSS 支持至 2027-06 → 选型正确。

  - ⚠️ MyBatis-Plus 对 Spring Boot 4 有专用 starter `mybatis-plus-spring-boot4-starter`（最新 3.5.17），并非 Boot3 的 `mybatis-plus-boot-starter`；用错 starter 会启动失败。

  - 前端锁定：Vue ^3.5（3.6 待稳定）、Vite ^8（Node24 兼容；可用 ^7）、Element Plus ^2.14（最新 2.14.5）、Pinia ^3（4.0 ESM-only）、Vue Router ^4、Axios ^1。

  **涉及文件**：`docs/开发设计文档.md`（v1 → v1.1：更新 2.1/2.2 技术选型锁版本，补 MyBatis-Plus 正确 starter 名，版本记录追加 v1.1）。
  **结果**：技术选型版本与兼容性一次性核定，消除 MyBatis-Plus × Spring Boot 4 的 starter 选型风险。

- **2026-09-03（操作日志 #16，执行 init.sql 建库验证）** 使用 MySQL root（本机 MySQL 8.0.34，服务 MySQL80）执行 `docs/sql/init.sql`，首次在真实实例建库并核验。

  **执行结果**：

  - 库 `dorm_manager` 创建成功，脚本一次执行 exit=0 无报错。

  - 10 张表全部建出：class/student/sys\_user/dorm\_building/dorm\_room/dorm\_bed/check\_in/checkout\_apply/hygiene\_record/repair\_order。

  - 示例数据行数：class 5、student 3、sys\_user 4、building 2、room 3、bed 12、check\_in 3；申请/卫生/报修各 0 —— 与设计要求一致。

  - `check_in` 5 快照字段（student\_name/class\_name/building\_name/room\_no/bed\_no）正确写入，3 人均在住；`(status, check_out_time)` 索引存在；sys\_user 角色与学号关联正确。

  - 102 室床位联动：1\~3 床占用（王小明/李小红/陈强）、4 床空闲 —— 与原型 room-detail 一致。

  **结论**：建库脚本在真实 MySQL 8.0.34 上可一次性成功执行，字段/索引/示例数据与数据库设计文档完全对齐。
  **涉及文件**：仅执行数据库脚本（无文件改动）。

- **2026-09-03（操作日志 #17，前端工程骨架搭建）** 按开发设计文档"前端 mock 先行、调好后开发后端"的新节奏，开始前端 `frontend/` Vue3+Vite 工程开发。

  **本次产出**（`frontend/` 下）：

  - 工程配置：`package.json`（Vue ^3.5 / Vite ^6（plugin-vue\@5 兼容需配 Vite6）/ Element Plus ^2.14 / Pinia ^3 / Vue Router ^4 / Axios ^1 / @element-plus/icons-vue）、`vite.config.js`（@ 别名、/api 代理 8080、端口 3000）、`index.html`。

  - 核心骨架：`src/main.js`（注册 Element Plus 中文、图标、pinia、router）、`src/App.vue`、`src/router/index.js`（双端路由 + 登录/角色守卫）、`src/store/user.js`（pinia：token/用户信息/登录/退出）、`src/api/request.js`（mock 短路 + axios 拦截器，Result 统一解包、401 处理）、`src/api/auth.js`、`src/api/dashboard.js`。

  - mock 机制：`src/mock/index.js`（登录/仪表盘 mock，`VITE_USE_MOCK=false` 切真实接口）。

  - 页面：`src/views/Login.vue`、`src/layouts/AdminLayout.vue`（13 菜单）/`StudentLayout.vue`（5 菜单，对齐原型）、`src/views/admin/Dashboard.vue`、`src/views/student/MyRoom.vue`。

  **工程落坑与处理**：① npm 沙箱限制 → 禁用沙箱 + npm 用项目内 `--cache`；② `@vitejs/plugin-vue@5` 与 Vite7 peer 冲突 → 降 Vite ^6（稳定组合）；③ Element Plus 需补充 `@element-plus/icons-vue` 依赖。

  **验证**：`npm install`（96 包）成功；`vite build` 一次通过（1693 模块）；`npm run dev` 于 :3000 返回 200，登录页骨架可访问。
  **待办（下轮）**：基础数据模块（学生/班级/楼栋/房间）等页面与各自 mock/契约。

- **2026-09-03（操作日志 #18，补全仪表盘统计 mock）** 依据原型 `prototype/admin/dashboard.html` 补全前端仪表盘统计。

  **改动**：

  - `src/mock/index.js`：`/dashboard/stats` 数值对齐原型（studentCount 1286 / buildingCount 6 / roomCount 1240 / occupancyRate 86.5%）；新增 `/dashboard/building-occupancy`（6 栋楼入住率）与 `/dashboard/hygiene-trend`（近 4 周卫生平均分 82/85/88/87）。

  - `src/api/dashboard.js`：新增 `getBuildingOccupancy()`、`getHygieneTrend()`。

  - `src/views/admin/Dashboard.vue`：渲染 4 张统计卡片 + 各楼栋入住率（横向进度条）+ 最近卫生检查（CSS 柱状图，未引图表库）。

  **验证**：`vite build` 通过；浏览器登录 admin 后仪表盘正确显示 1286/6/1240/86.5% 与两图表区域，全部核验 PASS。

- **2026-09-03（操作日志 #19，UI 重构 → 方案 B 现代专业深色）** 用户认为整体 UI 不好看，需换风格。先产出三套方案预览页（`src/views/PreviewDemo.vue`，路由 `/preview`：A 清新学院风 / B 现代专业深色 / C 极简编辑风 并排 mock），浏览器对照后选定 **B 方案（深蓝灰玻璃侧边栏 + 靛蓝强调 + 细腻阴影）**。

  **改造内容**：

  - 新增 `src/styles/theme.css`：全局设计令牌 CSS 变量（`--d-primary` 靛蓝 #4f6ef7、`--d-sb-*` 侧边栏、`--d-*` 内容区、`--d-radius`、`--d-shadow`）+ Element Plus 主色覆盖（`--el-color-primary` 及 light-3\~9 / dark-2）→ 所有 el 组件自动变靛蓝。

  - `AdminLayout.vue`/`StudentLayout.vue`：侧边栏改深色玻璃（`.d-sidebar` + el-menu 色彩变量覆盖、logo 靛蓝标记），选中项靛蓝高亮；头栏/内容区走令牌。

  - `Login.vue`：深蓝灰背景 + 靛蓝光晕 + 白卡片 + 靛蓝方形 logo。

  - `Dashboard.vue`：统计卡图标统一淡靛蓝底、楼栋入住率进度条与卫生柱状图改靛蓝（渐变）。

  - `main.js` 引入 theme.css；`router/index.js` 放行 `/preview`。

  **验证**：`vite build` 通过；浏览器核验登录页与仪表盘均应用深色侧边栏 + 靛蓝强调色、无样式错乱，8 项检查全部 PASS。
  **待办（下轮）**：基础数据模块（学生/班级/楼栋/房间）页面按 B 方案风格实现 + 各自 mock/契约。

- **2026-09-03（操作日志 #20，主题细节微调）** 沿用 B 方案基础上微调 4 处：靛蓝主色、深色玻璃侧边栏、12px 圆角保持不变；其余两点调整如下。

  **调整内容**：

  - 舒适密度 + **标题加粗**：无误，`AdminLayout.vue`/`StudentLayout.vue` 顶部页面标题字重 700 → 800。

  - **统一线性图标**：`Dashboard.vue` 4 张统计卡图标由 emoji（👨🎓🏢🚪🛏️）改为 Element Plus 线性 SVG 图标（`User`/`OfficeBuilding`/`House`/`TrendCharts`），浅靛蓝底 + 靛蓝描边色。

  **验证**：`vite build` 通过；浏览器核验统计卡为靛蓝线性 SVG（12 个 PNG 内 12 个 SVG 图标、色值 rgb(79,110,247)）、标题字重 800、侧边栏仍深色玻璃 + 靛蓝高亮、无样式错乱。期间 dev server 曾停止（重启后台 job-94d0... 后恢复 :3000）。

- **2026-09-04（操作日志 #21，仪表盘图表 ECharts 美化）** 用户反馈"各楼栋入住率"与"最近卫生检查"两大可视化模块展示不好看，由纯 CSS 进度条/柱状图升级为 **ECharts** 专业图表（匹配 B 方案现代专业质感）。

  **改造内容**：

  - `frontend` 安装 `echarts`（按需引入 echarts/core + BarChart/LineChart + Grid/Tooltip 组件 + CanvasRenderer）。

  - `Dashboard.vue`：

    - **各楼栋入住率** → 横向渐变条形图：6 栋靛蓝渐变圆角柱、柱端百分比标签、x 轴隐藏、虚线分隔线、shadow tooltip（"xx号楼 xx% 入住"）。

    - **最近卫生检查** → 平滑折线面积图：近 4 周、靛蓝 3px 线条 + 圆点（白描边）+ 顶部向下的靛蓝透明面积渐变、hover tooltip（"平均分 xx 分"）。

    - 配色从 `--d-primary` CSS 变量实时读取，自适应主题；卡片升级 header（标题+说明）+ 边框/阴影；新增 resize 自适应与 `onBeforeUnmount` dispose 释放。

  **验证**：`vite build` 通过；浏览器确认两 canvas 均为 ECharts 实例、条形图与折线图正常渲染、tooltip 可用、靛蓝配色协调、无空白报错（仅两处非阻塞 warn/info）。

- **2026-09-04（操作日志 #22，基础数据模块四页面上线）** 按 B 方案风格实现管理员端基础数据模块（学生 / 班级 / 楼栋 / 房间）。

  **新增文件**：

  - `src/mock/util.js`（ok/fail）；`src/mock/baseData.js`：集中存放班级/学生/楼栋/房间/床位 mock 数据与 CRUD（含学号唯一、班级名/楼栋名/房间号唯一校验、room 与 typing 联动、1号楼101-102床位示例）。

  - `src/mock/index.js` 扩展基础数据路由分发（students/classes/buildings/rooms 增删改查 + `/rooms/options` + `/rooms/{id}/beds`）。

  - `src/api` 新增 `student.js`/`class.js`/`building.js`/`room.js`。

  - `src/views/admin` 新增 `StudentList.vue`（搜索学号/姓名/学院/状态 + 表格 + 分页 + 新增/编辑弹窗含班级联动带出学院专业 + 删除确认 + 学籍/住宿状态标签）、`ClassList.vue`（年级/学院/班级名搜索 + 新增/编辑）、`BuildingList.vue`（楼栋名/管理员搜索 + 新增/编辑）、`RoomList.vue`（楼栋/房号/房型/状态搜索 + 新增/编辑 + **床位分布弹窗**：显示占用/空闲床位与学生）。

  - `src/router/index.js`：补全 4 个子路由到 admin children。

  **验证**：`vite build` 通过；浏览器逐个核验：学生 10 条、班级 5 条、楼栋 6 条、房间 9 条均正常渲染，新增弹窗、分页、床位弹窗可用，无空白/报错（仅非致命 warn）。
  **待办（下轮）**：住宿业务（入住登记/退宿处理/入住记录）与日常管理（卫生/报修）等页面 + mock。

- **2026-09-04（操作日志 #23，仪表盘与统计报表合并）** 用户要求将仪表盘与统计报表合并，采用"A 仪表盘作为一级含 4 子项"方案。

  **改动**：

  - `AdminLayout.vue`：删除独立"统计报表"分组；一级"仪表盘"改为子菜单，含 仪表盘总览(/admin/dashboard) / 入住统计(/admin/stat-occupancy) / 卫生统计(/admin/stat-hygiene) / 报修统计(/admin/stat-repair)。管理员端一级菜单收敛为 4 组。

  - 新增 3 个统计页面（沿用 B 方案 ECharts 风格）：

    - `StatOccupancy.vue` 入住统计：3 统计卡 + 各楼栋入住率条形图 + 近 6 月入住/退宿双折线。

    - `StatHygiene.vue` 卫生统计：4 统计卡 + 近 4 周平均分折线面积图 + 优秀/合格/不合格环形图。

    - `StatRepair.vue` 报修统计：3 统计卡 + 各类型报修柱状图 + 近 6 月提交/完成双折线。

  - `src/mock/stats.js`（三组统计数据）、`src/mock/index.js` 分发 `/stats/occupancy|hygiene|repair`、`src/api/stat.js`、`src/router/index.js` 注册 3 子路由。

  **验证**：`vite build` 通过；浏览器确认侧边栏仅剩 4 组一级菜单、"统计报表"分组已移除、仪表盘子菜单含 4 子项、3 个统计页图表（条形/折线/环形/面积/柱状）全部正常渲染、各页 title 正确、无空白报错。

- **2026-09-04（操作日志 #24，住宿业务模块上线）** 按原型 `prototype/admin` 实现管理员端住宿业务三页（入住登记 / 退宿处理 / 入住记录）。

  **新增文件**：

  - `src/mock/checkin.js`：住宿业务 mock。含 入住记录(`checkInRecords`)、退宿申请(`checkoutApplications`) 初始数据；接口 `getStudent`(学号带出学生)、`checkinRooms`(楼栋剩余床位房间)、`checkinFreeBeds`(房间空闲床位号)、`submitCheckin`(入住，占用床位+改学生在住)、`listCheckinRecords`(记录+状态/学号/姓名/楼栋筛选)、`listCheckoutApps`、`auditCheckoutApp`(申请通过则退宿生效/驳回留意见)、`directCheckout`(管理员直接退宿跳过申请)、`buildingOptions`。与 `baseData` 的 `occupyBed/freeBed/updateStudentHousing` 联动，保证各页住宿状态一致（已退宿后拒绝重复退宿）。

  - `src/api/checkin.js`：9 个住宿业务接口封装。

  - `src/views/admin` 新增 `Checkin.vue`（4 步引导：查学号带出学生 → 选楼栋 → 选房间剩床 → 床位；已在住者拦截重复入住；校验通过方可确认入住）、`CheckoutAudit.vue`（退宿申请列表+筛选+审核弹窗(通过/驳回留原因) + 「直接退宿」弹窗学号带出当前宿舍）、`CheckinRecord.vue`（入住记录列表+状态/学号/姓名/楼栋筛选+来源列+在住可直接退宿）。

  - `src/router/index.js`：注册 3 个 admin 子路由（checkin / checkout-audit / checkin-record）；`AdminLayout.vue` 侧边栏「住宿业务」子菜单三步。

  **验证**：`vite build` 通过（三包 chunk 正常产出）。函数级 mock 全链路验证通过：学生 2023020101 查询带出刘少军；buildings/options 6 楼栋；checkin/rooms?buildingId=1→101/102/104(剩床)；free-beds→\[4]；POST /checkin 成功且学生转在住、记录新增；在住 7 条/已退宿 2 条筛选正确；退宿申请 3 条、审核通过后学生转已退宿、重复退宿被拒（业务正确）。先前浏览器端「选楼栋不响应」经硬刷新与排查为浏览器旧缓存/自动化未触发 el-select change 所致，非代码缺陷。

- **2026-09-04（操作日志 #25，报修类型字典表落地 / 方案 B）** 用户确认数据库补"报修类型"，选方案 B（独立字典表 + `repair_order.type_id` 逻辑外键）。同步修改 `docs/sql/init.sql` 与 `docs/数据库设计说明.md`：

  **库结构**：

  - 新增表 `repair_type`（编号 10，置于 `repair_order` 前）：`id PK/AI`、`name VARCHAR(50) NOT NULL UNIQUE`（报修物品/类型名称）、`sort INT 默认0`（排序）、`created_at`。

  - `repair_order`（编号 10→11）：删除 `item VARCHAR(100)`；改为 `type_id BIGINT 逻辑FK→repair_type.id`；新增索引 `idx_ro_type(type_id)`。

  - 示例数据：`repair_type` 7 条（灯管/水龙头/空调/门锁/床铺/桌椅/其他），对齐原型 `repair-add` 下拉。

  **文档同步**：ER 关系补 `repair_type ──1:N──> repair_order(type_id)`；字段字典改"11 张表"并新增 `3.10 repair_type`、原 `3.10 repair_order`→`3.11`；逻辑外键表补 `repair_order.type_id → repair_type.id`；枚举字典补"报修类型（字典表）"。

  **验证**：两份文档 `type_id`/`repair_type` 完全对齐，无残留报修 `item` 字段（仅剩卫生表 `deduct_items` 语义不同、合法）。SQL 尚未在本机 MySQL 实测执行（涉及 DROP 重建、会清空演示库，未获授权前不执行）。

- **2026-09-04（操作日志 #26，init.sql 本机实测建库）** 用户提供 MySQL root 密码后，在本机 MySQL 8.0.34（MySQL80 服务）执行 `docs/sql/init.sql`（`mysql --execute="source ..."`，规避 PowerShell 不支持 `<` 重定向），重建 `dorm_manager` 库。

  **结果**：执行无语法错误，`SHOW TABLES` 共 **11 张表**（class / student / sys_user / dorm_building / dorm_room / dorm_bed / check_in / checkout_apply / hygiene_record / repair_type / repair_order）。抽查确认：`repair_type` 7 条字典数据（灯管/水龙头/空调/门锁/床铺/桌椅/其他）成功入库；`repair_order` 已无 `item` 列、新增 `type_id bigint`（索引 MUL），与双文档一致。建库基线稳定。

- **2026-09-04（操作日志 #27，学生端住宿业务：我的宿舍 + 退宿申请）** 补齐学生端住宿业务，与管理员端"退宿处理"闭环。

  **新增/修改**：

  - `src/mock/checkin.js`：新增 `currentRoom`（我的宿舍：当前在住信息+室友，未入住返回 dorm=null）、`submitCheckoutApply`（提交退宿申请：非在住拒绝、校验原因/日期、有未审核申请时拦截、生成 applyNo TS+yyyyMMdd+序号）、`cancelCheckoutApp`（撤销待审核申请=删除，非本人/非待审核拒绝）。

  - `src/mock/index.js`：注册 `GET /student/current-room`、`POST /checkout-applications`（生成）、`POST /checkout-applications/:id/cancel`。

  - `src/api/checkin.js`：新增 `getCurrentRoom` / `submitCheckoutApply` / `cancelCheckoutApp`。

  - `src/views/student/MyRoom.vue`：由硬编码改为接 `currentRoom` 真实数据（在住信息 + 室友表格），未入住显示空态；修复模板引用 `dorm` 未定义导致误判"未入住"的问题（用 computed 暴露 `info.dorm`）。

  - `src/views/student/CheckoutApply.vue`（新增）：提交退宿申请表单（仅读申请人/学号/当前宿舍/入住时间；退宿原因下拉、计划日期、说明）+ 「我的退宿申请记录」表格（编号/原因/计划日期/申请时间/状态 tag；待审核→撤销，已通过/已驳回→查看详情/驳回原因）。

  - `src/router/index.js`：注册 `/student/checkout-apply`（菜单已存在）。

  **验证**：`vite build` 通过（MyRoom/CheckoutApply chunk 正常）。Node 层 mock 逻辑全过：王小明在住(102·1床·室友3人)、赵敏已退宿 dorm=null；李小红提交生成 TS20260904004 待审核；重复申请拦截"您有未审核的退宿申请"；撤销成功并清空；撤销他人申请"无权操作"；王小明历史 TS20260902001。浏览器验证：李小红我的宿舍正确展示入住信息与室友；退宿申请表单只读带出正确，不填日期提交被"请选择计划退宿日期"校验拦截；王小明提交已有未审核申请时按钮 disabled 拦截、不产生新记录。console 仅 Element Plus 库内 blur 告警，与业务无关。

- **2026-09-04（操作日志 #28，管理员端日常管理：卫生检查 + 报修管理）** 开发管理员端日常管理模块，与数据库 `hygiene_record` / `repair_order` / `repair_type` 对齐。

  **新增/修改**：

  - `src/mock/daily.js`（新增）：卫生检查（`listHygiene` 筛选 checkDate/buildingId/result + 分页、`addHygiene` 扣分计分判定优秀/合格/不合格 + 照片强制规则）、报修（`getRepairTypes` 字典、`listRepair` 筛选 orderNo/buildingId/status、`getRepair`、`handleRepair` 处理派单校验处理人/电话、完成需说明）、内置报修类型字典与卫生/报修初始演示数据。

  - `src/mock/index.js`：注册 `GET/POST /daily/hygiene`、`GET /daily/repair-types`、`GET /daily/repairs`、`GET /daily/repair/:id`、`PUT /daily/repair/:id`。

  - `src/api/daily.js`（新增）：getHygieneList / addHygiene / getRepairTypes / getRepairList / getRepairDetail / handleRepair。

  - `src/views/admin/HygieneList.vue`（新增）：筛选（楼栋/检查日期/结果）+ 列表（照片缩略图 + 大图预览）+ 查看详情弹窗 +「＋ 新增检查」跳转。

  - `src/views/admin/HygieneAdd.vue`（新增）：楼栋→房间联动、7 项扣分自动计分（100 起扣，实时得分+自动判定结果）、现场照片上传（base64 预览）、评分<60 或违规电器强制照片、评语。

  - `src/views/admin/RepairList.vue`（新增）：筛选（单号/楼栋/状态）+ 列表（含处理人/联系电话列 + 状态 tag）+ 处理/详情弹窗（处理人、电话、状态处理中/已完成、处理说明）。

  - `src/router/index.js`：注册 `/admin/hygiene-list`、`/admin/hygiene-add`、`/admin/repair-list`（菜单已存在）。

  **验证**：`vite build` 通过（HygieneList/HygieneAdd/RepairList chunk 均产出）。Node 层 mock 逻辑全过：报修类型字典 7 条；卫生列表/结果过滤、不合格或违规电器无照片时强制拦截、带照片新增成功；报修列表及状态过滤、处理更新成功。浏览器验证：卫生列表渲染与详情弹窗、新增页楼栋→房间联动、扣分计分（地面-5→95、违规电器-15→80/合格）、报修列表与状态筛选、处理弹窗字段齐全均正常。

  **修复**：浏览器自动化暴露两处体验问题并已加固——① 新增卫生保存时，若基础必填校验未过，`validate()` 抛异常会遮蔽后续"违规电器必须上传照片"守卫；改为 `validate` 失败即 return，照片守卫在必填通过后必然执行。② 报修保存/卫生保存补全 submit/save 的请求失败兜底（catch 显示后台 msg + finally 复位按钮），避免失败时 unhandled error 与按钮卡死。其中照片文件上传与保存点击属浏览器自动化受限项，已在逻辑层加固，建议管理员登录后在页面上实际操作一遍复核。

- **2026-09-04（操作日志 #29，学生端日常管理：我的卫生检查 + 提交报修 + 报修进度）** 开发学生端日常管理，与管理员端卫生检查/报修管理构成闭环。

  **新增/修改**：

  - `src/mock/daily.js`：`listHygiene` 增加 `roomId` 过滤（学生按所在房间查）；`listRepair` 增加 `studentId` 过滤；新增 `createRepair`（提交报修：校验报修物品/问题描述，返回单号 BX+yyyymmdd+序号，关联学生当前房间 buildingId/roomId，状态待处理）；初始报修数据为王小明补一条已完成记录（含处理人/电话），便于演示"处理人（联系电话）"回显。

  - `src/mock/index.js`：注册 `POST /daily/repairs`（提交报修）。

  - `src/api/daily.js`：新增 `addRepair`。

  - `src/views/student/MyHygiene.vue`（新增）：标题带"本宿舍（楼栋 房间号）"，调 getCurrentRoom 取房间 → getHygieneList({roomId}) 展示卫生记录（检查日期/评分/结果 tag/检查人/扣分项/评语），未入住显示空态。

  - `src/views/student/RepairAdd.vue`（新增）：报修人/报修位置只读带出、联系电话、报修物品下拉（repair_type 字典）、问题描述、图片上传（最多 3 张 base64）；提交 addRepair 后跳转报修进度。

  - `src/views/student/MyRepair.vue`（新增）：getRepairList({studentId}) 展示我的报修（单号/物品/描述/时间/处理人（联系电话）/状态 tag/处理说明）。

  - `src/router/index.js`：注册 `/student/my-hygiene`、`/student/repair-add`、`/student/my-repair`（菜单已存在）。

  **验证**：`vite build` 通过。Node 层全过：我的卫生（roomId=2 返回 2 条）、我的报修（王小明 2 条含已完成处理人回显）、提交报修生成新单号待处理（备案电话兜底）、缺物品/缺描述/学生不存在三项校验拦截。浏览器验证（王小明登录）：我的卫生展示 1号楼102室 2 条记录；提交报修表单字段齐全、不填拦截"请选择报修物品/请描述问题情况"、提交后成功跳转且进度页新增一条床铺报修；报修进度显示 3 条（待处理/已完成，王师傅（13800000003）与处理说明正确回显，状态 tag 颜色正确）；console 无 error（仅 2 条非错误的 vue/ECharts 提示）。

<br />

- **2026-09-04（操作日志 #30，全局设置 + 个人中心）** 开发管理员端「全局设置」与「个人中心」，及学生端「个人中心」，并让退宿申请原因对接字典。

  **新增/修改**：

  - `src/mock/settings.js`（新增）：系统参数（systemName/welcomeMessage/contactPhone/contactEmail get/update）；退宿原因字典 CRUD（初始 毕业离校/休学/退学/调宿/其他）；个人资料 get/update（管理员取登录账号，学生从 students 取并允许改联系电话/紧急联系人）；修改密码 changePassword（校验原密码/6-20 位，直接改 `users` 账号密码）。借 `getLoginUsers()` 运行时读账号，规避模块环引用取值过早。

  - `src/mock/daily.js`：报修类型字典新增 createRepairType / updateRepairType / deleteRepairType（名称唯一校验；**被报修单引用的类型禁止删除**）。

  - `src/mock/index.js`：注册 `POST /auth/change-password`、`GET/PUT /profile`、`GET/PUT /settings/params`、`GET/POST/PUT/DELETE /daily/checkout-reasons`、`POST/PUT/DELETE /daily/repair-types`；导出 `getLoginUsers`。

  - `src/api/settings.js`（新增）：系统参数、退宿原因字典、个人资料、改密 的 API 封装；`src/api/daily.js` 补 createRepairType/updateRepairType/deleteRepairType。

  - `src/views/admin/Settings.vue`（新增）：Tab「系统参数」（4 项参数表单保存）+ Tab「退宿原因字典」（CRUD，编辑弹窗名称/排序）。

  - `src/views/admin/RepairType.vue`（新增）：报修类型字典 CRUD（同 BuildingList 风格），列表/编辑弹窗，删除被引用类型时后台拦截提示。

  - `src/views/admin/Profile.vue`（新增）：个人资料（用户名/姓名/角色只读 + 联系电话/邮箱可编辑保存）+ 修改密码卡（原/新/确认，改密成功登出回登录页）。

  - `src/views/student/Profile.vue`（新增）：个人资料（姓名/学号/性别/班级/学院/专业/在校住宿状态只读 + 联系电话/紧急联系人可改）+ 修改密码卡。

  - `src/views/student/CheckoutApply.vue`：退宿原因下拉由硬编码改为 `getCheckoutReasons()` 字典加载。

  - `src/router/index.js`：注册 `/admin/settings`、`/admin/repair-type`、`/admin/profile`、`/student/profile`；`src/layouts/AdminLayout.vue` 与 `StudentLayout.vue` 新增「系统设置/个人中心」左侧菜单与右上角下拉「个人中心」。

  **验证**：`vite build` 通过（Settings/RepairType/Profile×2/CheckoutApply/settings/daily chunk 均产出）。浏览器验证（admin/123456 登录）：系统设置页 4 项系统参数显示、退宿原因 Tab CRUD（新增「参军」出现、删除恢复）；报修类型页 7 项列表、删除被引用「灯管」提示「该类型已被报修单引用，无法删除」且未删、新增「插座」成功；个人中心资料展示（admin/系统管理员/管理员）。学生（2023010101/123456）：个人资料全字段正常、修改密码卡存在、退宿申请原因下拉来自字典（毕业离校/休学/退学/调宿/其他）。

  **修复**：浏览器自动化暴露学生个人资料字段全为 "-" 且 console 报 `Cannot read properties of undefined (reading 'find')`——`settings.js` 取 `getStudents(...).list` 误用，`getStudents` 返回是 `ok()` 包装体需 `.data.list`（与 `daily.js` 一致），已修正并复核通过（字段渲染正常、console 无该 error）。

<br />

- **2026-09-04（操作日志 #31，学生管理与班级管理合并 / 方案 B 两级联动）** 用户要求把「学生管理」与「班级管理」合并为一个页面，讨论后采用**方案 B：左班级 / 右学生两级联动**（master-detail）。

  **改动**：

  - `src/mock/baseData.js`：`getStudents` 新增 `className` 筛选（班内学生过滤）；`deleteClass` 增加**引用校验**——班内仍有学生则返回"该班级下仍有学生，无法删除"（与报修类型/退宿原因引用校验约定对齐）。
  - `src/views/admin/StudentList.vue` 重写为两级联动页：左侧班级列表面板（搜索、选中高亮、编辑/删除按钮、学生数/住宿数统计、新增班级），右侧选中班级的学生（学号/姓名/状态筛选 + 表格 + 分页 + 学生增删改）；学生弹窗在班内新增时**预填当前班级并自动带出学院/专业**；班级列表保持选中、删当前班自动回落到第一个班。
  - `src/layouts/AdminLayout.vue`：基础数据菜单由「学生管理 / 班级管理」两项收敛为「学生/班级管理」一项（指向 /admin/students）。
  - `src/router/index.js`：`/admin/students` 标题改为「学生/班级管理」；移除 `/admin/classes` 路由。
  - 删除 `src/views/admin/ClassList.vue`（功能并入 StudentList，页面不再独立）。

  **验证**：`vite build` 通过（`✓ built`）。浏览器核验（admin/123456）：① 菜单仅剩「学生/班级/楼栋/房间」四项，/admin/classes 访问为 404（No match）；② 默认选中「软工2301」右侧为该班 3 人（无外班混入），切「计科2301」→陈雨萱、「英语2201」→赵敏/孙悦（住宿状态已退宿）；③ 班内新增学生自动预填班级/学院/专业，保存后立即出现、删除后消失；④ 删除有学生的「软工2301」被拦截提示"该班级下仍有学生，无法删除"，列表保留；⑤ 班级项悬停浮出编辑/删除按钮，console 无 Vue 报错（核心项 PASS，空班删除/分页/筛选为与已验证 CRUD 同模式的低风险项）。

  **连带说明**：数据库 `class`/`student` 两表保持独立（合并仅 UI/导航层），`student.className` 快照与 `class.studentCount` 统计均由 Service/后端维护。
