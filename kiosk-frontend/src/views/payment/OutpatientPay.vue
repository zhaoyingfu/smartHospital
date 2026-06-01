<template>
  <div class="page">
    <header class="page-header">
      <button class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </button>
      <h2 class="page-title">门诊缴费</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="search-section">
        <div class="card search-card">
          <el-icon :size="28" style="color: #2563eb;"><Search /></el-icon>
          <h3>请输入就诊号</h3>
          <p class="desc">病历本或就诊单上的就诊号</p>
          <input v-model="visitNo" placeholder="请输入就诊号" class="input" />
          <button class="btn btn-primary" @click="queryOrders" :disabled="loading">
            {{ loading ? '查询中…' : '查询待缴费单' }}
          </button>
        </div>
      </div>
      <div v-if="orders.length > 0" class="order-list">
        <p class="section-title">待缴费订单</p>
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-info">
            <span class="order-no">单号: {{ order.orderNo }}</span>
            <span class="badge unpaid">待缴费</span>
          </div>
          <span class="order-amount">¥{{ formatAmount(order.totalAmount) }}</span>
          <button class="btn btn-primary" @click="payOrder(order)">去缴费</button>
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
const visitNo = ref('')
const loading = ref(false)
const orders = ref<any[]>([])

async function queryOrders() {
  if (!visitNo.value) { ElMessage.warning('请输入就诊号'); return }
  loading.value = true
  try {
    const res: any = await request.post('/kiosk/payment/outpatient/create', {
      patientId: patientStore.patientId,
      visitNo: visitNo.value,
      kioskId: kioskStore.kioskId
    })
    if (res.data) orders.value = [res.data]
  } finally { loading.value = false }
}

function payOrder(_order: any) {
  ElMessage.success('模拟支付成功')
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
  padding: 40px 0;
}
.card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(0,0,0,.06);
}
.search-card {
  text-align: center;
  padding: 40px;
  max-width: 460px;
  width: 100%;
}
.search-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #222;
  margin: 12px 0 4px;
}
.desc { font-size: 14px; color: #999; margin: 0 0 20px; }
.input {
  width: 100%;
  max-width: 340px;
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

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #444;
  margin: 0 0 12px;
}
.order-list { max-width: 600px; margin: 0 auto; }
.order-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0,0,0,.06);
  margin-bottom: 10px;
}
.order-info { flex: 1; display: flex; flex-direction: column; gap: 4px; }
.order-no { font-size: 14px; color: #888; }
.badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 12px;
  width: fit-content;
}
.badge.unpaid { background: #fef3c7; color: #d97706; }
.order-amount { font-size: 22px; font-weight: 700; color: #e74c3c; }
.order-card .btn { width: auto; min-width: 100px; height: 40px; font-size: 14px; }
</style>
