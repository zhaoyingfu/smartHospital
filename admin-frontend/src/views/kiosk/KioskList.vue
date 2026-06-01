<template>
  <div>
    <el-card>
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="machineNo" label="设备编号" />
        <el-table-column prop="location" label="安装位置" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ONLINE' ? 'success' : row.status === 'MAINTENANCE' ? 'warning' : 'danger'">
              {{ formatStatus(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paperStatus" label="打印纸状态">
          <template #default="{ row }">
            <el-tag :type="row.paperStatus === 'NORMAL' ? 'success' : row.paperStatus === 'LOW' ? 'warning' : 'danger'">
              {{ { NORMAL: '正常', LOW: '余量低', EMPTY: '缺纸' }[row.paperStatus] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastHeartbeat" label="最后心跳" />
        <el-table-column prop="appVersion" label="应用版本" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { formatStatus } from '../../utils/format'

const loading = ref(false)
const list = ref<any[]>([])

async function loadData() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/kiosk/list', { params: { pageNum: 1, pageSize: 50 } })
    list.value = res.data?.records || []
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
