<template>
  <div class="page">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回
      </button>
      <h2 class="page-title">确认挂号信息</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body centered">
      <div class="card">
        <div class="info-grid">
          <div class="info-item">
            <span class="label">科室</span>
            <span class="value">{{ departmentName }}</span>
          </div>
          <div class="info-item">
            <span class="label">医生</span>
            <span class="value">{{ doctorName }}</span>
          </div>
          <div class="info-item">
            <span class="label">日期</span>
            <span class="value">{{ date }}</span>
          </div>
          <div class="info-item">
            <span class="label">时段</span>
            <span class="value">{{ period }}</span>
          </div>
          <div class="info-item fee">
            <span class="label">挂号费</span>
            <span class="value amount">¥{{ formatAmount(fee) }}</span>
          </div>
        </div>
        <div class="actions">
          <button class="btn btn-outline" @click="$router.back()">取消</button>
          <button class="btn btn-primary" @click="confirmReg" :disabled="loading">
            {{ loading ? '提交中…' : '确认挂号' }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'
import { usePatientStore } from '../../stores/patient'
import { useKioskStore } from '../../stores/kiosk'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const patientStore = usePatientStore()
const kioskStore = useKioskStore()
const loading = ref(false)

const scheduleId = route.query.scheduleId as string
const doctorName = route.query.doctorName as string
const departmentName = route.query.departmentName as string
const date = route.query.date as string
const period = route.query.period as string
const fee = Number(route.query.fee)

async function confirmReg() {
  loading.value = true
  try {
    const res: any = await request.post('/kiosk/registration/register', {
      patientId: patientStore.patientId,
      scheduleId: Number(scheduleId),
      regType: 'SAME_DAY',
      kioskId: kioskStore.kioskId
    })
    if (res.data) {
      ElMessage.success('挂号成功')
      router.push({
        path: '/registration/result',
        query: {
          regNo: res.data.regNo,
          doctorName,
          departmentName,
          date,
          period,
          fee: String(fee),
          paymentOrderId: res.data.paymentOrderId || ''
        }
      })
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}
.page-header {
  display: flex;
  align-items: center;
  padding: 20px 32px;
  background: #fff;
  border-bottom: 1px solid #eef0f4;
}
.back-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  padding: 0;
  font-family: inherit;
}
.back-btn:hover { color: #222; }
.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #222;
  text-align: center;
  flex: 1;
}
.page-spacer { width: 80px; }
.page-body {
  flex: 1;
  padding: 32px;
  overflow-y: auto;
}
.centered {
  display: flex;
  align-items: center;
  justify-content: center;
}
.card {
  background: #fff;
  border-radius: 16px;
  padding: 36px;
  box-shadow: 0 4px 24px rgba(0,0,0,.06);
  max-width: 480px;
  width: 100%;
}
.info-grid { margin-bottom: 28px; }
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #eef0f4;
}
.info-item:last-child { border-bottom: none; }
.label { font-size: 15px; color: #888; }
.value { font-size: 16px; font-weight: 500; color: #222; }
.info-item.fee .value { font-size: 22px; }
.amount { color: #e74c3c; font-weight: 700; }
.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.btn {
  min-width: 140px;
  height: 48px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  font-family: inherit;
  transition: .2s;
}
.btn:disabled { opacity: .5; cursor: not-allowed; }
.btn-primary { background: #2563eb; color: #fff; }
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-outline { background: #fff; color: #444; border: 1px solid #d0d5dd; }
.btn-outline:hover { background: #f8f9fa; }
</style>
