<template>
  <div>
    <el-page-header @back="$router.back()" title="返回" :content="'交易详情'" />
    <el-card style="margin-top: 16px" v-loading="loading">
      <el-descriptions :column="2" border v-if="order">
        <el-descriptions-item label="订单号">{{ order.outTradeNo }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ order.bizType }}</el-descriptions-item>
        <el-descriptions-item label="业务单号">{{ order.bizNo }}</el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ formatAmount(order.totalAmount) }}</el-descriptions-item>
        <el-descriptions-item label="医保金额">¥{{ formatAmount(order.insuranceAmount) }}</el-descriptions-item>
        <el-descriptions-item label="自费金额">¥{{ formatAmount(order.selfAmount) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="order.status === 'SUCCESS' ? 'success' : 'danger'">{{ formatStatus(order.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
      </el-descriptions>
      <h3 style="margin-top: 24px">支付明细</h3>
      <el-table :data="details" stripe>
        <el-table-column prop="payMethod" label="支付方式" />
        <el-table-column label="金额">
          <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="tradeNo" label="第三方流水号" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">{{ formatStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="failReason" label="失败原因" />
        <el-table-column prop="payTime" label="支付时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../../utils/request'
import { formatAmount, formatStatus } from '../../utils/format'

const route = useRoute()
const loading = ref(false)
const order = ref<any>(null)
const details = ref<any[]>([])

async function loadData() {
  loading.value = true
  try {
    const id = route.params.id
    const res: any = await request.get(`/admin/transaction/${id}`)
    order.value = res.data
    const detailRes: any = await request.get(`/admin/transaction/${id}/details`)
    details.value = detailRes.data || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
