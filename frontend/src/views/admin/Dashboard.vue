<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in cards" :key="c.label">
        <el-card shadow="never" class="stat-card">
          <div class="stat-inner">
            <div class="stat-icon" :style="{ background: c.bg }">
              <el-icon :size="24"><component :is="c.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-num">{{ c.display }}</div>
              <div class="stat-label">{{ c.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-card shadow="never" class="section-card">
      <template #header><span class="sec-title">快捷入口</span></template>
      <div class="quick-actions">
        <el-button
          v-for="a in actions"
          :key="a.to"
          :icon="a.icon"
          @click="go(a.to)"
          :type="a.type || 'default'"
          plain
          size="large"
        >{{ a.label }}</el-button>
      </div>
    </el-card>

    <!-- 待办事项 + 楼栋入住率 -->
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never" class="section-card">
          <template #header><span class="sec-title">待办事项</span></template>
          <div class="todo-grid">
            <div
              v-for="t in todoItems"
              :key="t.label"
              class="todo-item"
              :class="{ clickable: !!t.to }"
              @click="t.to && go(t.to)"
            >
              <div class="todo-num" :style="{ color: t.color }">{{ t.count }}</div>
              <div class="todo-label">{{ t.label }}</div>
            </div>
          </div>
          <!-- 积压进度条 -->
          <div class="backlog-wrap" v-for="b in backlogSections" :key="b.label">
            <div class="backlog-header">
              <span class="backlog-label">{{ b.label }}</span>
              <span class="backlog-total">共 {{ b.total }} 条</span>
            </div>
            <div class="backlog-bars">
              <div
                v-for="s in b.items"
                :key="s.label"
                class="backlog-seg"
                :style="{ width: s.pct + '%', background: s.color }"
                :title="s.label + ': ' + s.value + ' 条'"
              >
                <span v-if="s.pct > 10" class="seg-text">{{ s.label }} {{ s.value }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="never" class="section-card chart-card">
          <template #header>
            <div class="flex-between"><span class="sec-title">各楼栋入住率</span><span class="sec-hint">实时床位占用</span></div>
          </template>
          <div ref="occupancyRef" class="chart-sm"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 运营告警 -->
    <el-card shadow="never" class="section-card" v-if="alerts.length">
      <template #header><span class="sec-title">运营告警</span></template>
      <div class="alert-list">
        <div v-for="(a, i) in alerts" :key="i" :class="'alert-item alert-' + a.level">
          <el-icon v-if="a.level === 'danger'" :size="18" color="#f56c6c"><WarningFilled /></el-icon>
          <el-icon v-else-if="a.level === 'warning'" :size="18" color="#e6a23c"><WarningFilled /></el-icon>
          <el-icon v-else :size="18" color="#67c23a"><CircleCheckFilled /></el-icon>
          <span>{{ a.text }}</span>
        </div>
      </div>
    </el-card>

    <!-- 最新动态 -->
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" class="section-card">
          <template #header><span class="sec-title">最新报修动态</span></template>
          <div class="feed-list">
            <div v-for="f in feeds.repair" :key="f.id" class="feed-item">
              <div class="feed-head">
                <span class="feed-title">{{ f.title }}</span>
                <el-tag :type="repairTag(f.status)" size="small" effect="plain">{{ f.status }}</el-tag>
              </div>
              <div class="feed-desc">{{ f.desc }}</div>
              <div class="feed-time">{{ f.time }}</div>
            </div>
            <el-empty v-if="!feeds.repair.length" description="暂无报修记录" :image-size="60" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="section-card">
          <template #header><span class="sec-title">最新退宿申请</span></template>
          <div class="feed-list">
            <div v-for="f in feeds.checkout" :key="f.id" class="feed-item">
              <div class="feed-head">
                <span class="feed-title">{{ f.title }}</span>
                <el-tag :type="checkoutTag(f.status)" size="small" effect="plain">{{ f.status }}</el-tag>
              </div>
              <div class="feed-desc">{{ f.desc }}</div>
              <div class="feed-time">{{ f.time }}</div>
            </div>
            <el-empty v-if="!feeds.checkout.length" description="暂无退宿申请" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 卫生趋势图（原有） -->
    <el-row :gutter="16" class="mt">
      <el-col :span="24">
        <el-card shadow="never" class="section-card chart-card">
          <template #header>
            <div class="flex-between"><span class="sec-title">最近卫生检查 · 平均分</span><span class="sec-hint">近 4 周趋势</span></div>
          </template>
          <div ref="trendRef" class="chart-lg"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getDashboardStats, getBuildingOccupancy, getHygieneTrend, getWorkbench } from '@/api/dashboard'
import { User, OfficeBuilding, House, TrendCharts, Plus, Edit, Finished, WarningFilled, CircleCheckFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([BarChart, LineChart, GridComponent, TooltipComponent, CanvasRenderer])

const router = useRouter()

// 统计卡片（含配色）
const cards = ref([])
const cardMeta = [
  { icon: User, label: '在校学生总数', bg: '#eef1ff' },
  { icon: OfficeBuilding, label: '宿舍楼栋数', bg: '#f0f9eb' },
  { icon: House, label: '房间总数', bg: '#fef0f0' },
  { icon: TrendCharts, label: '整体入住率', bg: '#fdf6ec' }
]

// 快捷入口
const actions = [
  { label: '入住登记', to: '/admin/checkin', icon: Plus, type: 'primary' },
  { label: '退宿处理', to: '/admin/checkout-audit', icon: Edit, type: 'warning' },
  { label: '卫生检查', to: '/admin/hygiene-list', icon: Finished, type: 'success' },
  { label: '报修管理', to: '/admin/repair-list', icon: Edit, type: 'info' }
]

// 待办
const todoItems = ref([])
const backlogSections = ref([])
const alerts = ref([])
const feeds = ref({ repair: [], checkout: [] })

// 图表
const buildings = ref([])
const trend = ref([])
const occupancyRef = ref(null)
const trendRef = ref(null)
let charts = []
let disposed = false

function readVar(name) {
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim()
}

function go(path) {
  router.push(path)
}

function repairTag(s) {
  return s === '待处理' ? 'danger' : s === '处理中' ? 'warning' : 'success'
}
function checkoutTag(s) {
  return s === '待审核' ? 'warning' : s === '已通过' ? 'success' : 'danger'
}

function initOccupancy() {
  if (!occupancyRef.value || !buildings.value.length) return
  const primary = readVar('--d-primary') || '#4f6ef7'
  const c = echarts.init(occupancyRef.value)
  c.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (p) => `${p[0].name}<br/><b style="color:${primary}">${p[0].value}%</b> 入住`
    },
    grid: { left: 8, right: 40, top: 8, bottom: 8, containLabel: true },
    xAxis: { type: 'value', max: 100, splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } }, axisLabel: { show: false } },
    yAxis: { type: 'category', data: buildings.value.map((b) => b.building), axisLine: { show: false }, axisTick: { show: false }, axisLabel: { color: '#6b7290', fontSize: 12 } },
    series: [{
      type: 'bar',
      data: buildings.value.map((b) => +(b.rate * 100).toFixed(1)),
      barWidth: 12,
      itemStyle: { borderRadius: [0, 6, 6, 0], color: { type: 'linear', x: 0, y: 0, x2: 1, y2: 0, colorStops: [{ offset: 0, color: primary }, { offset: 1, color: '#7a8cff' }] } },
      label: { show: true, position: 'right', color: '#1c2340', fontWeight: 700, fontSize: 11, formatter: '{c}%' }
    }]
  })
  charts.push(c)
}

