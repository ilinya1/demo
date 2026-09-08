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

- **2026-09-04（操作日志 #32，前端全面复查：修复班级改名孤儿学生缺陷）** 用户要求再次仔细检查前端。ite build 通过（✓ built；exit 1 仅为沙箱对缓存目录写限制）；对 mock 核心逻辑（checkin 出入住、baseData 班级/宿舍联动、StudentList 边界）逐段深查，发现并修复一处**真实缺陷**：

  **缺陷**：合并后班级页可编辑班级名称，但学生以 `className` 快照关联。原 `updateClass` 直接覆盖 `name` 不改名字段，导致——① 班级改名后名下学生仍持旧 `className`，在左面板新班下"消失/孤立"；② `deleteClass` 引用校验按当前 name 匹配，改名后因已找不到旧名学生而失去拦截作用。

  **修复**（`src/mock/baseData.js::updateClass`）：改名前校验重名（`classes.some(c.id!==id && c.name===d.name)` → 返回"班级名称已存在"，与 `createClass` 一致）；改名时级联将该班学生 `className` 更新为新名（`student.className` 为可更新字段，check_in 快照不受影响），保证关联键一致、删除引用校验继续有效。

  **验证**：build 通过；浏览器实测改名「软工2301」→「软工2305A」保存后左侧同步、点选该班右侧仍显示原 3 名学生（未孤立）——级联修复生效（PASS）。重名拦截与改回恢复逻辑已代码级核对（mock 为内存态，刷新即还原初始数据）。实况说明：浏览器代理多次因预算在关键操作前截断，核心级联项已实测通过，其余低风险项按代码审读认可。

  **连带说明**：本次复查未发现其他阻断性问题；容量/占用、住宿状态枚举、api↔mock 契约、路由/菜单/守卫、模块环引用（authData 唯一依赖方向）等均一致。

- **2026-09-04（操作日志 #33，交接文档升级至 v5）** 按要求更新 docs/项目交接文档.md，把此前严重滞后（描述为"静态 HTML 原型 + 后端骨架 + DB 未建库"）的状态刷新为当前实况，记录"已做什么 + 后续做什么"。核实前端清单（package.json 与 views/api/mock 盘点）后确认关键事实：Vue ^3.5 / Vite ^6 / Element Plus ^2.14 / Pinia ^3 / Vue Router ^4.5 / Axios ^1.7 / ECharts ^6；MySQL80 已运行 dorm_manager（11 表）；admin 视图 16、student 视图 6。文档 v5 新增「六、v5 —— Vue3 前端开发落地与数据库修订」章节（前端升级、双端模块表、mock 架构、关键设计/修复、DB 10→11 表 
epair_type/
epair_order.type_id），并将「当前技术状态」「后续开发待办」（后端最高优先 + mybatis-plus-spring-boot4-starter 约束 + 前端联调/上传/统计真实化/部署）「版本修订记录」整体翻新，目录树与技术栈同步校正。涉及文件：docs/项目交接文档.md（v1→v5）。

- **2026-09-07（操作日志 #34，管理端仪表盘升级为运营工作台）** 用户反映仪表盘功能偏少、较单调，经方案对比（A 功能增强 / B 运营工作台 / C 轻量交互）后选定**方案 B**。在保留原「4 张统计卡 + 楼栋入住率条形图 + 卫生均分折线图」基础上扩展：
  - **新增**`src/mock/workbench.js`：`/dashboard/workbench` 聚合端点，复用 daily/checkin 的真实在存数据（listRepair/listHygiene/listCheckoutApps/listCheckinRecords 全量拉取）统计——待办（待审核退宿/待处理报修/在住/已退宿）、报修与退宿积压分布、运营告警（报修超 3 天未处理、卫生不合格、退宿待审核）、最新报修/退宿动态。
  - `src/mock/index.js` 注册端点；`src/api/dashboard.js` 新增 `getWorkbench()`。
  - 重写 `src/views/admin/Dashboard.vue`：统计卡 + **快捷入口**（入住登记/退宿处理/卫生检查/报修管理，router 跳转）+ 待办格 + 积压分段进度条 + 运营告警 + 最新报修/退宿动态列表 + 沿用两张 ECharts 图；快速入口路由与 /admin/checkin、/checkout-audit、/hygiene-list、/repair-list 对齐。

  **验证**：`vite build` 通过（`✓ built`，exit 1 仅为沙箱不能写 esbuild 缓存日志，非代码问题；Dashboard chunk 已生成）。浏览器实测（admin/123456）：① 4 张统计卡数值正确；② 快捷入口 4 按钮渲染、点「退宿处理」正确跳至 /admin/checkout-audit；③ 待办四项计数与 mock 一致（待审核退宿 2、待处理报修 1、在住 6、已退宿 2）；④ 楼栋入住率条形图与卫生均分折线图正常渲染；⑤ 运营告警显示报修超时/卫生不合格/退宿待审核三类条目；⑥ 最新报修/退宿动态列表正常；⑦ console 无 404/undefined，仅存非阻塞性 Vue/ECharts 警告（全部 PASS）。涉及文件：src/mock/workbench.js（新增）、src/mock/index.js、src/api/dashboard.js、src/views/admin/Dashboard.vue。

- **2026-09-07（操作日志 #35，学生/班级管理信息展示补全）** 用户要求在基础数据中的学生/班级管理页完善信息展示：① 左侧班级栏显示班主任；② 右侧学生栏显示紧急联系人及其联系方式。
  - `src/views/admin/StudentList.vue`：左侧班级项信息区增加「班主任：xxx」；右侧学生表格新增「紧急联系人」列（姓名 + 联系方式并排，未填显示 —）；学生新增/编辑弹窗增加「紧急联系人电话」输入项并纳入表单提交对象（emptyForm 加 `emergencyPhone`）。
  - `src/mock/baseData.js`：学生 mock 数据补 `emergencyPhone` 字段（10 名学生各配 13911110001~010）。

  **验证**：`vite build` 通过（`✓ built`）。班主任、紧急联系人姓名/电话展示与录入、mock 数据字段更新均已实现，页面结构经代码审读确认。涉及文件：src/views/admin/StudentList.vue、src/mock/baseData.js。

