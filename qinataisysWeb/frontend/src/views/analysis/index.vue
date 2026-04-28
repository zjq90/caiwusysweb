<template>
  <div class="analysis">
    <div class="card">
      <div class="card-header">
        <span class="card-title">月度对比分析</span>
        <div class="header-actions">
          <el-date-picker
            v-model="currentMonth"
            type="month"
            value-format="YYYY-MM"
            placeholder="选择对比月份"
            @change="loadData"
          />
        </div>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="compare-card">
            <div class="compare-label">本月收入</div>
            <div class="compare-value">¥{{ comparison.current?.income || 0 }}</div>
            <div class="compare-change" :class="comparison.incomeChange?.trend">
              <el-icon v-if="comparison.incomeChange?.trend === 'up'"><TrendCharts /></el-icon>
              <el-icon v-else><TrendCharts /></el-icon>
              {{ comparison.incomeChange?.diff || 0 }} ({{ comparison.incomeChange?.rate || 0 }}%)
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="compare-card">
            <div class="compare-label">上月收入</div>
            <div class="compare-value">¥{{ comparison.last?.income || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="compare-card expense">
            <div class="compare-label">本月支出</div>
            <div class="compare-value">¥{{ comparison.current?.expense || 0 }}</div>
            <div class="compare-change" :class="comparison.expenseChange?.trend">
              <el-icon v-if="comparison.expenseChange?.trend === 'up'"><TrendCharts /></el-icon>
              <el-icon v-else><TrendCharts /></el-icon>
              {{ comparison.expenseChange?.diff || 0 }} ({{ comparison.expenseChange?.rate || 0 }}%)
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="compare-card expense">
            <div class="compare-label">上月支出</div>
            <div class="compare-value">¥{{ comparison.last?.expense || 0 }}</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">支出类别对比</span>
          </div>
          <div ref="categoryCompareChart" class="chart-container-large"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">收支趋势对比</span>
          </div>
          <div ref="trendCompareChart" class="chart-container-large"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <div class="card">
          <div class="card-header">
            <span class="card-title">支出雷达图</span>
          </div>
          <div ref="radarChart" class="chart-container-radar"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { recordApi } from '@/api/record'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const currentMonth = ref(dayjs().format('YYYY-MM'))
const categoryCompareChart = ref<HTMLElement>()
const trendCompareChart = ref<HTMLElement>()
const radarChart = ref<HTMLElement>()

const comparison = reactive({
  current: { income: 0, expense: 0 },
  last: { income: 0, expense: 0 },
  incomeChange: { diff: 0, rate: 0, trend: 'up' },
  expenseChange: { diff: 0, rate: 0, trend: 'up' },
  currentCategoryExpense: [],
  lastCategoryExpense: []
})

const loadData = async () => {
  try {
    const lastMonth = dayjs(currentMonth.value).subtract(1, 'month').format('YYYY-MM')
    const res = await recordApi.getMonthComparison(currentMonth.value, lastMonth)
    
    if (res.code === 200) {
      comparison.current = res.data.current
      comparison.last = res.data.last
      comparison.incomeChange = res.data.incomeChange
      comparison.expenseChange = res.data.expenseChange
      comparison.currentCategoryExpense = res.data.currentCategoryExpense || []
      comparison.lastCategoryExpense = res.data.lastCategoryExpense || []

      await nextTick()
      initCategoryCompareChart()
      initTrendCompareChart()
      initRadarChart()
    }
  } catch (error) {
    console.error('加载分析数据失败:', error)
  }
}

const initCategoryCompareChart = () => {
  if (!categoryCompareChart.value) return

  const currentData = comparison.currentCategoryExpense
  const lastData = comparison.lastCategoryExpense

  const categories = [...new Set([...currentData.map((d: any) => d.name), ...lastData.map((d: any) => d.name)])]
  
  const currentValues = categories.map(name => {
    const item = currentData.find((d: any) => d.name === name)
    return item ? Number(item.amount || 0) : 0
  })
  
  const lastValues = categories.map(name => {
    const item = lastData.find((d: any) => d.name === name)
    return item ? Number(item.amount || 0) : 0
  })

  const chart = echarts.init(categoryCompareChart.value)
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['本月', '上月']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '本月',
        type: 'bar',
        data: currentValues,
        itemStyle: { color: '#409eff' }
      },
      {
        name: '上月',
        type: 'bar',
        data: lastValues,
        itemStyle: { color: '#909399' }
      }
    ]
  })
}