function initTrend() {
  if (!trendRef.value || !trend.value.length) return
  const primary = readVar('--d-primary') || '#4f6ef7'
  const c = echarts.init(trendRef.value)
  c.setOption({
    tooltip: { trigger: 'axis', formatter: (p) => `${p[0].name}<br/>平均分 <b style="color:${primary}">${p[0].value}</b> 分` },
    grid: { left: 8, right: 16, top: 24, bottom: 8, containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: trend.value.map((t) => t.week), axisLine: { lineStyle: { color: '#e6e9f2' } }, axisTick: { show: false }, axisLabel: { color: '#6b7290', fontSize: 13 } },
    yAxis: { type: 'value', min: 60, max: 100, splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } }, axisLabel: { color: '#6b7290', fontSize: 12 } },
    series: [{
      type: 'line', data: trend.value.map((t) => t.score), smooth: true, symbol: 'circle', symbolSize: 8,
      lineStyle: { width: 3, color: primary },
      itemStyle: { color: primary, borderColor: '#fff', borderWidth: 2 },
      areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 0, colorStops: [{ offset: 0, color: 'rgba(79,110,247,0.28)' }, { offset: 1, color: 'rgba(79,110,247,0)' }] } }
    }]
  })
  charts.push(c)
}

function onResize() {
  charts.forEach((c) => c.resize())
}

