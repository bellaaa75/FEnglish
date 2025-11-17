<template>
  <div class="study-record-page">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card shadow="hover" class="stat-card left-panel">
          <div class="left-top">
            <div class="user-id">用户ID: <span class="uid">{{ userId }}</span></div>
            <div class="metric">
              <div class="label">本月学习单词数：</div>
              <div class="value">{{ stats.monthlyWordCount }}</div>
            </div>
            <div class="metric">
              <div class="label">本月打卡天数：</div>
              <div class="value">{{ stats.monthlyStudyDays }}</div>
            </div>
          </div>

          <div class="calendar-container">
            <div class="dow">日</div><div class="dow">一</div><div class="dow">二</div><div class="dow">三</div><div class="dow">四</div><div class="dow">五</div><div class="dow">六</div>
            <div class="calendar-grid">
              <template v-for="(cell, idx) in calendarCells" :key="idx">
                <div class="cell" :class="{empty: !cell.date}" :style="cellStyle(cell.count)">
                  <div class="cell-count" v-if="cell.date">{{ cell.count === 0 ? 0 : cell.count }}</div>
                </div>
              </template>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card shadow="hover" class="trend-card">
          <div class="trend-header">累计学习词数（仅展示近7天）</div>
          <div ref="lineRef" class="line-chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import request from '@/utils/request'
import * as echarts from 'echarts'
import { format, subDays } from 'date-fns'

