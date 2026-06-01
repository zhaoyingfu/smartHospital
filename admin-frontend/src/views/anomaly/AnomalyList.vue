<template>
  <div>
    <el-card>
      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="detect" :loading="loading">手动检测</el-button>
      </div>
      <el-alert v-for="(item, index) in anomalies" :key="index" :title="item" type="warning" show-icon style="margin-bottom: 12px" />
      <el-empty v-if="anomalies.length === 0 && detected" description="暂无异常交易" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import request from '../../utils/request'

const loading = ref(false)
const anomalies = ref<string[]>([])
const detected = ref(false)

async function detect() {
  loading.value = true
  try {
    const res: any = await request.get('/admin/anomaly/detect')
    anomalies.value = res.data || []
    detected.value = true
  } finally {
    loading.value = false
  }
}
</script>