onMounted(async () => {
  const [s, wb, bld, tr] = await Promise.all([
    getDashboardStats(),
    getWorkbench(),
    getBuildingOccupancy(),
    getHygieneTrend()
  ])
  cards.value = cardMeta.map((m, i) => {
    const vals = [s.studentCount, s.buildingCount, s.roomCount, (s.occupancyRate * 100).toFixed(1) + '%']
    return { ...m, display: vals[i] }
  })

  // 待办
  const t = wb.todos
  todoItems.value = [
    { label: '待审核退宿', count: t.pendingCheckout, color: '#e6a23c', to: '/admin/checkout-audit' },
    { label: '待处理报修', count: t.pendingRepair, color: '#f56c6c', to: '/admin/repair-list' },
    { label: '当前在住', count: t.inHouse, color: '#67c23a' },
    { label: '已退宿', count: t.outHouse, color: '#909399' }
  ]

  // 积压进度条
  const backlogBarColors = ['#f56c6c', '#e6a23c', '#67c23a']
  backlogSections.value = [
    {
      label: '报修状态分布',
      total: wb.backlog.repair.reduce((a, b) => a + b.value, 0),
      items: wb.backlog.repair.map((x, i) => ({ ...x, color: backlogBarColors[i] || '#909399', pct: 0 }))
    },
    {
      label: '退宿申请分布',
      total: wb.backlog.checkout.reduce((a, b) => a + b.value, 0),
      items: wb.backlog.checkout.map((x, i) => ({ ...x, color: backlogBarColors[i] || '#909399', pct: 0 }))
    }
  ]
  // 计算百分比
  backlogSections.value.forEach((s) => {
    s.items.forEach((x) => {
      x.pct = s.total ? Math.round((x.value / s.total) * 100) : 0
    })
  })

  alerts.value = wb.alerts
  feeds.value = wb.feeds

  buildings.value = bld
  trend.value = tr
  await new Promise((r) => setTimeout(r, 30))
  if (disposed) return
  initOccupancy()
  initTrend()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  disposed = true
  window.removeEventListener('resize', onResize)
  charts.forEach((c) => c.dispose())
  charts = []
})
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: 16px; }

/* 统计卡片 */
.stat-card { border-radius: var(--d-radius); border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.stat-card :deep(.el-card__body) { padding: 20px; }
.stat-inner { display: flex; align-items: center; gap: 14px; }
.stat-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: var(--d-primary); }
.stat-num { font-size: 26px; font-weight: 800; color: var(--d-ink); line-height: 1.1; }
.stat-label { font-size: 13px; color: var(--d-muted); margin-top: 4px; }

/* 分区卡片 */
.section-card { border-radius: var(--d-radius); border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.section-card :deep(.el-card__body) { padding: 16px 18px; }
.section-card :deep(.el-card__header) { border-bottom: 1px solid var(--d-border); padding: 12px 18px; }
.sec-title { font-size: 15px; font-weight: 800; color: var(--d-ink); }
.sec-hint { font-size: 12px; color: var(--d-muted); }
.flex-between { display: flex; align-items: baseline; justify-content: space-between; }

/* 快捷入口 */
.quick-actions { display: flex; gap: 12px; flex-wrap: wrap; }

/* 待办网格 */
.todo-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px; }
.todo-item { background: #f8f9fc; border-radius: 10px; padding: 14px 16px; text-align: center; cursor: default; transition: background .15s; }
.todo-item.clickable { cursor: pointer; }
.todo-item.clickable:hover { background: #eef1f7; }
.todo-num { font-size: 32px; font-weight: 800; line-height: 1; }
.todo-label { font-size: 13px; color: var(--d-muted); margin-top: 6px; }

/* 积压进度条 */
.backlog-wrap { margin-bottom: 14px; }
.backlog-wrap:last-child { margin-bottom: 0; }
.backlog-header { display: flex; justify-content: space-between; margin-bottom: 6px; }
.backlog-label { font-size: 13px; font-weight: 600; color: var(--d-ink); }
.backlog-total { font-size: 12px; color: var(--d-muted); }
.backlog-bars { display: flex; height: 24px; border-radius: 12px; overflow: hidden; background: #f0f2f6; }
.backlog-seg { display: flex; align-items: center; justify-content: center; transition: width .3s; min-width: 0; }
.seg-text { font-size: 11px; color: #fff; font-weight: 600; white-space: nowrap; }

/* 告警 */
.alert-list { display: flex; flex-direction: column; gap: 8px; }
.alert-item { display: flex; align-items: center; gap: 8px; padding: 8px 12px; border-radius: 8px; font-size: 13px; }
.alert-danger { background: #fef0f0; color: #f56c6c; }
.alert-warning { background: #fdf6ec; color: #e6a23c; }
.alert-success { background: #f0f9eb; color: #67c23a; }

/* 动态列表 */
.feed-list { display: flex; flex-direction: column; gap: 2px; }
.feed-item { padding: 10px 0; border-bottom: 1px solid var(--d-border); }
.feed-item:last-child { border-bottom: none; }
.feed-head { display: flex; align-items: center; justify-content: space-between; }
.feed-title { font-size: 13px; font-weight: 600; color: var(--d-ink); }
.feed-desc { font-size: 12px; color: var(--d-muted); margin-top: 3px; }
.feed-time { font-size: 11px; color: #b0b6c8; margin-top: 2px; }

/* 图表 */
.chart-sm { height: 220px; width: 100%; }
.chart-lg { height: 260px; width: 100%; }
.mt { margin-top: 0; }
</style>