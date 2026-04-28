<template>
  <div class="statistics">
    <div class="card">
      <div class="card-header">
        <span class="card-title">财务统计</span>
        <div class="header-actions">
          <el-radio-group v-model="periodType" @change="handlePeriodChange">
            <el-radio-button value="month">月度</el-radio-button>
            <el-radio-button value="year">年度</el-radio-button>
          </el-radio-group>
          <el-date-picker
            v-if="periodType === 'month'"
            v-model="selectedMonth"
            type="month"
            value-format="YYYY-MM"
            placeholder="选择月份"
            @change="loadData"
          />
          <el-date-picker
            v-else
            v-model="selectedYear"
            type="year"
            value-format="YYYY"
            placeholder="选择年份"
            @change="loadData"
          />
        </div>
      </div>

      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card income">
            <div class="stat-value">¥{{ summary.income || 0 }}</div>
            <div class="stat-label">{{ periodType === 'month' ? '本月' : '本年' }}收入</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card expense">
            <div class="stat-value">¥{{ summary.expense || 0 }}</div>
            <div class="stat-label">{{ periodType === 'month' ? '本月' : '本年' }}支出</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card balance">
            <div class="stat-value">¥{{ summary.balance || 0 }}</div>
            <div class="stat-label">{{ periodType === 'month' ? '本月' : '本年' }}结余</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card rate">
            <div class="stat-value">{{ savingsRate }}%</div>
            <div class="stat-label">储蓄率</div>
          </div>
        </el-col>
      </el-row>
    </div>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">收入类别统计</span>
          </div>
          <div ref="incomeChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">支出类别统计</span>
          </div>
          <div ref="expenseChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="24">
        <div class="card">
          <div class="card-header">
            <span class="card-title">{{ periodType === 'month' ? '每日' : '月度' }}收支趋势</span>
          </div>
          <div ref="trendChart" class="chart-container-large"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">收入分布（饼图）</span>
          </div>
          <div ref="incomePieChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">支出分布（环形图）</span>
          </div>
          <div ref="expenseRingChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { recordApi } from '@/api/record'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const periodType = ref('month')
const selectedMonth = ref(dayjs().format('YYYY-MM'))
const selectedYear = ref(dayjs().format('YYYY'))
const incomeChart = ref<HTMLElement>()
const expenseChart = ref<HTMLElement>()
const trendChart = ref<HTMLElement>()
const incomePieChart = ref<HTMLElement>()
const expenseRingChart = ref<HTMLElement>()

const summary = reactive({
  income: 0,
  expense: 0,
  balance: 0
})

const savingsRate = computed(() => {
  if (summary.income > 0) {
    return ((summary.balance / summary.income) * 100).toFixed(1)
  }
  return 0
})

const handlePeriodChange = () => {
  loadData()
}

const loadData = async () => {
  try {
    if (periodType.value === 'month') {
      const [summaryRes, incomeRes, expenseRes, dailyRes] = await Promise.all([
        recordApi.getMonthlySummary(selectedMonth.value),
        recordApi.getCategoryStatistics(1, selectedMonth.value),
        recordApi.getCategoryStatistics(2, selectedMonth.value),
        recordApi.getDailyStatistics(selectedMonth.value)
      ])

      if (summaryRes.code === 200) {
        summary.income = Number(summaryRes.data.income).toFixed(2)
        summary.expense = Number(summaryRes.data.expense).toFixed(2)
        summary.balance = Number(summaryRes.data.balance).toFixed(2)
      }

      await nextTick()
      if (incomeRes.code === 200) initCategoryChart(incomeChart.value, incomeRes.data, '收入', '#67c23a')
      if (expenseRes.code === 200) initCategoryChart(expenseChart.value, expenseRes.data, '支出', '#f56c6c')
      if (dailyRes.code === 200) initTrendChart(trendChart.value, dailyRes.data, 'daily')
      if (incomeRes.code === 200) initPieChart(incomePieChart.value, incomeRes.data, '收入分布', 'pie')
      if (expenseRes.code === 200) initPieChart(expenseRingChart.value, expenseRes.data, '支出分布', 'ring')
    } else {
      const res = await recordApi.getMonthlyStatistics(selectedYear.value)
      if (res.code === 200) {
        let totalIncome = 0
        let totalExpense = 0
        res.data.forEach((item: any) => {
          totalIncome += Number(item.income || 0)
          totalExpense += Number(item.expense || 0)
        })
        summary.income = totalIncome.toFixed(2)
        summary.expense = totalExpense.toFixed(2)
        summary.balance = (totalIncome - totalExpense).toFixed(2)

        await nextTick()
        initTrendChart(trendChart.value, res.data, 'monthly')
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const initCategoryChart = (el: HTMLElement | undefined, data: any[], name: string, color: string) => {
  if (!el || data.length === 0) return

  const chartData = data.map((item: any) => ({
    name: item.name,
    value: Number(item.total || 0)
  })).filter((item: any) => item.value > 0)

  if (chartData.length === 0) return

  const chart = echarts.init(el)
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: chartData.map((d: any) => d.name)
    },
    series: [
      {
        name: name,
        type: 'bar',
        data: chartData.map((d: any) => d.value),
        itemStyle: { color }
      }
    ]
  })
}

const initTrendChart = (el: HTMLElement | undefined, data: any[], type: 'daily' | 'monthly') => {
  if (!el) return

  const dates = data.map((item: any) => type === 'daily' ? item.date.split('-')[2] : item.month.split('-')[1] + '月')
  const incomes = data.map((item: any) => Number(item.income || 0))
  const expenses = data.map((item: any) => Number(item.expense || 0))

  const chart = echarts.init(el)
  chart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['收入', '支出']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        data: incomes,
        areaStyle: { color: 'rgba(103, 194, 58, 0.2)' },
        lineStyle: { color: '#67c23a' },
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '支出',
        type: 'line',
        smooth: true,
        data: expenses,
        areaStyle: { color: 'rgba(245, 108, 108, 0.2)' },
        lineStyle: { color: '#f56c6c' },
        itemStyle: { color: '#f56c6c' }
      }
    ]
  })
}

const initPieChart = (el: HTMLElement | undefined, data: any[], name: string, chartType: 'pie' | 'ring') => {
  if (!el) return

  const chartData = data.map((item: any) => ({
    name: item.name,
    value: Number(item.total || 0)
  })).filter((item: any) => item.value > 0)

  if (chartData.length === 0) return

  const chart = echarts.init(el)
  const radius = chartType === 'ring' ? ['40%', '70%'] : '70%'

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
        name: name,
        type: 'pie',
        radius,
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  })
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.statistics {
  .header-actions {
    display: flex;
    align-items: center;
    gap: 15px;
  }

  .stat-card {
    text-align: center;
    padding: 20px;
    border-radius: 8px;
    color: #fff;

    .stat-value {
      font-size: 28px;
      font-weight: 600;
    }

    .stat-label {
      font-size: 14px;
      margin-top: 8px;
      opacity: 0.8;
    }

    &.income {
      background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
    }

    &.expense {
      background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
    }

    &.balance {
      background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    }

    &.rate {
      background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
    }
  }

  .chart-container {
    width: 100%;
    height: 350px;
  }

  .chart-container-large {
    width: 100%;
    height: 400px;
  }
}
</style>
