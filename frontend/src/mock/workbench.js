// 仪表盘 · 运营工作台聚合 mock
// 复用 daily(报修/卫生) 与 checkin(退宿申请) 的真实在存数据做统计，
// 保证工作台的待办/积压/告警/动态与各业务列表页一致。
import { ok } from './util'
import { listRepair, listHygiene } from './daily'
import { listCheckoutApps, listCheckinRecords } from './checkin'

function lastNDays(n) {
  const d = new Date()
  d.setDate(d.getDate() - n)
  const p = (x) => String(x).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`
}

// 全部业务数据（大分页拉全量，mock 阶段数据量小）
const allRepair = () => listRepair({ pageSize: 1000 }).data
const allHygiene = () => listHygiene({ pageSize: 1000 }).data
const allCheckout = () => listCheckoutApps({ pageSize: 1000 }).data
const allRecords = () => listCheckinRecords({ pageSize: 1000 }).data

export function workbench() {
  const repair = allRepair().list
  const hygiene = allHygiene().list
  const checkout = allCheckout().list
  const records = allRecords().list

  // ---- 待办 ----
  const pendingCheckout = checkout.filter((a) => a.status === '待审核').length
  const pendingRepair = repair.filter((o) => o.status === '待处理').length
  const badHygiene = hygiene.filter((h) => h.result === '不合格').length

  // ---- 积压分布 ----
  const repairBacklog = ['待处理', '处理中', '已完成'].map((s) => ({
    label: s,
    value: repair.filter((o) => o.status === s).length
  }))
  const checkoutBacklog = ['待审核', '已通过', '已驳回'].map((s) => ({
    label: s,
    value: checkout.filter((a) => a.status === s).length
  }))

  // ---- 告警 ----
  const alerts = []
  // 报修长期未处理（待处理且创建超过 3 天）
  const staleRepairs = repair.filter((o) => o.status === '待处理' && o.createTime.slice(0, 10) < lastNDays(3))
  if (staleRepairs.length) {
    alerts.push({ level: 'danger', type: 'repair', text: `${staleRepairs.length} 条报修超 3 天未处理（如 ${staleRepairs[0].roomNo}室「${staleRepairs[0].typeName}」）` })
  } else if (pendingRepair) {
    alerts.push({ level: 'warning', type: 'repair', text: `${pendingRepair} 条报修待处理，请及时派单` })
  }
  // 卫生不合格
  if (badHygiene) alerts.push({ level: 'danger', type: 'hygiene', text: `本周期存在 ${badHygiene} 间卫生不合格宿舍，需复查整改` })
  // 退宿待审核
  if (pendingCheckout) alerts.push({ level: 'warning', type: 'checkout', text: `${pendingCheckout} 条退宿申请待审核` })
  if (!alerts.length) alerts.push({ level: 'success', type: 'all', text: '各项运行良好，暂无待办告警' })

  // ---- 动态（报修 / 退宿申请）----
  const repairFeeds = repair.slice(0, 5).map((o) => ({
    id: o.id,
    type: 'repair',
    title: `${o.roomNo}室 · ${o.typeName}`,
    desc: o.description,
    status: o.status,
    time: o.createTime
  }))
  const checkoutFeeds = checkout.slice(0, 5).map((a) => ({
    id: a.id,
    type: 'checkout',
    title: `${a.studentName} · ${a.roomNo}室`,
    desc: `${a.reason}（计划 ${a.planDate}）`,
    status: a.status,
    time: a.createTime
  }))

  return ok({
    todos: {
      pendingCheckout,
      pendingRepair,
      badHygiene,
      inHouse: records.filter((r) => r.status === '在住').length,
      outHouse: records.filter((r) => r.status === '已退宿').length
    },
    backlog: { repair: repairBacklog, checkout: checkoutBacklog },
    alerts,
    feeds: { repair: repairFeeds, checkout: checkoutFeeds }
  })
}