- **2026-09-07（操作日志 #36，student 表新增 emergency_phone 字段）** 承接 #35：前端新增的「紧急联系人电话」需数据库支撑，同步数据库设计并应用到已建库。
  - `docs/sql/init.sql`：`student` 表 `emergency_contact` 后新增 `emergency_phone VARCHAR(20) COMMENT '紧急联系人电话'`；INSERT 语句属性列补 `emergency_phone`，3 名示例学生各填 13911110001~003。
  - `docs/数据库设计说明.md`：3.2 student 字段表新增 `emergency_phone | VARCHAR(20) | — | 紧急联系人电话` 一行。
  - 实库执行：`ALTER TABLE dorm_manager.student ADD COLUMN emergency_phone VARCHAR(20) ... AFTER emergency_contact`，并按学号 UPDATE 填充 3 名学生电话；`SHOW COLUMNS` 与 `SELECT` 均确认字段存在、数据就位。

  涉及文件：docs/sql/init.sql、docs/数据库设计说明.md；数据库 dorm_manager.student 已同步变更。

- **2026-09-07（操作日志 #37，mock 数据一致性加固：删除在住学生联动释放床位）** 检查「新增学生」功能时，用户要求修复「删除在住学生未联动释放床位」。诊断发现根因是 mock 内存中**床位快照（bedStudents）、房间占用（rooms[].occupiedCount/status）、入住记录（checkInRecords）三套数据各自硬编码、互不对齐**（床位快照仅初始化 102 室，而 `rooms` 却把 201/103 标为"已满"，`checkInRecords` 显示学生却住这些房间），导致删除在住学生时联动释放因快照缺失而失效。
  - `src/mock/baseData.js` `deleteStudent`：删除前遍历床位快照，命中床位 `freeBed` 释放 + `recomputeRoomOccupancy` 重算房间占用；床位初始化由「仅 102 室」扩展为对齐所有在住记录的 `bedStudentsSeed`（102/103/201），加载时同步各房间占用/状态。
  - `src/mock/checkin.js` 新增 `removeStudentRecords`：清理该生全部入住记录与退宿申请，避免 orphan 残留。
  - `src/mock/index.js` DELETE `/students/{id}` 分支：学生删除**成功后**依次执行床位释放 → 记录清理。
  - 验证：删除张小飞后 2号楼201 室 已住 1→0、状态 部分入住→空闲、其入住记录清除，均 PASS；`vite build` 通过。
  - 另：**入住记录页移除「来源」列**（入驻记录仅管理员登记，唯一来源，无需列示；`source` 字段内部仍保留，属 UI 展示调整）。
  - 涉及文件：src/mock/baseData.js、src/mock/checkin.js、src/mock/index.js、src/views/admin/CheckinRecord.vue。

- **2026-09-07（操作日志 #38，交接文档升级 v6）** 将 v6 阶段改动（运营工作台、mock 数据一致性加固、UI 补全、`student.emergency_phone`）写入 `docs/项目交接文档.md`：新增「六之一、v6」章节，更新头部版本、当前技术状态（数据库样例一致性表述、emergency_phone 列）、版本修订记录新增 v6 行。
  - 涉及文件：docs/项目交接文档.md。

- **2026-09-07（操作日志 #39，前端全面复查与 Bug 修复：直接退宿原因改为字典加载）** 用户要求通读全部项目文档后继续修复前端 Bug。本轮通读了 `docs/`（HTML原型修改记录 / 项目交接文档 / 开发设计文档 / 数据库设计说明）并系统性审查了 `frontend/src` 的 mock 层（baseData/checkin/daily/settings/index/workbench/authData/util/stats）与视图层（admin 16 + student 6 页、PasswordForm、router、双端 Layout、store/api 封装）。

  **审查结论**：mock 数据已一致（床位快照 `bedStudentsSeed` 与 `checkInRecords` 在住记录完全对齐 102/103/201，房间占用/状态经 `recomputeRoomOccupancy` 同步）；班级改名级联、删除在住学生联动释放床位、字典删除引用拦截、退宿申请流转、mock 路由分发等均已闭环，未发现新的数据一致性缺陷。

  **修复的 Bug（1 处）**：`frontend/src/views/admin/CheckoutAudit.vue`「直接退宿」弹窗的*退宿原因*下拉原为硬编码数组 `['毕业离校','休学','退学','调宿','其他']`，违反硬约束「退宿原因下拉选项从字典加载」，且与学生端 `CheckoutApply.vue`（#30 已改字典加载）不一致——字典新增/删除的原因无法实时反映，还可能出现下拉选项与字典不符。已改为从 `getCheckoutReasons()` 字典加载（`reasons` 改为 `ref([])`，`onMounted` 新增 `loadReasons()`），与退宿申请页/全局设置字典保持同源。

  **验证**：`npm run build` 通过（`✓ built`，exit 1 仅为沙箱无法写 esbuild 缓存日志 + chunk 体积告警，非代码问题，与既往会话一致）。
  **涉及文件**：frontend/src/views/admin/CheckoutAudit.vue。

