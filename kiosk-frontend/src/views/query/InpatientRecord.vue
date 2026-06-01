<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2 class="page-title">住院充值记录</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="action-bar">
        <el-button type="primary" size="large" round @click="loadData" v-if="!loaded">
          <el-icon><Search /></el-icon> 查询充值记录
        </el-button>
        <el-button v-else size="large" round @click="loaded = false; records = []">
          <el-icon><Refresh /></el-icon> 重新查询
        </el-button>
      </div>
      <div v-if="records.length" class="table-wrap">
        <el-table :data="records" stripe size="large" style="width: 100%">
          <el-table-column prop="depositNo" label="流水号" />
          <el-table-column prop="inpatientNo" label="住院号" />
          <el-table-column label="充值金额">
            <template #default="{ row }">¥{{ formatAmount(row.amount) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" />
        </el-table>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'
import { usePatientStore } from '../../stores/patient'

const patientStore = usePatientStore()
const records = ref<any[]>([])
const loaded = ref(false)

async function loadData() {
  const res: any = await request.get('/kiosk/query/inpatient-records', { params: { patientId: patientStore.patientId } })
  records.value = res.data || []
  loaded.value = true
}
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; padding: 28px 32px; overflow-y: auto; }
.action-bar { display: flex; justify-content: center; padding: 20px 0; margin-bottom: 16px; }
.table-wrap { background: white; border-radius: 16px; padding: 4px; box-shadow: var(--shadow); }
</style>
