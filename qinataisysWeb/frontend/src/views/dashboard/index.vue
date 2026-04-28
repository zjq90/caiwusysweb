<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card income">
          <div class="stat-icon">
            <el-icon :size="32"><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.income || 0 }}</div>
            <div class="stat-label">本月收入</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card expense">
          <div class="stat-icon">
            <el-icon :size="32"><ShoppingCart /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.expense || 0 }}</div>
            <div class="stat-label">本月支出</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card balance">
          <div class="stat-icon">
            <el-icon :size="32"><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ summary.balance || 0 }}</div>
            <div class="stat-label">本月结余</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card budget">
          <div class="stat-icon">
            <el-icon :size="32"><Coin /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ budgetUsage }}%</div>
            <div class="stat-label">预算使用</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <div class="card">
          <div class="card-header">
            <span class="card-title">本月收支趋势</span>
          </div>
          <div ref="trendChart" class="chart-container"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card">
          <div class="card-header">
            <span class="card-title">支出类别分布</span>
          </div>
          <div ref="pieChart" class="chart-container"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">最近收支</span>
            <el-button type="primary" link @click="$router.push('/records')">查看全部</el-button>
          </div>
          <el-table :data="recentRecords" style="width: 100%" v-if="recentRecords.length">
            <el-table-column prop="categoryName" label="类别" />
            <el-table-column prop="type" label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="row.type === 1 ? 'success' : 'danger'" size="small">
                  {{ row.type === 1 ? '收入' : '支出' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span :class="row.type === 1 ? 'text-success' : 'text-danger'">
                  {{ row.type === 1 ? '+' : '-' }}{{ row.amount }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="recordTime" label="时间" width="160">
              <template #default="{ row }">
                {{ formatTime(row.recordTime) }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无记录" />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">待办备忘</span>
            <el-button type="primary" link @click="$router.push('/memos')">查看全部</el-button>
          </div>
          <el-table :data="recentMemos" style="width: 100%" v-if="recentMemos.length">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="important" label="重要" width="80">
              <template #default="{ row }">
                <el-tag v-if="row.important" type="danger" size="small">重要</el-tag>
                <span v-else class="text-info">-</span>
              </template>
            </el-table-column>
            <el-table-column prop="reminderTime" label="提醒时间" width="160">
              <template #default="{ row }">
                {{ row.reminderTime ? formatTime(row.reminderTime) : '-' }}
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无备忘" />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">我的心愿</span>
            <el-button type="primary" link @click="$router.push('/wishes')">查看全部</el-button>
          </div>
          <el-table :data="wishes" style="width: 100%" v-if="wishes.length">
            <el-table-column prop="title" label="心愿" />
            <el-table-column prop="progress" label="进度" width="200">
              <template #default="{ row }">
                <el-progress :percentage="row.progress || 0" :status="row.progress >= 100 ? 'success' : ''" />
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
                  {{ row.status === 1 ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无心愿" />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <div class="card-header">
            <span class="card-title">快捷操作</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="shortcut-item" @click="$router.push('/records/add')">
                <el-icon :size="32" color="#409eff"><Plus /></el-icon>
                <span>添加记账</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="shortcut-item" @click="$router.push('/records')">
                <el-icon :size="32" color="#67c23a"><Notebook /></el-icon>
                <span>收支明细</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="shortcut-item" @click="$router.push('/statistics')">
                <el-icon :size="32" color="#e6a23c"><DataAnalysis /></el-icon>
                <span>财务统计</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="shortcut-item" @click="$router.push('/calculator')">
                <el-icon :size="32" color="#f56c6c"><Calculator /></el-icon>
                <span>计算器</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { recordApi } from '@/api/record'
import { memoApi } from '@/api/memo'
import { wishApi } from '@/api/wish'
import { budgetApi } from '@/api/budget'
import * as echarts from 'echarts'
import dayjs from 'dayjs'

const userStore = useUserStore()
const trendChart = ref<HTMLElement>()
const pieChart = ref<HTMLElement>()

const summary = reactive({
  income: 0,
  expense: 0,
  balance: 0
})

const budgetUsage = ref(0)
const recentRecords = ref([])
const recentMemos = ref([])
const wishes = ref([])

const currentMonth = dayjs().format('YYYY-MM')

const formatTime = (time: string) => {
  return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

const loadSummary = async () => {
  try {
    const res = await recordApi.getMonthlySummary(currentMonth)
    if (res.code === 200) {
      summary.income = Number(res.data.income).toFixed(2)
      summary.expense = Number(res.data.expense).toFixed(2)
      summary.balance = Number(res.data.balance).toFixed(2)
    }
  } catch (error) {
    console.error('加载汇总数据失败:', error)
  }
}

const loadBudget = async () => {
  try {
    const res = await budgetApi.getByMonth(dayjs().year(), dayjs().month() + 1)
    if (res.code === 200 && res.data.length > 0) {
      const totalBudget = res.data.reduce((sum: number, b: any) => sum + Number(b.amount), 0)
      const totalUsed = res.data.reduce((sum: number, b: any) => sum + Number(b.usedAmount || 0), 0)
      if (totalBudget > 0) {
        budgetUsage.value = Math.min(100, Math.round((totalUsed / totalBudget) * 100))
      }
    }
  } catch (error) {
    console.error('加载预算数据失败:', error)
  }
}

const loadRecentRecords = async () => {
  try {
    const res = await recordApi.getList({ page: 1, size: 5 })
    if (res.code === 200) {
      recentRecords.value = res.data.records || []
    }
  } catch (error) {
    console.error('加载最近记录失败:', error)
  }
}

const loadRecentMemos = async () => {
  try {
    const res = await memoApi.getMyMemos()
    if (res.code === 200) {
      recentMemos.value = res.data.slice(0, 5)
    }
  } catch (error) {
    console.error('加载备忘失败:', error)
  }
}

const loadWishes = async () => {
  try {
    const res = await wishApi.getMyWishes()
    if (res.code === 200) {
      wishes.value = res.data.slice(0, 5)
    }
  } catch (error) {
    console.error('加载心愿单失败:', error)
  }
}

const initTrendChart = async () => {
  try {
    const res = await recordApi.getDailyStatistics(currentMonth)
    if (res.code === 200) {
      const dates = res.data.map((item: any) => item.date)
      const incomes = res.data.map((item: any) => Number(item.income || 0))
      const expenses = res.data.map((item: any) => Number(item.expense || 0))

      await nextTick()
      if (trendChart.value) {
        const chart = echarts.init(trendChart.value)
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
            data: dates.map((d: string) => d.split('-')[2])
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
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
                  { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
                ])
              },
              lineStyle: {
                color: '#67c23a'
              },
              itemStyle: {
                color: '#67c23a'
              }
            },
            {
              name: '支出',
              type: 'line',
              smooth: true,
              data: expenses,
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
                  { offset: 1, color: 'rgba(245, 108, 108, 0.05)' }
                ])
              },
              lineStyle: {
                color: '#f56c6c'
              },
              itemStyle: {
                color: '#f56c6c'
              }
            }
          ]
        })
      }
    }
  } catch (error) {
    console.error('初始化趋势图失败:', error)
  }
}

