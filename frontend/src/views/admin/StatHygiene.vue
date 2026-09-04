<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="6" v-for="c in cards" :key="c.label">
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
          <template #header><span class="card-title">近 4 周检查平均分</span></template>
          <div ref="lineRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span class="card-title">检查结果分布</span></template>
          <div ref="pieRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { DataAnalysis, Medal, Warning } from '@element-plus/icons-vue'
import { getStatHygiene } from '@/api/stat'
import * as echarts from 'echarts/core'
import { LineChart, PieChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

echarts.use([LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const cards = ref([])
const lineRef = ref(null)
const pieRef = ref(null)
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
  const d = await getStatHygiene()
  cards.value = [
    { icon: DataAnalysis, label: '检查总数', display: d.cards.total },
    { icon: Medal, label: '平均分', display: d.cards.avg },
    { icon: undefined, label: '优秀(≥90)', display: d.cards.excellent },
    { icon: Warning, label: '不合格(<60)', display: d.cards.fail }
  ]
  await new Promise((r) => setTimeout(r, 30))
  if (disposed) return
  const pr = primaryColor()
  push(lineRef.value, {
    tooltip: { trigger: 'axis', formatter: (p) => `${p[0].name}<br/>平均分 <b style="color:${pr}">${p[0].value}</b> 分` },
    grid: { left: 8, right: 16, top: 24, bottom: 8, containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: d.byWeek.map((w) => w.week), axisLine: { lineStyle: { color: '#e6e9f2' } }, axisTick: { show: false }, axisLabel: { color: '#6b7290', fontSize: 13 } },
    yAxis: { type: 'value', min: 60, max: 100, splitLine: { lineStyle: { color: '#eef0f6', type: 'dashed' } }, axisLabel: { color: '#6b7290', fontSize: 12 } },
    series: [{ type: 'line', data: d.byWeek.map((w) => w.avg), smooth: true, symbol: 'circle', symbolSize: 8, lineStyle: { width: 3, color: pr }, itemStyle: { color: pr, borderColor: '#fff', borderWidth: 2 }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(79,110,247,0.28)' }, { offset: 1, color: 'rgba(79,110,247,0)' }] } } }]
  })
  push(pieRef.value, {
    tooltip: { trigger: 'item', formatter: '{b}: {c} 次（{d}%）' },
    legend: { bottom: 0, icon: 'circle', itemWidth: 8, textStyle: { color: '#6b7290', fontSize: 12 } },
    series: [{ type: 'pie', radius: ['42%', '70%'], center: ['50%', '45%'], itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }, label: { show: false }, data: d.statusDist.map((s, i) => ({ ...s, itemStyle: { color: ['#4f6ef7', '#9aa7ff', '#ff6670'][i] } })) }]
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