<template>
  <div class="page">
    <header class="page-header">
      <h2 class="page-title">挂号结果</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body centered">
      <div class="card">
        <div class="icon-wrap">
          <el-icon :size="44"><CircleCheckFilled /></el-icon>
        </div>
        <h3 class="title">挂号成功</h3>
        <p class="no">号条编号: {{ regNo }}</p>
        <div class="detail">
          <div class="row"><span>科室</span><span>{{ departmentName }}</span></div>
          <div class="row"><span>医生</span><span>{{ doctorName }}</span></div>
          <div class="row"><span>日期</span><span>{{ date }}</span></div>
          <div class="row"><span>时段</span><span>{{ period }}</span></div>
          <div class="row fee"><span>费用</span><span>¥{{ formatAmount(fee) }}</span></div>
        </div>
        <div class="actions">
          <button class="btn btn-primary" @click="goPay">去支付</button>
          <button class="btn btn-outline" @click="$router.push('/')">返回首页</button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { formatAmount } from '../../utils/format'

const route = useRoute()
const router = useRouter()
const regNo = route.query.regNo as string
const doctorName = route.query.doctorName as string
const departmentName = route.query.departmentName as string
const date = route.query.date as string
const period = route.query.period as string
const fee = Number(route.query.fee)
const paymentOrderId = route.query.paymentOrderId as string

function goPay() {
  router.push({ path: '/payment/select', query: { paymentOrderId, fee: String(fee), regNo } })
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
  padding: 44px 36px;
  text-align: center;
  box-shadow: 0 4px 24px rgba(0,0,0,.06);
  max-width: 440px;
  width: 100%;
}
.icon-wrap {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #ecfdf5;
  color: #10b981;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}
.title {
  font-size: 22px;
  font-weight: 600;
  color: #222;
  margin: 0 0 6px;
}
.no {
  font-size: 14px;
  color: #888;
  margin: 0 0 24px;
}
.detail {
  text-align: left;
  border: 1px solid #eef0f4;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}
.row {
  display: flex;
  justify-content: space-between;
  padding: 12px 18px;
  border-bottom: 1px solid #eef0f4;
  font-size: 14px;
}
.row:last-child { border-bottom: none; }
.row span:first-child { color: #888; }
.row span:last-child { font-weight: 500; color: #222; }
.row.fee span:last-child { color: #e74c3c; font-weight: 600; }
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
.btn-primary { background: #2563eb; color: #fff; }
.btn-primary:hover { background: #1d4ed8; }
.btn-outline { background: #fff; color: #444; border: 1px solid #d0d5dd; }
.btn-outline:hover { background: #f8f9fa; }
</style>