export default {
  name: 'StudyRecordView',
  setup() {
    const lineRef = ref(null)
    let lineChart = null
    const userId = ref(getUserId())
    const calendarCells = ref([])
    const maxCount = ref(0)

    const stats = ref({
      monthlyWordCount: 0,
      monthlyStudyDays: 0,
      dailyWordCounts: []
    })

    function getUserId() {
      const raw = localStorage.getItem('userId') || ''
      try {
        // 兼容被 JSON.stringify 存储的情况
        if ((raw.startsWith('"') && raw.endsWith('"')) || (raw.startsWith("'") && raw.endsWith("'"))) {
          return raw.slice(1, -1)
        }
        return raw
      } catch (e) {
        return raw
      }
    }

    const parseDailyCounts = (list) => {
      // Expect list elements like { "studyDate": "2025-11-01", "wordCount": 20 }
      const map = new Map()
      if (!Array.isArray(list)) return []
      for (const item of list) {
        if (!item) continue
        try {
          // handle common keys from repository: studyDate, wordCount
          if (item.studyDate !== undefined && item.wordCount !== undefined) {
            const d = String(item.studyDate)
            const v = Number(item.wordCount)
            map.set(d, (map.get(d) || 0) + (Number.isFinite(v) ? v : 0))
            continue
          }
          // fallback: handle { date/count } or { '2025-11-01': 2 }
          if (item.date !== undefined && item.count !== undefined) {
            const d = String(item.date)
            const v = Number(item.count)
            map.set(d, (map.get(d) || 0) + (Number.isFinite(v) ? v : 0))
            continue
          }
          if (item.day !== undefined && item.count !== undefined) {
            const d = String(item.day)
            const v = Number(item.count)
            map.set(d, (map.get(d) || 0) + (Number.isFinite(v) ? v : 0))
            continue
          }
          // generic fallback: find a date-like key and a numeric value
          const keys = Object.keys(item)
          if (keys.length === 2) {
            const k0 = keys[0], k1 = keys[1]
            const a = item[k0], b = item[k1]
            // choose the key whose value looks like a date
            if (/^\d{4}-\d{2}-\d{2}/.test(String(a))) {
              const d = String(a)
              const v = Number(b)
              map.set(d, (map.get(d) || 0) + (Number.isFinite(v) ? v : 0))
              continue
            }
            if (/^\d{4}-\d{2}-\d{2}/.test(String(b))) {
              const d = String(b)
              const v = Number(a)
              map.set(d, (map.get(d) || 0) + (Number.isFinite(v) ? v : 0))
              continue
            }
          }
        } catch (e) {
          console.warn('parseDailyCounts error', e)
        }
      }
      return Array.from(map.entries())
    }

    const fetchStats = async () => {
      const userId = getUserId()
      try {
        const res = await request.get('/api/study-records/statistics', { params: { userId } })
        // request interceptor returns either an object with `data` or the payload directly
        const payload = (res && res.data !== undefined) ? res.data : res
        if (!payload) return
        stats.value.monthlyWordCount = payload.monthlyWordCount || payload.monthlyWordCount === 0 ? payload.monthlyWordCount : 0
        stats.value.monthlyStudyDays = payload.monthlyStudyDays || payload.monthlyStudyDays === 0 ? payload.monthlyStudyDays : 0
        stats.value.dailyWordCounts = payload.dailyWordCounts || []

        const calendarData = parseDailyCounts(stats.value.dailyWordCounts)
        // calendarData is Array<[date, count]>
        buildCalendarCellsFromArray(calendarData)
        renderLine(calendarData)
      } catch (err) {
        console.error('获取学习统计出错', err)
      }
    }

    // Build calendar cells for the current month from parsed calendarData
    const buildCalendarCellsFromArray = (arr) => {
      const map = new Map(arr)
      // compute maxCount for coloring
      let m = 0
      for (const [, v] of arr) if (Number.isFinite(Number(v))) m = Math.max(m, Number(v))
      maxCount.value = m

      const today = new Date()
      const year = today.getFullYear()
      const month = today.getMonth() // 0-based
      const first = new Date(year, month, 1)
      const startWeek = first.getDay() // 0-6, Sunday=0
      const daysInMonth = new Date(year, month + 1, 0).getDate()

      const cells = []
      const total = 42
      for (let i = 0; i < total; i++) {
        const dayIndex = i - startWeek + 1
        if (dayIndex >= 1 && dayIndex <= daysInMonth) {
          const d = new Date(year, month, dayIndex)
          const dStr = format(d, 'yyyy-MM-dd')
          const count = map.get(dStr) || 0
          cells.push({ date: dStr, count })
        } else {
          cells.push({ date: null, count: 0 })
        }
      }
      calendarCells.value = cells
    }

    const renderLine = (calendarData) => {
      if (!lineRef.value) return
      if (!lineChart) lineChart = echarts.init(lineRef.value)

      // build a map from calendarData
      const map = new Map(calendarData.map(d => [d[0], d[1]]))
      const days = []
      const vals = []
      for (let i = 6; i >= 0; i--) {
        const day = format(subDays(new Date(), i), 'yyyy-MM-dd')
        days.push(day)
        vals.push(map.get(day) || 0)
      }

      const option = {
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: days },
        yAxis: { type: 'value' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        series: [{ data: vals, type: 'line', smooth: true, areaStyle: { color: 'rgba(43,156,244,0.15)' }, lineStyle: { color: '#2b9cf4' }, itemStyle: { color: '#2b9cf4' } }]
      }

      try {
        lineChart.setOption(option)
      } catch (e) {
        console.warn('line chart setOption error', e)
      }
    }

    const handleResize = () => {
      lineChart?.resize()
    }

    onMounted(() => {
      // hide top bar if present (user requested no top bar in this view)
      const top = document.querySelector('.top')
      if (top) top.style.display = 'none'

      fetchStats()
      window.addEventListener('resize', handleResize)
    })

    onBeforeUnmount(() => {
      window.removeEventListener('resize', handleResize)
      try { lineChart && lineChart.dispose() } catch (e) { console.warn('dispose line error', e) }
      // restore top bar
      const top = document.querySelector('.top')
      if (top) top.style.display = ''
    })
    const cellStyle = (count) => {
      if (!count) return { background: '#fff' }
      const m = maxCount.value || 1
      const ratio = Math.min(1, count / m)
      // interpolate between light blue and dark blue
      const alpha = 0.2 + ratio * 0.65
      return { background: `rgba(43,156,244,${alpha})`, color: '#fff' }
    }

    return {
      lineRef,
      lineChart,
      stats,
      userId,
      calendarCells,
      cellStyle
    }
  }
}
</script>

<style scoped>
.study-record-page { padding: 16px }
.left-panel { height: 520px }
.left-top { padding: 8px 12px }
.user-id { font-size: 16px; font-weight: 600; margin-bottom: 8px }
.user-id .uid { background: #eee; padding: 2px 6px; border-radius: 4px }
.metric { display:flex; align-items:baseline; gap:8px; margin:6px 0 }
.metric .label { color:#333 }
.metric .value { font-size: 22px; font-weight:700; color:#111 }
.calendar-container { margin: 12px; padding: 10px; border: 2px solid #e6f2ff; }
.calendar-container .dow { display:inline-block; width: calc((100% - 14px)/7); text-align:center; color:#666; padding:6px 0 }
.calendar-grid { display:grid; grid-template-columns: repeat(7, 1fr); gap:4px; margin-top:8px }
.cell { min-height: 48px; border:1px solid #e6eef9; display:flex; align-items:center; justify-content:center }
.cell.empty { background: #fafafa }
.cell-count { font-weight:600 }
.trend-card { height: 520px }
.trend-header { font-weight: 600; margin-bottom: 8px }
.line-chart { width: 100%; height: 440px }
</style>