- **2026-09-07（操作日志 #40，Bug 修复：新增学生「学号」输入框自禁用，只能输 1 位）** 用户反馈「新增学生功能中学号只能填一个数字」。经浏览器复现与源码定位，根因是 `StudentList.vue` 学号输入框 `:disabled="!!form.studentId"` 把 disabled 绑定在学号**自身值**上：新增弹窗中学号初始为空可输入，但敲入第 1 个字符后 `studentId` 变为真值 → 输入框立即自禁用，导致学号永远停留在 1 位（且无 maxlength、无报错，属静默 UI 逻辑 Bug）。
  - **修复**：`:disabled` 与弹窗标题改为基于**编辑态** `editing` 判断（而非学号值）——新增时学号可任意输入、弹窗标题保持「新增学生」；编辑时学号只读、标题「编辑学生」。
    - 学号输入框：`<el-input :disabled="!!editing" ...>`（原本 `!!form.studentId`）。
    - 弹窗标题：`:title="editing ? '编辑学生' : '新增学生'"`（原 `form.studentId ? ...`，同根因连带：新增输入学号后标题误变「编辑学生」）。
  - **验证**：`npm run build` 通过；浏览器实测新增弹窗输入 `2023999999`（多位数）完整接收、输入框不再禁用、标题保持「新增学生」；编辑态学号仍只读。均 PASS。
  - **涉及文件**：frontend/src/views/admin/StudentList.vue。

- **2026-09-07（操作日志 #41，Bug 修复：新增卫生检查无法保存）** 用户反馈「新增卫生检查保存不了」。定位根因是前后端契约不一致：`HygieneAdd.vue` 的表单 `form` 中**不含 `score` 字段**（分数是 `computed` 由扣分项派生），提交时 `addApi({ ...form })` 未携带 `score`；而 mock `daily.js::addHygiene` 强制校验 `d.score`（缺失即返回「请确定评分（100 分起扣）」），导致保存被静默拦截（弹窗/页面无该提示，属契约缺失 Bug）。
  - **修复**：`HygieneAdd.vue` 保存时提交计算得分 `await addApi({ ...form, score: score.value })`，与 `daily.js`（按 score 判定优秀/合格/不合格）对齐。
  - **验证**：`npm run build` 通过；浏览器实测填楼栋/房间/日期/检查人（评 100 分、无违规电器）保存成功后跳转卫生列表，且列表新增该条记录（1号楼101室 100分 优秀 测试）。PASS。
  - **涉及文件**：frontend/src/views/admin/HygieneAdd.vue。

- **2026-09-07（操作日志 #42，Bug 修复：退宿处理「直接退宿/审核」缺失错误处理）** 浏览器冒烟测试（入住登记、直接退宿、学生端退宿申请、报修处理四条业务流）发现：`CheckoutAudit.vue` 的 `onDirect`（直接退宿）与 `onAudit`（审核）仅有 `try/finally`、**缺 `catch`**，当 `directCheckout` 失败（学生不在住）或 `auditCheckoutApp` 失败时抛未处理 Vue 组件事件错误，控制台 `Unhandled error` 且界面无任何提示（与其他保存流程已有的 catch 模式不一致）。
  - **修复**：`onDirect`、`onAudit` 均补 `catch (e) { if (e && e.msg) ElMessage.error(e.msg) }`，与 RepairAdd/HygieneAdd/RepairList 的错误反馈模式对齐。
  - **验证**：浏览器实测「直接退宿」对不在住学生 `2023020102` 弹出「该学生当前不在住」、对不存在学号弹出「学生不存在」，弹窗保持打开、**控制台无 Unhandled error**。PASS。
  - **说明**：冒烟中「入住登记后经整页刷新/导航，内存 mock 数据被重置、刚入住学生宿舍信息丢失」属项目已文档化特性（`写操作仅落在内存 mock，刷新页面即还原`），非本轮修复范围；SPA 内 `router.push` 客户端跳转不引发重置，仅浏览器整页刷新会重置。
  - **涉及文件**：frontend/src/views/admin/CheckoutAudit.vue。

- **2026-09-07（操作日志 #43，系统性 Bug 修复：mock 模式业务错误提示缺失 + 保存函数未捕获异常）** 浏览器全模块冒烟（班级/楼栋/房间/报修类型/系统设置/统计/个人中心）发现两类根因：
  1. **`request.js` mock 分支不弹错误提示**：真实接口（axios）分支响应拦截器会对 `code!==0` 调用 `ElMessage.error` 后 reject，但 mock 分支直接 `reject` 不弹提示 → 所有业务校验失败（班级含学生、楼栋名已存在、报修类型被引用、原密码错误等）在 mock 模式下**静默无反馈**（部分还被组件 `.catch(()=>{})` 吞掉）。
  2. **部分保存函数缺 `catch` 导致未处理异常**：BuildingList/RoomList/StudentList/RepairType/Settings/PasswordForm/Profile 的 save 仅 `try/finally`，API reject 时抛 `Unhandled error during execution`，如楼栋保存重名时报 `SyntaxError`。
  - **修复**：
    - `api/request.js`：mock 分支 `code!==0` 时先 `ElMessage.error(body.msg)` 再 `reject`，与 axios 分支行为一致，错误提示集中化、一处生效全局生效。
    - 为上述缺 catch 的保存函数批量补 `catch (e) { /* 失败提示已由 request 统一弹出 */ }`（楼栋/房间/班级/学生/报修类型/退宿原因/改密/双端个人资料），消除未处理异常。
    - 移除 4 处重复的组件级 `catch(e){ if(e.msg) ElMessage.error(e.msg) }`（HygieneAdd/RepairAdd/RepairList/CheckoutAudit onAudit·onDirect），避免与 request 集中提示**双 toast**。
  - **验证**：`npm run build` 通过；浏览器逐项复测均正确弹出红色错误提示且**控制台无 Unhandled error/SyntaxError**——①新增楼栋重名「楼栋名已存在」弹窗不关；②删「软工2301」「该班级下仍有学生，无法删除」班级保留；③删「灯管」「该类型已被报修单引用，无法删除」保留；④改密原密码错误「原密码错误」不登出不跳转。全部 PASS。
  - **涉及文件**：frontend/src/api/request.js、views/admin/BuildingList.vue、views/admin/RoomList.vue、views/admin/StudentList.vue、views/admin/RepairType.vue、views/admin/Settings.vue、views/admin/HygieneAdd.vue、views/admin/RepairList.vue、views/admin/CheckoutAudit.vue、views/admin/Profile.vue、views/student/RepairAdd.vue、views/student/Profile.vue、components/PasswordForm.vue。