const initTrendCompareChart = () => {
  if (!trendCompareChart.value) return

  const chart = echarts.init(trendCompareChart.value)
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center'
    },
    series: [
      {
        name: '本月收入分布',
        type: 'pie',
        radius: ['0%', '35%'],
        center: ['25%', '50%'],
        label: {
          position: 'inner',
          fontSize: 12
        },
        data: comparison.currentCategoryExpense.slice(0, 5).map((d: any) => ({
          name: d.name,
          value: Number(d.amount || 0)
        }))
      },
      {
        name: '上月收入分布',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['25%', '50%'],
        label: {
          show: false
        },
        data: comparison.lastCategoryExpense.slice(0, 5).map((d: any) => ({
          name: d.name,
          value: Number(d.amount || 0)
        }))
      },
      {
        name: '本月支出分布',
        type: 'pie',
        radius: ['0%', '35%'],
        center: ['75%', '50%'],
        label: {
          position: 'inner',
          fontSize: 12
        },
        data: comparison.currentCategoryExpense.slice(0, 5).map((d: any) => ({
          name: d.name,
          value: Number(d.amount || 0)
        }))
      }
    ]
  })
}

const initRadarChart = () => {
  if (!radarChart.value) return

  const currentData = comparison.currentCategoryExpense
  const lastData = comparison.lastCategoryExpense

  const categories = [...new Set([...currentData.map((d: any) => d.name), ...lastData.map((d: any) => d.name)])]
  
  const maxValue = Math.max(
    ...currentData.map((d: any) => Number(d.amount || 0)),
    ...lastData.map((d: any) => Number(d.amount || 0))
  )

  const chart = echarts.init(radarChart.value)
  chart.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      data: ['本月', '上月']
    },
    radar: {
      indicator: categories.map(name => ({
        name,
        max: maxValue * 1.2
      })),
      splitArea: {
        areaStyle: {
          color: ['rgba(64, 158, 255, 0.1)', 'rgba(64, 158, 255, 0.2)'],
          shadowColor: 'rgba(0, 0, 0, 0.1)',
          shadowBlur: 10
        }
      }
    },
    series: [
      {
        name: '支出对比',
        type: 'radar',
        data: [
          {
            value: categories.map(name => {
              const item = currentData.find((d: any) => d.name === name)
              return item ? Number(item.amount || 0) : 0
            }),
            name: '本月',
            areaStyle: { color: 'rgba(64, 158, 255, 0.3)' },
            lineStyle: { color: '#409eff' }
          },
          {
            value: categories.map(name => {
              const item = lastData.find((d: any) => d.name === name)
              return item ? Number(item.amount || 0) : 0
            }),
            name: '上月',
            areaStyle: { color: 'rgba(144, 147, 153, 0.3)' },
            lineStyle: { color: '#909399' }
          }
        ]
      }
    ]
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.analysis {
  .header-actions {
    display: flex;
    align-items: center;
  }

  .compare-card {
    padding: 20px;
    border-radius: 8px;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
    border-left: 4px solid #409eff;

    &.expense {
      background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
      border-left-color: #f56c6c;
    }

    .compare-label {
      font-size: 14px;
      color: #606266;
    }

    .compare-value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin: 8px 0;
    }

    .compare-change {
      font-size: 13px;
      display: flex;
      align-items: center;
      gap: 4px;

      &.up {
        color: #f56c6c;
      }

      &.down {
        color: #67c23a;
      }
    }
  }

  .chart-container-large {
    width: 100%;
    height: 400px;
  }

  .chart-container-radar {
    width: 100%;
    height: 450px;
  }
}
</style>
