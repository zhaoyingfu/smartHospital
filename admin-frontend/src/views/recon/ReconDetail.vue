<template>
  <div>
    <el-page-header @back="$router.back()" title="返回" content="对账明细" />
    <el-card style="margin-top: 16px" v-loading="loading">
      <el-table :data="details" stripe>
        <el-table-column prop="result" label="对账结果">
          <template #default="{ row }">
            <el-tag :type="row.result === 'MATCHED' ? 'success' : 'danger'">{{ formatStatus(row.result) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="systemTradeNo" label="系统订单号" />
        <el-table-column label="系统金额">
          <template #default="{ row }">{{ row.systemAmount ? '¥' + formatAmount(row.systemAmount) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="channelTradeNo" label="渠道流水号" />
        <el-table-column label="渠道金额">
          <template #default="{ row }">{{ row.channelAmount ? '¥' + formatAmount(row.channelAmount) : '-' }}</template>
        </el-table-column>
        <el-table-column label="差异金额">
          <template #default="{ row }">{{ row.diffAmount ? '¥' + formatAmount(row.diffAmount) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="handleStatus" label="处理状态">
          <template #default="{ row }">
            <el-tag :type="row.handleStatus === 'HANDLED' ? 'success' : 'warning'">{{ formatStatus(row.handleStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" v-if="true">
          <template #default="{ row }">
            <el-button v-if="row.handleStatus === 'UNHANDLED'" text type="primary" @click="handleDiff(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showHandleDialog" title="处理差异" width="400px">
      <el-select v-model="handleForm.handleResult" placeholder="处理方式" style="width: 100%">
        <el-option label="调账" value="ADJUST" />
        <el-option label="挂账" value="PENDING" />
        <el-option label="豁免" value="EXEMPT" />
      </el-select>
      <el-input v-model="handleForm.resolution" type="textarea" placeholder="处理说明" style="margin-top: 16px" />
      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../../utils/request'
import { formatAmount, formatStatus } from '../../utils/format'
import { ElMessage } from 'element-plus'

const route = useRoute()
const loading = ref(false)
const details = ref<any[]>([])
const showHandleDialog = ref(false)
const handleForm = ref({ detailId: 0, handleResult: '', resolution: '' })

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/recon/details', { params: { recordId: route.query.recordId } })
    details.value = res.data || []
  } finally {
    loading.value = false
  }
}

function handleDiff(row: any) {
  handleForm.value.detailId = row.id
  handleForm.value.handleResult = ''
  handleForm.value.resolution = ''
  showHandleDialog.value = true
}

async function submitHandle() {
  await request.post('/admin/recon/handle', handleForm.value)
  ElMessage.success('处理成功')
  showHandleDialog.value = false
  loadData()
}

onMounted(loadData)
</script>