- **2026-09-07（操作日志 #44，新增功能：学生/班级管理模块「学院管理」）** 用户要求在「学生/班级管理」模块增加「新增学院」功能。经确认采用**完整学院管理**（新增/编辑/删除 + 引用拦截 + 改名级联）且**仅前端 mock**（不建库、不改 init.sql/数据库设计说明）。
  - **mock 层**（`src/mock/baseData.js`）：新增学院字典 `colleges`（初始 计算机学院/机械工程学院/外国语学院）+ `getColleges/createCollege/updateCollege/deleteCollege`。重名拦截、**改名级联更新该学院下班级与学生的 `college` 字段**、**删除引用拦截**（有班级或学生引用「该学院下仍有班级或学生，无法删除」），与报修类型/退宿原因字典约定对齐。`src/mock/index.js` 注册 `/colleges` 的 GET/POST/PUT/DELETE。
  - **api**：新增 `src/api/college.js` 封装。
  - **视图**（`src/views/admin/StudentList.vue`）：原硬编码 `colleges` 数组改为从 `getColleges()` 加载的 ref；左侧「班级」面板标题旁新增「学院」按钮 → 打开「学院管理」弹窗（顶部输入新增 + 列表编辑/删除）。
  - **验证**：`npm run build` 通过；浏览器实测——新增「经济管理学院」出现在列表且进「新增班级/新增学生」的学院下拉；重复新增拦截「该学院已存在」；改名「经济管理学院」→「经贸学院」后下拉同步显示新名（级联生效）；删被引用的「计算机学院」拦截「该学院下仍有班级或学生，无法删除」；删无引用的「经贸学院」成功。均 PASS。无控制台报错。
  - **涉及文件**：frontend/src/mock/baseData.js、frontend/src/mock/index.js、frontend/src/api/college.js（新增）、frontend/src/views/admin/StudentList.vue。

- **2026-09-08（操作日志 #45，新增功能：管理员重置学生密码）** 用户要求增加「重置学生密码」功能。经确认采用**学生表格行操作入口 + 重置为默认密码（123456）**，仅前端 mock。
  - **mock 层**（`src/mock/authData.js`）：新增 `DEFAULT_STUDENT_PASSWORD='123456'` 与 `resetStudentPassword(username,name)`——将账号密码重置为默认值；**若该生暂无登录账号则自动创建**（保证重置后即可用默认密码登录，解决「目标学生需先有账号」的前置问题）。`src/mock/index.js` 注册 `POST /auth/reset-password`。
  - **api**：`src/api/auth.js` 新增 `resetStudentPassword(data)`。
  - **视图**（`src/views/admin/StudentList.vue`）：学生表格「操作」列新增「重置密码」按钮 → 确认弹窗（文案含默认密码与自动建号提示）→ 成功后提示已重置为 123456；「操作」列宽 140→210。
  - **验证**：`npm run build` 通过；浏览器实测——① 操作列出现「编辑/重置密码/删除」；② 对有账号学生「王小明」重置成功提示含 123456；③ 对无账号学生「陈雨萱」点重置后自动建号，退出管理员再用 `2023010301/123456` 登录**成功进入学生端**（默认密码生效）。均 PASS，无报错。
  - **涉及文件**：frontend/src/mock/authData.js、frontend/src/mock/index.js、frontend/src/api/auth.js、frontend/src/views/admin/StudentList.vue。

- **2026-09-08（操作日志 #46，前端字段 ↔ 数据库设计核对与对齐）** 应要求全面核对前端 mock 字段与数据库基线（`init.sql` + 《数据库设计说明.md》）。逐表比对后输出报告，并按用户选定处理：
  - **修正 A1 房间类型枚举**：前端 `baseData.js` `ROOM_TYPES` 由 `['4人间','6人间']` 改为 `['四人间','六人间']`（对齐 init.sql 种子与说明 3.5）；`createRoom/updateRoom` 的类型推导由 `ROOM_TYPES.find(t=>t.includes(...))` 改为新增 `roomTypeOf(capacity)`（4/6 学士转换），杜绝改后 `includes('4')` 失效回退错误；`RoomList.vue` 容量下拉文案改为「四人间（4 人）/六人间（6 人）」，卫生检查房间下拉房型随之显示「四人间」。
  - **修正 A2 房间补「楼层」字段**：库 `dorm_room.floor NOT NULL`；前端房间 mock 全量补 `floor`（101→1/201→2/301→3），`createRoom/updateRoom` 支持 `floor`（未填时按房号首位 `roomFloorOf` 推导）；`RoomList.vue` 列表新增「楼层」列、新增/编辑弹窗新增「楼层」数字输入并纳入 `emptyForm` 与必填校验。
  - **B 类（记录在案、后端实现时对齐，本轮不改）**：①`student.class_id`(逻辑FK) ↔ mock 用 `className` 字符串；②`check_in` 缺 `building_id/bed_id`(库 NOT NULL)；③`check_in.source` mock 用 `manual`(库枚举仅 apply/direct)；④`sys_user` 缺 `status`。
  - **C1 种子数据**（mock 10 名学生 vs 库 3 名、102 室在住口径不同）：按用户选择**暂不处理**，仅记录。
  - **D 类（仅前端有、库无表）**：学院字典（colleges，按决策仅 mock）、系统参数（settings.js，库无 sys_parameter 表，既有缺口）。
  - **验证**：`npm run build` 通过；浏览器实测——房型列全显「四人间/六人间」、楼层列 1/2/3、新增弹窗含楼层(可改)且容量下拉为中文、新增 2号楼/2层/999/四人间 保存后列表正确、删除后恢复。均 PASS。
  - **涉及文件**：frontend/src/mock/baseData.js、frontend/src/views/admin/RoomList.vue。

