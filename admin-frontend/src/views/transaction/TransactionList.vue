<template>
  <div>
    <el-card>
      <div class="filter-bar">
        <el-select v-model="query.bizType" placeholder="业务类型" clearable>
          <el-option label="挂号" value="REGISTRATION" />
          <el-option label="门诊缴费" value="OUTPATIENT" />
          <el-option label="住院充值" value="INPATIENT" />
        </el-select>
        <el-select v-model="query.status" placeholder="支付状态" clearable>
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失败" value="FAILED" />
          <el-option label="待支付" value="PENDING" />
          <el-option label="已关闭" value="CLOSED" />
          <el-option label="已退款" value="REFUNDED" />
        </el-select>
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="outTradeNo" label="订单号" width="220" />
        <el-table-column prop="bizType" label="业务类型" width="120">
          <template #default="{ row }">{{ { REGISTRATION: '挂号', OUTPATIENT: '门诊缴费', INPATIENT: '住院充值' }[row.bizType] }}</template>
        </el-table-column>
        <el-table-column prop="bizNo" label="业务单号" width="180" />
        <el-table-column label="金额">
          <template #default="{ row }">¥{{ formatAmount(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : row.status === 'FAILED' ? 'danger' : 'info'">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button text type="primary" @click="$router.push(`/transaction/${row.id}`)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
        style="margin-top: 16px"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { formatAmount, formatStatus } from '../../utils/format'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const dateRange = ref<string[]>([])
const query = ref({ pageNum: 1, pageSize: 10, bizType: '', status: '' })

async function loadData() {
  loading.value = true
  try {
    const params: any = { ...query.value }
    if (!params.bizType) delete params.bizType
    if (!params.status) delete params.status
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res: any = await request.get('/admin/transaction/list', { params })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
