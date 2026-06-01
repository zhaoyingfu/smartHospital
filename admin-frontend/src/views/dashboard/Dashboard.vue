<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in summaryCards" :key="card.title">
        <el-card shadow="hover">
          <el-statistic :title="card.title" :value="card.value" />
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top: 20px">
      <h3>今日交易概况</h3>
      <p>系统运行正常，详细数据请查看各管理页面</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'

const summaryCards = ref([
  { title: '今日交易笔数', value: 0 },
  { title: '今日交易金额(元)', value: 0 },
  { title: '今日退款笔数', value: 0 },
  { title: '在线设备数', value: 0 },
])

async function loadSummary() {
  try {
    const res: any = await request.get('/admin/dashboard/summary')
    if (res.data) {
      summaryCards.value[0].value = res.data.todayTransactionCount
      summaryCards.value[1].value = Number(formatAmount(res.data.todayTransactionAmount))
      summaryCards.value[2].value = res.data.todayRefundCount
      summaryCards.value[3].value = res.data.onlineDeviceCount
    }
  } catch {}
}

onMounted(loadSummary)
</script>