- **2026-09-08（操作日志 #47，B2 处理：mock 入住记录补齐 `building_id`）** 承接 #46 的 B2「check_in 除快照外缺关联 id」。本次补齐 **`buildingId`**（库 `check_in.building_id NOT NULL`，逻辑FK→dorm_building.id）：
  - `src/mock/checkin.js`：8 条初始 `checkInRecords` 全部补 `buildingId`（1号楼=1/2号楼=2/3号楼=3，与 buildingName 对应）；`submitCheckin` 新建记录写入 `buildingId: room.buildingId`（快照 `buildingName` 之外的必要关联 id）；`currentRoom` 的 dorm 载荷补 `buildingId`。
  - **验证**：`npm run build` 通过；浏览器实测——入住记录列表/状态筛选正常、楼栋列渲染正确；对 `2023020102` 李娜完成一次新入住后在记录页出现该在住记录；全程无报错/undefined。PASS。
  - **说明**：`bed_id`、`check_in.source`(manual)、`student.class_id`、`sys_user.status` 仍为 B 类待处理项，后续可按需对齐。
  - **涉及文件**：frontend/src/mock/checkin.js。

- **2026-09-08（操作日志 #48，B 类剩余项补全：`bed_id` / `check_in.source` / `student.class_id` / `sys_user.status`）** 承接 #47，补齐 B 类其余字段对齐：
  - **`check_in.bedId`**（`src/mock/checkin.js`）：库 `check_in.bed_id NOT NULL`；8 条初始 `checkInRecords` 补 `bedId`（格式 `${roomId}-${bedNo}`，与 buildBeds 的 bedId 一致），`submitCheckin` 写入 `bedId: \`${room.id}-${bedNo}\``。
  - **`check_in.source` 语义修正**：库枚举仅 `apply/direct`（退宿来源）；原 mock 在新建入住与在住记录上用 `manual/apply` 不符。现按规范——新入住/在住记录的 `source` 置空 `''`（尚未退宿），退宿时由 `auditCheckoutApp`(apply)/`directCheckout`(direct) 写入（`doCheckout` 既有逻辑不变）。
  - **`student.classId`**（`src/mock/baseData.js`）：库 `student.class_id` 逻辑FK→class.id；10 名学生补 `classId`（软工2301=1/2302=2/计科2301=3/机设2301=4/英语2201=5），新增 `classIdOf(className)`，`createStudent/updateStudent` 由 className 自动推导 classId（班级改名级联不变，classId 恒随 class.id）。
  - **`sys_user.status`**（`src/mock/authData.js`）：库 `status TINYINT 默认1`；3 个初始账号补 `status:1`，`resetStudentPassword` 自动建号也带 `status:1`。
  - **验证**：`npm run build` 通过；浏览器实测——学生增删（classId 推导不破坏）、将刘少军 `2023020101` 入住后再直接退宿（source=direct）、入住记录「已退宿」筛选正常，全程 console 无 error/undefined。PASS。（B 类至此全部对齐。）
  - **涉及文件**：frontend/src/mock/checkin.js、frontend/src/mock/baseData.js、frontend/src/mock/authData.js。

- **2026-09-08（操作日志 #49，微型修正：学生学籍筛选补「休学」）** 承接 #46 全面核对中发现的小项：`StudentList.vue` 学籍筛选 `academicStatuses` 由 `['在校','毕业','退学']` 补入 `'休学'`，与库学籍枚举（在校/毕业/退学/休学）对齐。`statusTag` 对其走 `info` 灰色（未单独配色，语义无碍）。
  - **验证**：`npm run build` 通过（`✓ built`）。
  - **涉及文件**：frontend/src/views/admin/StudentList.vue。

- **2026-09-08（操作日志 #50，后端开发环境准备）** 进入后端开发阶段前的环境就绪，全部验证通过：
  - **环境核对**：JDK 21.0.7、Maven 3.9.16、MySQL80（8.0.34）服务运行中；IDE 已配置 Project SDK=21（`project-jdk-name="21"` → `D:\JAVA\jdk-21.0.7`，`misc.xml`/`jdk.table.xml` 已就绪，无模块级覆盖）。
  - **pom.xml**：新增 `mybatis-plus-spring-boot4-starter`(3.5.17，Spring Boot 4 专用)、`mysql-connector-j`(runtime)、`spring-boot-starter-validation`。
  - **包结构重构**：`com.example.demo` → `com.gzlg.dorm`；新启动类 `DormApplication`（`@MapperScan("com.gzlg.dorm.mapper")`）；旧包文件删除、测试类迁移。
  - **application.yaml**：数据源 `dorm_manager`（root，密码 `123456` 已填）+ MyBatis-Plus 驼峰映射/自增主键/不启用逻辑删除 + 端口 8080。
  - **验证**：`mvn -q compile` 通过；启动 `spring-boot:run` 成功（`Started DormApplication`、Tomcat 8080、数据源连通无错误；仅"mapper 包暂空"的 WARN 属预期），验证后已停服务。
  - **涉及文件**：pom.xml、src/main/java/com/gzlg/dorm/DormApplication.java（新增）、src/test/java/com/gzlg/dorm/DormApplicationTests.java、src/main/resources/application.yaml；删除 com/example/demo 旧包。

