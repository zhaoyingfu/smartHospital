<template>
  <div>
    <el-card>
      <div class="filter-bar">
        <el-button type="primary" @click="showGenerateDialog = true">生成结算</el-button>
      </div>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="settleDate" label="结算日期" />
        <el-table-column prop="totalTransaction" label="交易笔数" />
        <el-table-column label="总金额">
          <template #default="{ row }">¥{{ formatAmount(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="微信">
          <template #default="{ row }">¥{{ formatAmount(row.wechatAmount) }}</template>
        </el-table-column>
        <el-table-column label="支付宝">
          <template #default="{ row }">¥{{ formatAmount(row.alipayAmount) }}</template>
        </el-table-column>
        <el-table-column label="银行卡">
          <template #default="{ row }">¥{{ formatAmount(row.bankAmount) }}</template>
        </el-table-column>
        <el-table-column label="医保">
          <template #default="{ row }">¥{{ formatAmount(row.medicareAmount) }}</template>
        </el-table-column>
        <el-table-column label="退款">
          <template #default="{ row }">¥{{ formatAmount(row.totalRefund) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="{ PENDING: 'info', CONFIRMED: 'warning', LOCKED: 'success' }[row.status]">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" text type="primary" @click="confirmSettlement(row)">确认</el-button>
            <el-button v-if="row.status === 'CONFIRMED'" text type="success" @click="lockSettlement(row)">锁定</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showGenerateDialog" title="生成结算" width="400px">
      <el-date-picker v-model="generateDate" type="date" placeholder="选择结算日期" value-format="YYYY-MM-DD" style="width: 100%" />
      <template #footer>
        <el-button @click="showGenerateDialog = false">取消</el-button>
        <el-button type="primary" @click="generateSettlement">生成</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { formatAmount, formatStatus } from '../../utils/format'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref<any[]>([])
const showGenerateDialog = ref(false)
const generateDate = ref('')

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/settlement/list', { params: { pageNum: 1, pageSize: 50 } })
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

async function generateSettlement() {
  if (!generateDate.value) return
  await request.post('/admin/settlement/generate', null, { params: { settleDate: generateDate.value } })
  ElMessage.success('结算已生成')
  showGenerateDialog.value = false
  loadData()
}

async function confirmSettlement(row: any) {
  await request.post(`/admin/settlement/${row.id}/confirm`)
  ElMessage.success('已确认')
  loadData()
}

async function lockSettlement(row: any) {
  await request.post(`/admin/settlement/${row.id}/lock`)
  ElMessage.success('已锁定')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.filter-bar { margin-bottom: 16px; }
</style>
