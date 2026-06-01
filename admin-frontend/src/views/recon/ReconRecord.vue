<template>
  <div>
    <el-card>
      <div class="filter-bar">
        <el-button type="primary" @click="showReconDialog = true">手动触发对账</el-button>
      </div>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="reconDate" label="对账日期" />
        <el-table-column prop="channel" label="渠道" />
        <el-table-column prop="totalCount" label="总笔数" />
        <el-table-column prop="matchedCount" label="匹配笔数" />
        <el-table-column prop="longCount" label="长款笔数" />
        <el-table-column prop="shortCount" label="短款笔数" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'DONE' ? 'success' : row.status === 'HAS_DIFF' ? 'warning' : 'info'">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button text type="primary" @click="viewDetails(row)">查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showReconDialog" title="手动触发对账" width="400px">
      <el-date-picker v-model="reconDate" type="date" placeholder="选择对账日期" value-format="YYYY-MM-DD" />
      <el-select v-model="reconChannel" placeholder="选择渠道" style="margin-top: 16px; width: 100%">
        <el-option label="微信" value="WECHAT" />
        <el-option label="支付宝" value="ALIPAY" />
        <el-option label="银行卡" value="BANK_CARD" />
        <el-option label="医保" value="MEDICARE" />
        <el-option label="HIS" value="HIS" />
      </el-select>
      <template #footer>
        <el-button @click="showReconDialog = false">取消</el-button>
        <el-button type="primary" @click="executeRecon" :loading="reconLoading">执行对账</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { formatStatus } from '../../utils/format'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const list = ref<any[]>([])
const showReconDialog = ref(false)
const reconDate = ref('')
const reconChannel = ref('')
const reconLoading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/recon/records', { params: { pageNum: 1, pageSize: 50 } })
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

function viewDetails(row: any) {
  router.push({ path: '/recon/detail', query: { recordId: row.id } })
}

async function executeRecon() {
  if (!reconDate.value || !reconChannel.value) {
    ElMessage.warning('请选择日期和渠道')
    return
  }
  reconLoading.value = true
  try {
    await request.post('/admin/recon/execute', null, { params: { reconDate: reconDate.value, channel: reconChannel.value } })
    ElMessage.success('对账已执行')
    showReconDialog.value = false
    loadData()
  } finally {
    reconLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.filter-bar { margin-bottom: 16px; }
</style>