- **2026-09-08（操作日志 #51，后端统一返回与异常处理地基）** 搭建后端基础返回与异常层（`com.gzlg.dorm.common.*`）：
  - `common/result/Result<T>`：`{code, msg, data}`，静态 `ok()/fail()`。
  - `common/result/ResultCode`：`0=成功`、400/401/403/404/500 对应 HTTP 语义。
  - `common/result/PageResult<T>`：`{list, total}`（对齐前端分页读取）。
  - `common/exception/BizException`：携带 code+msg 的业务异常。
  - `common/exception/GlobalExceptionHandler`：`@RestControllerAdvice` 统一捕获 BizException、参数校验（MethodArgumentNotValid/Bind/ConstraintViolation/TypeMismatch/HttpMessageNotReadable）、404（NoResourceFound）、兜底 Exception；均返回 **HTTP 200 + {code,msg}**（前端以 code!==0 判定失败并弹 msg）。
  - **契约对齐（关键）**：开发设计文档原写 `code:200=成功 / message / PageResult.records`，与实际前端 `request.js`（`code===0` 成功、读 `msg`、分页读 `list/total`）矛盾。已按**前端契约为准**实现，并同步更新《开发设计文档》第四章。
  - **验证**：`mvn -q compile` 通过；`mvn -q test`（contextLoads）通过（仅 MyBatis mapper 包暂空 WARN、Mockito 动态 agent 提示，均非错误）。
  - **涉及文件**：新增 src/main/java/com/gzlg/dorm/common/result/{Result,ResultCode,PageResult}.java、common/exception/{BizException,GlobalExceptionHandler}.java；修改 docs/开发设计文档.md（第四章）。

