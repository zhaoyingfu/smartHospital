<template>
  <div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="refundNo" label="退款单号" />
        <el-table-column prop="paymentOrderId" label="原支付订单ID" />
        <el-table-column label="退款金额">
          <template #default="{ row }">¥{{ formatAmount(row.refundAmount) }}</template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'REFUNDED' ? 'success' : row.status === 'FAILED' ? 'danger' : 'warning'">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" v-if="true">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING_APPROVE'" text type="primary" @click="approveRefund(row, true)">通过</el-button>
            <el-button v-if="row.status === 'PENDING_APPROVE'" text type="danger" @click="approveRefund(row, false)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" :total="total" layout="total, prev, pager, next" @current-change="loadData" style="margin-top: 16px" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { formatAmount, formatStatus } from '../../utils/format'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/refund/list', { params: { pageNum: pageNum.value, pageSize: 10 } })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

async function approveRefund(row: any, approved: boolean) {
  await request.post('/admin/refund/approve', { refundId: row.id, approved, reason: approved ? '审批通过' : '审批拒绝' })
  ElMessage.success(approved ? '已通过' : '已拒绝')
  loadData()
}

onMounted(loadData)
</script>
