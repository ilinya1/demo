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
  - 新增「五、v4 —— 数据库设计落地与终核」章节：7 条设计决策（逻辑外键/快照/冗余/床位独立表/账号体系/多图/二期预留）+ 10 张表全貌 + 四轮复核 A~F 类修复汇总。
  - 更新「当前技术状态」：数据库设计已定稿（init.sql + 数据库设计说明.md，需同步维护，尚未建库运行）；后端仍为骨架；前端仍为静态原型。
  - 重写「后续开发待办」：以 v4 为基线，拆分为 后端接口实现 / 前端联调 / 工程化与功能完善 / 交付与部署 四部分，逐步到各模块接口清单。
  - 版本修订记录追加 v4。

  **涉及文件**：`docs/项目交接文档.md`。
  **结果**：交接文档与仓库最新状态一致，可直接作为接手人员的完整开发指引。

