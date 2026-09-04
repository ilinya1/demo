<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in cards" :key="c.label">
        <el-card shadow="never" class="stat">
          <div class="card">
            <div class="icon"><el-icon :size="26"><component :is="c.icon" /></el-icon></div>
            <div class="info">
              <div class="num">{{ c.display }}</div>
              <div class="label">{{ c.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt">
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span class="card-title">各楼栋入住率</span>
              <span class="card-hint">实时床位占用</span>
            </div>
          </template>
          <div ref="occupancyRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span class="card-title">最近卫生检查 · 平均分</span>
              <span class="card-hint">近 4 周趋势</span>
            </div>
          </template>
          <div ref="trendRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { getDashboardStats, getBuildingOccupancy, getHygieneTrend } from '@/api/dashboard'
import { User, OfficeBuilding, House, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([BarChart, LineChart, GridComponent, TooltipComponent, CanvasRenderer])

const cards = ref([])
const buildings = ref([])
const trend = ref([])
const occupancyRef = ref(null)
const trendRef = ref(null)

let charts = []
let disposed = false

function readVar(name) {
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim()
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
    grid: { left: 8, right: 48, top: 8, bottom: 8, containLabel: true },
    xAxis: {
      type: 'value',
      max: 100,
      splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } },
      axisLabel: { show: false }
    },
    yAxis: {
      type: 'category',
      data: buildings.value.map((b) => b.building),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#6b7290', fontSize: 13 }
    },
    series: [
      {
        type: 'bar',
        data: buildings.value.map((b) => +(b.rate * 100).toFixed(1)),
        barWidth: 14,
        itemStyle: {
          borderRadius: [0, 7, 7, 0],
          color: {
            type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: primary },
              { offset: 1, color: '#7a8cff' }
            ]
          }
        },
        label: {
          show: true, position: 'right', color: '#1c2340',
          fontWeight: 700, fontSize: 12,
          formatter: '{c}%'
        }
      }
    ]
  })
  charts.push(c)
}

function initTrend() {
  if (!trendRef.value || !trend.value.length) return
  const primary = readVar('--d-primary') || '#4f6ef7'
  const c = echarts.init(trendRef.value)
  c.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (p) => `${p[0].name}<br/>平均分 <b style="color:${primary}">${p[0].value}</b> 分`
    },
    grid: { left: 8, right: 16, top: 24, bottom: 8, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: trend.value.map((t) => t.week),
      axisLine: { lineStyle: { color: '#e6e9f2' } },
      axisTick: { show: false },
      axisLabel: { color: '#6b7290', fontSize: 13 }
    },
    yAxis: {
      type: 'value',
      min: 60,
      max: 100,
      splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } },
      axisLabel: { color: '#6b7290', fontSize: 12 }
    },
    series: [
      {
        type: 'line',
        data: trend.value.map((t) => t.score),
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3, color: primary },
        itemStyle: { color: primary, borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(79,110,247,0.28)' },
              { offset: 1, color: 'rgba(79,110,247,0)' }
            ]
          }
        }
      }
    ]
  })
  charts.push(c)
}

function onResize() {
  charts.forEach((c) => c.resize())
}

onMounted(async () => {
  const s = await getDashboardStats()
  cards.value = [
    { icon: User, label: '在校学生总数', display: s.studentCount },
    { icon: OfficeBuilding, label: '宿舍楼栋数', display: s.buildingCount },
    { icon: House, label: '房间总数', display: s.roomCount },
    { icon: TrendCharts, label: '整体入住率', display: (s.occupancyRate * 100).toFixed(1) + '%' }
  ]
  buildings.value = await getBuildingOccupancy()
  trend.value = await getHygieneTrend()
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
.card { display: flex; align-items: center; gap: 14px; }
.icon {
  width: 52px; height: 52px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  background: var(--d-primary-soft); color: var(--d-primary);
}
.num { font-size: 28px; font-weight: 800; color: var(--d-ink); line-height: 1.1; }
.label { color: var(--d-muted); font-size: 13px; margin-top: 4px; }
.mt { margin-top: 16px; }
.stat, .chart-card { border-radius: var(--d-radius); border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.stat :deep(.el-card__body) { padding: 20px; }
.chart-card :deep(.el-card__header) { border-bottom: 1px solid var(--d-border); padding: 14px 18px; }
.chart-card :deep(.el-card__body) { padding: 12px; }
.chart-header { display: flex; align-items: baseline; justify-content: space-between; }
.card-title { font-size: 15px; font-weight: 800; color: var(--d-ink); }
.card-hint { font-size: 12px; color: var(--d-muted); }
.chart { height: 280px; width: 100%; }
</style>