const initPieChart = async () => {
  try {
    const res = await recordApi.getCategoryStatistics(2, currentMonth)
    if (res.code === 200) {
      const data = res.data.map((item: any) => ({
        name: item.name,
        value: Number(item.total || 0)
      })).filter((item: any) => item.value > 0)

      await nextTick()
      if (pieChart.value && data.length > 0) {
        const chart = echarts.init(pieChart.value)
        chart.setOption({
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          series: [
            {
              name: '支出类别',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              itemStyle: {
                borderRadius: 10,
                borderColor: '#fff',
                borderWidth: 2
              },
              label: {
                show: false,
                position: 'center'
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: 16,
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: data
            }
          ]
        })
      }
    }
  } catch (error) {
    console.error('初始化饼图失败:', error)
  }
}

onMounted(() => {
  loadSummary()
  loadBudget()
  loadRecentRecords()
  loadRecentMemos()
  loadWishes()
  initTrendChart()
  initPieChart()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-row {
    margin-bottom: 20px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 8px;
    color: #fff;

    .stat-icon {
      width: 64px;
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 8px;
    }

    .stat-info {
      margin-left: 16px;

      .stat-value {
        font-size: 24px;
        font-weight: 600;
      }

      .stat-label {
        font-size: 14px;
        opacity: 0.8;
        margin-top: 4px;
      }
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

    &.budget {
      background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
    }
  }

  .chart-container {
    width: 100%;
    height: 350px;
  }

  .shortcut-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
    }

    span {
      margin-top: 8px;
      font-size: 14px;
      color: #606266;
    }
  }
}
</style>
