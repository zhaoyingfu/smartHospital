<template>
  <div class="page">
    <header class="page-header">
      <button class="back-btn" @click="goBack">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回
      </button>
      <h2 class="page-title">选择支付方式</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body centered">
      <div v-if="!payDone" class="pay-select">
        <p class="hint">请选择支付方式完成支付</p>
        <p class="amount">¥{{ formatAmount(fee) }}</p>
        <div class="method-grid">
          <button class="method-card" @click="doPay('WECHAT')" :disabled="loading">
            <span class="method-icon" style="background:#07c160;color:#fff">微</span>
            <span class="method-name">微信支付</span>
          </button>
          <button class="method-card" @click="doPay('ALIPAY')" :disabled="loading">
            <span class="method-icon" style="background:#1677ff;color:#fff">支</span>
            <span class="method-name">支付宝</span>
          </button>
          <button class="method-card" @click="doPay('BANK_CARD')" :disabled="loading">
            <span class="method-icon" style="background:#faad14;color:#fff">卡</span>
            <span class="method-name">银行卡</span>
          </button>
          <button class="method-card" @click="doPay('MEDICARE')" :disabled="loading">
            <span class="method-icon" style="background:#e74c3c;color:#fff">医</span>
            <span class="method-name">医保支付</span>
          </button>
        </div>
        <p v-if="loading" class="processing">支付处理中，请稍候…</p>
      </div>
      <div v-else class="pay-result">
        <div v-if="paySuccess" class="result-card">
          <div class="icon-wrap success">
            <el-icon :size="44"><CircleCheckFilled /></el-icon>
          </div>
          <h3 class="result-title">支付成功</h3>
          <p class="result-amount">¥{{ formatAmount(fee) }}</p>
          <p class="result-method">{{ methodLabel }}</p>
          <button class="btn btn-primary" @click="$router.push('/')">返回首页</button>
        </div>
        <div v-else class="result-card">
          <div class="icon-wrap fail">
            <el-icon :size="44"><CloseBold /></el-icon>
          </div>
          <h3 class="result-title">支付失败</h3>
          <p class="result-msg">{{ failMsg }}</p>
          <div class="actions">
            <button class="btn btn-primary" @click="resetPay">重新支付</button>
            <button class="btn btn-outline" @click="$router.push('/')">返回首页</button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'
import { useKioskStore } from '../../stores/kiosk'

const router = useRouter()
const route = useRoute()
const kioskStore = useKioskStore()

const paymentOrderId = route.query.paymentOrderId as string
const fee = Number(route.query.fee)

const loading = ref(false)
const payDone = ref(false)
const paySuccess = ref(false)
const failMsg = ref('')
const selectedMethod = ref('')

const methodLabel = computed(() => {
  const map: Record<string, string> = { WECHAT: '微信支付', ALIPAY: '支付宝', BANK_CARD: '银行卡', MEDICARE: '医保支付' }
  return map[selectedMethod.value] || ''
})

function goBack() {
  if (payDone.value) {
    router.push('/')
  } else {
    router.back()
  }
}

async function doPay(method: string) {
  loading.value = true
  selectedMethod.value = method
  try {
    const res: any = await request.post('/kiosk/payment/pay', {
      paymentOrderId: Number(paymentOrderId),
      payMethod: method,
      kioskId: kioskStore.kioskId
    })
    paySuccess.value = res.data && res.data.status === 'SUCCESS'
    if (!paySuccess.value) {
      failMsg.value = res.data?.failReason || '支付处理失败，请重试'
    }
  } catch (e: any) {
    paySuccess.value = false
    failMsg.value = e.message || '网络异常，请重试'
  } finally {
    loading.value = false
    payDone.value = true
  }
}

function resetPay() {
  payDone.value = false
  paySuccess.value = false
  failMsg.value = ''
  selectedMethod.value = ''
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
  display: flex;
  align-items: center;
  justify-content: center;
}
.pay-select { text-align: center; }
.hint { font-size: 16px; color: #888; margin: 0 0 8px; }
.amount { font-size: 40px; font-weight: 700; color: #e74c3c; margin: 0 0 36px; }
.method-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  max-width: 420px;
}
.method-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 28px 16px;
  background: #fff;
  border: 2px solid #eef0f4;
  border-radius: 16px;
  cursor: pointer;
  transition: .2s;
  font-family: inherit;
}
.method-card:hover { border-color: #2563eb; box-shadow: 0 4px 16px rgba(37,99,235,.1); }
.method-card:disabled { opacity: .5; cursor: not-allowed; }
.method-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
}
.method-name { font-size: 16px; font-weight: 500; color: #222; }
.processing { color: #2563eb; font-size: 14px; margin-top: 20px; }
.pay-result { text-align: center; }
.result-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px;
  box-shadow: 0 4px 24px rgba(0,0,0,.06);
  max-width: 400px;
  width: 100%;
}
.icon-wrap {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
}
.icon-wrap.success { background: #ecfdf5; color: #10b981; }
.icon-wrap.fail { background: #fef2f2; color: #ef4444; }
.result-title { font-size: 24px; font-weight: 600; color: #222; margin: 0 0 8px; }
.result-amount { font-size: 36px; font-weight: 700; color: #e74c3c; margin: 0 0 4px; }
.result-method { font-size: 14px; color: #888; margin: 0 0 28px; }
.result-msg { font-size: 15px; color: #888; margin: 0 0 28px; }
.actions { display: flex; gap: 12px; justify-content: center; }
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
.btn-primary { background: #2563eb; color: #fff; }
.btn-primary:hover { background: #1d4ed8; }
.btn-outline { background: #fff; color: #444; border: 1px solid #d0d5dd; }
.btn-outline:hover { background: #f8f9fa; }
</style>
