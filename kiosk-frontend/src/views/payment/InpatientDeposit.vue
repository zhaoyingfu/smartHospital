<template>
  <div class="page">
    <header class="page-header">
      <button class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </button>
      <h2 class="page-title">住院预交金充值</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="search-section">
        <div class="card search-card">
          <el-icon :size="28" style="color: #2563eb;"><Search /></el-icon>
          <h3>查询住院信息</h3>
          <input v-model="inpatientNo" placeholder="请输入住院号" class="input" />
          <button class="btn btn-primary" @click="queryInfo" :disabled="loading">
            {{ loading ? '查询中…' : '查询' }}
          </button>
        </div>
      </div>
      <div v-if="patientInfo" class="info-section">
        <div class="card info-card">
          <div class="row"><span>患者</span><span>{{ patientInfo.patientName }}</span></div>
          <div class="row"><span>科室</span><span>{{ patientInfo.departmentName }}</span></div>
          <div class="row"><span>床号</span><span>{{ patientInfo.bedNo }}</span></div>
          <div class="row balance"><span>账户余额</span><span>¥{{ formatAmount(patientInfo.balance) }}</span></div>
        </div>
        <div class="card amount-card">
          <h4>选择充值金额</h4>
          <div class="amount-grid">
            <button
              v-for="a in [500, 1000, 2000, 5000]"
              :key="a"
              class="amount-btn"
              :class="{ selected: amount === a * 100 }"
              @click="amount = a * 100"
            >
              ¥{{ a }}
            </button>
          </div>
          <button
            class="btn btn-primary"
            @click="doDeposit"
            :disabled="amount <= 0"
          >
            充值 ¥{{ formatAmount(amount) }}
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'
import { usePatientStore } from '../../stores/patient'
import { useKioskStore } from '../../stores/kiosk'
import { ElMessage } from 'element-plus'

const patientStore = usePatientStore()
const kioskStore = useKioskStore()
const inpatientNo = ref('')
const loading = ref(false)
const patientInfo = ref<any>(null)
const amount = ref(0)

async function queryInfo() {
  if (!inpatientNo.value) { ElMessage.warning('请输入住院号'); return }
  loading.value = true
  try {
    const res: any = await request.get('/kiosk/payment/inpatient/info', { params: { inpatientNo: inpatientNo.value } })
    patientInfo.value = res.data
  } finally { loading.value = false }
}

async function doDeposit() {
  if (amount.value <= 0) { ElMessage.warning('请选择充值金额'); return }
  try {
    await request.post('/kiosk/payment/inpatient/deposit', {
      patientId: patientStore.patientId,
      inpatientNo: inpatientNo.value,
      amount: amount.value,
      kioskId: kioskStore.kioskId
    })
    ElMessage.success('充值成功')
    patientInfo.value = null
    amount.value = 0
  } catch {}
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
  padding: 28px 32px;
  overflow-y: auto;
}

.search-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
.card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,.06);
}
.search-card {
  text-align: center;
  padding: 36px;
  max-width: 420px;
  width: 100%;
}
.search-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin: 12px 0 20px;
}
.input {
  width: 100%;
  max-width: 300px;
  height: 48px;
  padding: 0 16px;
  border: 1px solid #d0d5dd;
  border-radius: 10px;
  font-size: 16px;
  outline: none;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 16px;
  display: block;
  margin-left: auto;
  margin-right: auto;
}
.input:focus { border-color: #2563eb; }
.btn {
  width: 220px;
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

.info-section {
  max-width: 480px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.info-card { padding: 20px 24px; }
.info-card .row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #eef0f4;
  font-size: 15px;
}
.info-card .row:last-child { border-bottom: none; }
.info-card .row span:first-child { color: #888; }
.info-card .row span:last-child { font-weight: 500; color: #222; }
.balance span:last-child { color: #2563eb; font-weight: 700; font-size: 18px; }

.amount-card { padding: 24px; text-align: center; }
.amount-card h4 {
  font-size: 15px;
  font-weight: 600;
  color: #444;
  margin: 0 0 16px;
}
.amount-grid {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 20px;
}
.amount-btn {
  width: 90px;
  height: 48px;
  border: 2px solid #d0d5dd;
  border-radius: 12px;
  background: #fff;
  font-size: 16px;
  font-weight: 600;
  color: #222;
  cursor: pointer;
  transition: all .2s;
  font-family: inherit;
}
.amount-btn:hover { border-color: #2563eb; color: #2563eb; }
.amount-btn.selected { border-color: #2563eb; background: #eff6ff; color: #2563eb; }
.amount-card .btn { width: 240px; }
</style>
