<template>
  <div class="calculator">
    <div class="card">
      <div class="card-header">
        <span class="card-title">计算器</span>
      </div>

      <div class="calculator-wrapper">
        <div class="calculator-display">
          <div class="display-history">{{ history }}</div>
          <div class="display-result">{{ display }}</div>
        </div>

        <div class="calculator-buttons">
          <div class="button-row">
            <el-button class="calc-btn" @click="handleClear">AC</el-button>
            <el-button class="calc-btn" @click="handleBackspace">DEL</el-button>
            <el-button class="calc-btn operator" @click="handleOperator('%')">%</el-button>
            <el-button class="calc-btn operator" @click="handleOperator('/')">÷</el-button>
          </div>
          <div class="button-row">
            <el-button class="calc-btn" @click="handleNumber('7')">7</el-button>
            <el-button class="calc-btn" @click="handleNumber('8')">8</el-button>
            <el-button class="calc-btn" @click="handleNumber('9')">9</el-button>
            <el-button class="calc-btn operator" @click="handleOperator('*')">×</el-button>
          </div>
          <div class="button-row">
            <el-button class="calc-btn" @click="handleNumber('4')">4</el-button>
            <el-button class="calc-btn" @click="handleNumber('5')">5</el-button>
            <el-button class="calc-btn" @click="handleNumber('6')">6</el-button>
            <el-button class="calc-btn operator" @click="handleOperator('-')">−</el-button>
          </div>
          <div class="button-row">
            <el-button class="calc-btn" @click="handleNumber('1')">1</el-button>
            <el-button class="calc-btn" @click="handleNumber('2')">2</el-button>
            <el-button class="calc-btn" @click="handleNumber('3')">3</el-button>
            <el-button class="calc-btn operator" @click="handleOperator('+')">+</el-button>
          </div>
          <div class="button-row">
            <el-button class="calc-btn" @click="handleNumber('0')" style="flex: 2">0</el-button>
            <el-button class="calc-btn" @click="handleDecimal">.</el-button>
            <el-button class="calc-btn equal" @click="handleEqual">=</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const display = ref('0')
const history = ref('')
const currentValue = ref('')
const previousValue = ref('')
const operator = ref('')
const waitingForOperand = ref(false)

const handleNumber = (num: string) => {
  if (waitingForOperand.value) {
    currentValue.value = num
    waitingForOperand.value = false
  } else {
    currentValue.value = currentValue.value === '0' ? num : currentValue.value + num
  }
  display.value = currentValue.value
}

const handleDecimal = () => {
  if (waitingForOperand.value) {
    currentValue.value = '0.'
    waitingForOperand.value = false
  } else if (!currentValue.value.includes('.')) {
    currentValue.value += '.'
  }
  display.value = currentValue.value
}

const handleOperator = (op: string) => {
  const inputValue = parseFloat(currentValue.value)

  if (isNaN(inputValue)) return

  if (previousValue.value === '') {
    previousValue.value = currentValue.value
  } else if (operator.value) {
    const result = calculate(parseFloat(previousValue.value), parseFloat(currentValue.value), operator.value)
    previousValue.value = String(result)
    display.value = formatNumber(result)
  }

  waitingForOperand.value = true
  operator.value = op

  const opDisplay = { '+': '+', '-': '−', '*': '×', '/': '÷', '%': '%' }[op] || op
  history.value = `${previousValue.value} ${opDisplay}`
}

const calculate = (prev: number, current: number, op: string): number => {
  switch (op) {
    case '+':
      return prev + current
    case '-':
      return prev - current
    case '*':
      return prev * current
    case '/':
      return current !== 0 ? prev / current : 0
    case '%':
      return prev % current
    default:
      return current
  }
}

const handleEqual = () => {
  if (!operator.value || waitingForOperand.value) return

  const inputValue = parseFloat(currentValue.value)
  if (isNaN(inputValue)) return

  const result = calculate(parseFloat(previousValue.value), inputValue, operator.value)
  const opDisplay = { '+': '+', '-': '−', '*': '×', '/': '÷', '%': '%' }[operator.value] || operator.value

  history.value = `${previousValue.value} ${opDisplay} ${currentValue.value} =`
  display.value = formatNumber(result)

  previousValue.value = ''
  currentValue.value = String(result)
  operator.value = ''
  waitingForOperand.value = true
}

const handleClear = () => {
  display.value = '0'
  history.value = ''
  currentValue.value = ''
  previousValue.value = ''
  operator.value = ''
  waitingForOperand.value = false
}

const handleBackspace = () => {
  if (waitingForOperand.value) return

  if (currentValue.value.length > 1) {
    currentValue.value = currentValue.value.slice(0, -1)
  } else {
    currentValue.value = '0'
  }
  display.value = currentValue.value
}

const formatNumber = (num: number): string => {
  if (isNaN(num) || !isFinite(num)) return 'Error'
  if (num === 0) return '0'
  
  const result = parseFloat(num.toPrecision(12))
  return String(result)
}
</script>

<style lang="scss" scoped>
.calculator {
  .calculator-wrapper {
    max-width: 400px;
    margin: 0 auto;
  }

  .calculator-display {
    background: #303133;
    padding: 20px;
    border-radius: 8px 8px 0 0;
    text-align: right;

    .display-history {
      font-size: 14px;
      color: #909399;
      min-height: 20px;
      margin-bottom: 5px;
    }

    .display-result {
      font-size: 36px;
      font-weight: 600;
      color: #fff;
      word-break: break-all;
    }
  }

  .calculator-buttons {
    background: #f5f7fa;
    padding: 10px;
    border-radius: 0 0 8px 8px;
  }

  .button-row {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .calc-btn {
    flex: 1;
    height: 60px;
    font-size: 20px;
    font-weight: 500;
    border-radius: 8px;

    &.operator {
      background: #409eff;
      color: #fff;
      border-color: #409eff;

      &:hover {
        background: #66b1ff;
        border-color: #66b1ff;
      }
    }

    &.equal {
      background: #67c23a;
      color: #fff;
      border-color: #67c23a;

      &:hover {
        background: #85ce61;
        border-color: #85ce61;
      }
    }
  }
}
</style>