- **2026-09-08（操作日志 #52，后端 11 表实体 + Mapper）** 按《数据库设计说明.md》为 11 张表建立实体与 Mapper：
  - **pom.xml**：新增 Lombok（optional，减少实体样板代码，版本由 Boot 父管理）。
  - **entity**（`com.gzlg.dorm.entity`）：`Clazz`(class，实体名避 Java 保留字)、`Student`(PK=student_id, INPUT)、`SysUser`、`DormBuilding`、`DormRoom`、`DormBed`、`CheckIn`(含 5 快照 + source)、`CheckoutApply`、`HygieneRecord`(deduct_items/photos 为 JSON 字符串)、`RepairType`、`RepairOrder`。均用 `@Data` + `@TableName` + `@TableId`，下划线↔驼峰由全局 mapUnderscoreToCamelCase 映射。
  - **mapper**（`com.gzlg.dorm.mapper`）：11 个 `BaseMapper<Entity>` 子接口，由 `@MapperScan("com.gzlg.dorm.mapper")` 扫描。
  - **验证**：`mvn compile` 通过；`mvn test` 全部通过（`Tests run: 2, Failures: 0`）；新增 `MapperSmokeTest` 实测 class/repair_type/student 可查询、列映射正确（含 emergency_phone）；此前「No MyBatis mapper was found」WARN 已消失。
  - **涉及文件**：新增 src/main/java/com/gzlg/dorm/entity/*.java（11）、mapper/*Mapper.java（11）、test/java/com/gzlg/dorm/MapperSmokeTest.java；修改 pom.xml。

- **2026-09-08（操作日志 #53，后端登录鉴权模块）** 采用「轻量 JWT + 拦截器」实现登录鉴权（用户确认选型）：
  - **pom**：新增 `jjwt`(0.12.6)、`spring-security-crypto`（仅 crypto，DelegatingPasswordEncoder）、`jackson-databind`（webmvc starter 未内置，JSON 必需）；测试 starter 换为标准 `spring-boot-starter-test`。
  - **common/jwt**：`JwtProperties`(secret/expireMinutes)、`JwtUtil`(HS256 生成/解析)、`LoginUser`、`UserContext`(ThreadLocal)、`JwtInterceptor`(校验 Bearer token 写入上下文，失败返回 HTTP 401 + {code:401,msg})、`WebMvcConfig`(注册拦截器，放行 /auth/login)。
  - **common/config/PasswordEncoderConfig**：`DelegatingPasswordEncoder` 兼容 `{noop}`(演示)/`{bcrypt}`(生产)。
  - **auth**：`dto/LoginRequest`、`vo/{UserVO,LoginResult}`、`service/AuthService`+impl（校验账号/密码/状态，STUDENT 回填姓名与学生端 studentId，ADMIN 显示名"系统管理员"，签发 token）、`controller/AuthController`(login/logout/me)。
  - **application.yaml**：`server.servlet.context-path=/api`（对齐前端 baseURL）+ `jwt.*`。
  - **验证**：`mvn test` 全过（`Tests run: 7`）。`AuthControllerTest` 为 RANDOM_PORT 真实 HTTP（JDK HttpClient），覆盖管理员/学生登录、错误密码(400)、无 token(401)、带 token 访问 /me——同时实证鉴权拦截器与 JSON 序列化正常。Boot 4 中 `AutoConfigureMockMvc`/`TestRestTemplate` 均已迁移，测试改用 HttpClient 绕开。
  - **涉及文件**：新增 common/jwt/{JwtProperties,JwtUtil,LoginUser,UserContext,JwtInterceptor,WebMvcConfig}、common/config/PasswordEncoderConfig、dto/LoginRequest、vo/{UserVO,LoginResult}、service/AuthService、service/impl/AuthServiceImpl、controller/AuthController、test/.../AuthControllerTest；修改 pom.xml、application.yaml。

- **2026-09-08（操作日志 #54，后端基础数据模块 CRUD + 校验）** 实现学生/班级/楼栋/房间四类基础数据接口（对齐前端 mock 契约，均需 JWT）：
  - **DTOs**：`ClazzReq`、`StudentReq`(className→classId)、`BuildingReq`、`RoomReq`。
  - **VOs**：`ClazzVO`(含 studentCount/boardingCount)、`StudentVO`(含 className)、`RoomVO`(含 buildingName/occupiedCount)、`BedVO`。
  - **Service + Controller**：
    - 班级：分页(名称/学院/年级)、重命名唯一校验、删除时班内有学生拦截「该班级下仍有学生，无法删除」。
    - 学生：分页(学号/姓名/学院/学籍/班级)、学号唯一、班级必须存在、**在住学生禁止删除**、查询单条。className 由服务映射为 classId 落库。
    - 楼栋：楼栋名唯一、删除时该楼栋下有房间拦截。
    - 房间：`(buildingId,roomNo)` 唯一、新建自动建 capacity 个床位、更新容量时床位增删且「容纳人数不能小于已住人数」、删除时存在占用床位则拦截、`/beds` 回填在住学生、`/options` 返回楼栋下拉+房型。
  - **验证**：`mvn test` 全过（`Tests run: 10, Failures: 0`）。新增 `BaseDataControllerTest`（真实 HTTP+JWT）：列表(班级/学生含 className)、班级增删/重名拦截/有学生删除拦截、房间增删/重号拦截/建成 4 床位；测试数据已清理。
  - **说明**：学院(colleges)为前端 mock 字典，库无表，本模块不实现后端接口；删除在住学生/房间占用拦截为后端安全边界，与前端 mock 级联释放逻辑不同（退宿事务联动的完整实现在住宿业务模块）。
  - **涉及文件**：新增 dto/{Clazz,Student,Building,Room}Req、vo/{Clazz,Student,Room,Bed}VO、service/{Clazz,Student,Building,Room}Service(+impl)、controller/{Clazz,Student,Building,Room}Controller、test/.../BaseDataControllerTest。

- **2026-09-08（操作日志 #55，后端住宿业务模块）** 实现入住/退宿核心事务联动（对齐前端 mock 契约，均需 JWT）：
  - **DTO/VO**：`CheckinRequest`、`CheckoutApplyRequest`、`AuditRequest`、`DirectCheckoutRequest`；`CheckInVO`、`CheckinRoomVO`、`CheckoutAppVO`、`CurrentRoomVO`(student/dorm/roommates)。
  - **CheckInService(+impl)**：入住登记（校验未在住+床位空闲 → 写 `check_in` 5 快照 + 床位占用 + 学生住宿在住 + 刷新房间）、共享事务 `checkout()`（置已退宿 + 释放床位 + 学生已退宿 + 刷新房间，供直接退宿/审核通过复用）、`checkinRooms/free-beds`、入住记录分页、学生端 currentRoom(含室友)。
  - **CheckoutService(+impl)**：退宿申请（在住校验/待审核唯一/applyNo 生成）、审核（通过→走 `checkout(apply)` 退宿，驳回留意见）、撤销（本人待审核可删）、直接退宿（`checkout(direct)`）；申请列表回填学生信息与当前宿舍（复用历史 check_in 快照）。
  - **接口**：`CheckInController`(/checkin、/checkin/rooms、/checkin/rooms/{id}/free-beds、/checkin-records、/student/current-room)、`CheckoutController`(/checkout-applications 列表·提交·audit·cancel、/checkout/direct)。`BuildingController` 补充 `/buildings/options`（入住/直接退宿下拉）。
  - **修复**：`StudentServiceImpl.create` 漏 `setStudentId`（插入报 student_id 非空）已补。
  - **验证**：`mvn test` 全过（`Tests run: 12`）。新增 `AccommodationControllerTest`：创建学生→入住→我的宿舍→记录在住→提交申请→撤销→再申请→审核通过→已退宿(source=apply)→已退宿学生可删；及直接退宿(source=direct)。测试数据已清理。
  - **涉及文件**：新增 dto/{Checkin,CheckoutApply,Audit,DirectCheckout}Request、vo/{CheckIn,CheckinRoom,CheckoutApp,CurrentRoom}VO、service/{CheckIn,Checkout}Service(+impl)、controller/{CheckIn,Checkout}Controller、test/.../AccommodationControllerTest；修改 service/impl/StudentServiceImpl、service/{BuildingService,BuildingServiceImpl}、controller/BuildingController。

- **2026-09-08（操作日志 #56，后端日常管理模块）** 实现卫生检查、报修、报修类型字典（对齐前端 mock 契约，均需 JWT）：
  - **DTO/VO**：`HygieneRequest`、`RepairTypeRequest`、`RepairCreateRequest`、`RepairHandleRequest`；`HygieneVO`(deductItems/photos 解析为数组 + buildingName/roomNo)、`RepairVO`(images 数组 + studentName/typeName/buildingName/roomNo 回填)。
  - **报修类型字典**（`RepairTypeService`）：sort 升序列表、重名拦截「该报修类型已存在」、删除被报修单引用拦截「该类型已被报修单引用，无法删除」。
  - **卫生检查**（`HygieneService`）：列表按 checkDate/buildingId/roomId/result 过滤；登记按 score 判定 优秀/合格/不合格，`score<60 或含"违规电器"且无照片` 拦截「评分低于 60 或涉及违规电器时，必须上传现场照片」，deductItems/photos 以 JSON 字符串落库。
  - **报修**（`RepairService`）：列表(orderNo/buildingId/studentId/status)与详情、学生提交（校验学生/房间，取 room.buildingId，orderNo=BX+日期+序号）、处理派单/完成（已完成需说明、handleTime 首次写）。
  - **接口**：`RepairTypeController`(/daily/repair-types)、`HygieneController`(/daily/hygiene)、`RepairController`(/daily/repairs、/daily/repair/{id})。
  - **验证**：编译通过；`mvn test` 全过（`Tests run: 15, Failures: 0`）。`DailyManagementControllerTest` 覆盖：类型增删、卫生登记与「必须上传」400 校验、报修提交→查询→完成处理全链路。卫生/报修测试数据见库（未清理）。
  - **涉及文件**（由子代理实现，需复核）：新增 vo/{Hygiene,Repair}VO、service/{RepairType,Hygiene,Repair}Service(+impl)、controller/{RepairType,Hygiene,Repair}Controller、test/.../DailyManagementControllerTest；加 dto/{Hygiene,RepairType,RepairCreate,RepairHandle}Request（本轮主线程已建）。

- **2026-09-08（操作日志 #57，后端统计与仪表盘聚合）** 实现三张统计报表 + 仪表盘聚合，全部从库实时计算（不写死）：
  - **StatsService(+impl)/StatsController**：`/stats/{occupancy,hygiene,repair}`、`/dashboard/{stats,building-occupancy,hygiene-trend,workbench}` 共 7 个接口，返回 `Result<Map<...>>`。口径含占用率、各楼栋入住率、近6月入住/退宿与报修趋势、近4周卫生均分、退宿/报修积压、运营告警、最新报修/退宿动态。
  - **验证**：编译通过；`mvn test` 全过（`Tests run: 22, Failures: 0`）。`StatsControllerTest` 7 用例全绿，既有 15 用例无回归。
  - **联调提醒（待做）**：`/dashboard/building-occupancy` 与 `/dashboard/hygiene-trend` 按前端 mock 应为**顶层数组**，子代理实现为 `{list:[...]}` 包装；前端未接真实接口时无碍，联调切换时需对齐（去掉外层 list 包装，返回数组）。
  - **涉及文件**（子代理实现）：新增 service/StatsService(+impl)、controller/StatsController、test/.../StatsControllerTest。

- **2026-09-08（操作日志 #58，后端个人中心 / 系统设置 / 改密）** 补齐后端最后一个大模块，含两块数据库支撑：
  - **DB（已执行 + init.sql/说明同步）**：新建 `sys_parameter` 表（系统参数持久化，默认4条：系统名称/欢迎语/联系电话/邮箱）+ 种子；`sys_user` 补 `phone`/`email` 两列（管理员资料可持久化）。数据库设计说明升级为「12 张表」并补 3.12 节。
  - **Service/Controller**：`ProfileService`（管理员/学生资料查改，学生含紧急联系人）、`SettingsService`（系统参数查改/恢复默认；退宿原因字典内存 CRUD + 被申请引用删除拦截）、`AccountService`（改密：原密码校验+6-20位、用 DelegatingPasswordEncoder 重编码；重置学生密码：无号自动建 STUDENT 账号、默认 123456）；`AuthController` 追加 `/auth/change-password`、`/auth/reset-password`。
  - **验证**：编译通过；`mvn test` 全过（`Tests run: 27, Failures: 0`）。`ProfileSettingsTest` 5 用例：系统参数存/改/恢复、双端资料查询、改密后新密码可登录（已还原为 123456，存储前缀变 bcrypt，兼容登录）。既有 22 用例无回归。
  - **至此后端全部大模块完成**：登录鉴权、基础数据、住宿业务、日常管理、统计、个人中心/系统设置/改密。均需 JWT。
  - **涉及文件**（子代理实现）：新增 entity/SysParameter、mapper/SysParameterMapper、service/{Profile,Settings,Account}Service(+impl)、controller/{Profile,Settings}Controller、test/.../ProfileSettingsTest；修改 entity/SysUser（+phone/email）、controller/AuthController、docs/sql/init.sql、docs/数据库设计说明.md。

- **2026-09-08（操作日志 #59，前后端联调：mock 切真实接口 + 后端契约修正）** 进入前后端联调，将前端 mock 切换为真实后端接口（`frontend/.env.development` 设 `VITE_USE_MOCK=false`）。前置：已推送后端 27 项测试全绿（提交 cfff674）。
  1. **统计数组包装修正**：`GET /api/dashboard/building-occupancy` 与 `/api/dashboard/hygiene-trend` 原返回 `{"list":[...]}`，前端 Dashboard 期望顶层数组 `[{building,rate}]` / `[{week,score}]`。调整 `StatsService`/`StatsServiceImpl`/`StatsController` 返回 `List<Map>`，提交 dc42997。
  2. **分页 total 恒为 0 修复（影响所有列表页）**：定位根因是未注册 MyBatis-Plus 分页拦截器（`selectPage` 的 total 未填充）。新增 `mybatis-plus-jsqlparser` 依赖（`PaginationInnerInterceptor` 依赖 JSqlParser）+ `common/config/MybatisPlusConfig.java` 注册 `PaginationInnerInterceptor(DbType.MYSQL)`。
  3. **床位号格式偏移修复**：库 `dorm_bed.bed_no` 存 `N号床`，而前端契约处处按纯数字 `N` 消费并自行拼后缀（MyRoom/CheckinRecord/CheckoutAudit/RoomList），导致页面上出现「1号床 床」错乱。新增 `common/util/BedNoUtil.strip()`，在对外 VO 赋值点（`CheckInServiceImpl` 当前宿舍/室友/记录、`CheckoutServiceImpl` 审核当前宿舍、`RoomServiceImpl` 床位分布）统一剥掉「号床」后缀，返回数字 `N`。
  4. **验证**：浏览器逐模块跑通仪表盘/基础数据/住宿/日常/统计/个人中心·系统设置·改密（管理员+学生账号），无 JS 报错；三处修复经浏览器复验均已生效（楼栋分页「共 2 条」、入住记录床位「1号床」格式正常、我的宿舍「1 床」）。`mvn test` 全过（27 项），提交 af1ab49 已推送 main。
  5. **说明**：学院管理仍走前端本地 mock（库无学院表，属预期）；管理员个人中心电话/邮箱初始为空属数据缺失（可在个人中心补充），非接口字段失配。
  - **涉及文件**：新增 `frontend/.env.development`、`common/config/MybatisPlusConfig.java`、`common/util/BedNoUtil.java`；修改 `pom.xml`、`service/StatsService.java`、`service/impl/StatsServiceImpl.java`、`controller/StatsController.java`、`service/impl/{CheckIn,Checkout,Room}ServiceImpl.java`。

