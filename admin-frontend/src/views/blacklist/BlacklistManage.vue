<template>
  <div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="patientId" label="患者ID" />
        <el-table-column prop="reason" label="拉黑原因" />
        <el-table-column prop="createTime" label="拉黑时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button text type="danger" @click="removeBlacklist(row)">移出黑名单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref<any[]>([])

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/blacklist/list', { params: { pageNum: 1, pageSize: 50 } })
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

async function removeBlacklist(row: any) {
  await request.delete(`/admin/blacklist/${row.patientId}`)
  ElMessage.success('已移出黑名单')
  loadData()
}

onMounted(loadData)
</script>
