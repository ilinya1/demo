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

