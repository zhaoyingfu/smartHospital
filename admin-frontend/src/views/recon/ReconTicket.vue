<template>
  <div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'CLOSED' ? 'success' : 'warning'">{{ { OPEN: '待处理', IN_PROGRESS: '处理中', CLOSED: '已关闭' }[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="resolution" label="处理结果" />
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'

const loading = ref(false)
const list = ref<any[]>([])

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/recon/tickets', { params: { pageNum: 1, pageSize: 50 } })
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
