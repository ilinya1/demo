<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="c in cards" :key="c.label">
        <el-card shadow="never" class="stat">
          <div class="card">
            <div class="icon"><el-icon :size="24"><component :is="c.icon" /></el-icon></div>
            <div>
              <div class="num">{{ c.display }}</div>
              <div class="label">{{ c.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="mt">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="card-title">各类型报修数量</span></template>
          <div ref="barRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="card-title">近 6 月提交 / 完成趋势</span></template>
          <div ref="lineRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { Files, CircleCheck, TrendCharts } from '@element-plus/icons-vue'
import { getStatRepair } from '@/api/stat'
import * as echarts from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([BarChart, LineChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const cards = ref([])
const barRef = ref(null)
const lineRef = ref(null)
let charts = []
let disposed = false

function readVar(name) {
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim()
}
function primaryColor() {
  return readVar('--d-primary') || '#4f6ef7'
}
function push(el, option) {
  const c = echarts.init(el)
  c.setOption(option)
  charts.push(c)
}

onMounted(async () => {
  const d = await getStatRepair()
  cards.value = [
    { icon: Files, label: '报修总数', display: d.cards.total },
    { icon: CircleCheck, label: '已完成', display: d.cards.done },
    { icon: TrendCharts, label: '完成率', display: (d.cards.rate * 100).toFixed(1) + '%' }
  ]
  await new Promise((r) => setTimeout(r, 30))
  if (disposed) return
  const pr = primaryColor()
  push(barRef.value, {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: (p) => `${p[0].name}<br/><b style="color:${pr}">${p[0].value}</b> 件` },
    grid: { left: 8, right: 24, top: 24, bottom: 8, containLabel: true },
    xAxis: { type: 'category', data: d.byType.map((t) => t.type), axisLine: { lineStyle: { color: '#e6e9f2' } }, axisTick: { show: false }, axisLabel: { color: '#6b7290', fontSize: 13 } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } }, axisLabel: { color: '#6b7290', fontSize: 12 } },
    series: [{ type: 'bar', data: d.byType.map((t) => t.count), barWidth: 34, itemStyle: { borderRadius: [7, 7, 0, 0], color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: pr }, { offset: 1, color: '#9aa7ff' }] } }, label: { show: true, position: 'top', color: '#1c2340', fontWeight: 700 } }]
  })
  push(lineRef.value, {
    tooltip: { trigger: 'axis' },
    legend: { top: 0, right: 0, icon: 'circle', itemWidth: 8, textStyle: { color: '#6b7290', fontSize: 12 } },
    grid: { left: 8, right: 16, top: 30, bottom: 8, containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: d.trend.map((t) => t.month), axisLine: { lineStyle: { color: '#e6e9f2' } }, axisTick: { show: false }, axisLabel: { color: '#6b7290', fontSize: 12 } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } }, axisLabel: { color: '#6b7290', fontSize: 12 } },
    series: [
      { name: '提交', type: 'line', data: d.trend.map((t) => t.submit), smooth: true, symbol: 'circle', symbolSize: 7, lineStyle: { width: 3, color: pr }, itemStyle: { color: pr } },
      { name: '完成', type: 'line', data: d.trend.map((t) => t.done), smooth: true, symbol: 'circle', symbolSize: 7, lineStyle: { width: 3, color: '#2fc48d' }, itemStyle: { color: '#2fc48d' } }
    ]
  })
  window.addEventListener('resize', onResize)
})

function onResize() { charts.forEach((c) => c.resize()) }
onBeforeUnmount(() => {
  disposed = true
  window.removeEventListener('resize', onResize)
  charts.forEach((c) => c.dispose())
  charts = []
})
</script>

<style scoped>
.card { display: flex; align-items: center; gap: 14px; }
.icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; background: var(--d-primary-soft); color: var(--d-primary); }
.num { font-size: 26px; font-weight: 800; color: var(--d-ink); line-height: 1.1; }
.label { color: var(--d-muted); font-size: 13px; margin-top: 4px; }
.mt { margin-top: 16px; }
.stat, .chart-card { border: 1px solid var(--d-border); box-shadow: var(--d-shadow); }
.stat :deep(.el-card__body) { padding: 18px 20px; }
.chart-card :deep(.el-card__body) { padding: 12px; }
.card-title { font-size: 15px; font-weight: 800; color: var(--d-ink); }
.chart { height: 280px; width: 100%; }
</style>