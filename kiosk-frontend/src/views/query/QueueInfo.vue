<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2 class="page-title">排队叫号</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body centered">
      <div v-if="!regNo && !queueInfo" class="query-card">
        <div class="icon-wrap"><el-icon :size="40"><Search /></el-icon></div>
        <h3>查询排队进度</h3>
        <p class="hint">输入挂号号码</p>
        <el-input v-model="queryRegNo" placeholder="挂号号" size="large" class="query-input" maxlength="30" />
        <el-button type="primary" size="large" round @click="queryQueue" :loading="loading">查询</el-button>
      </div>

      <template v-else>
        <div class="status-card">
          <div class="current-section">
            <span class="section-label">当前叫号</span>
            <div class="current-no">{{ currentCallNo || '---' }}</div>
            <div class="current-name">{{ currentCallName || '' }}</div>
          </div>
          <div class="divider"></div>
          <div class="my-section">
            <span class="section-label">我的排队号</span>
            <div class="my-no">{{ myQueueNo }}</div>
            <div class="my-wait">
              前面还有 <span class="wait-num">{{ myWaitCount }}</span> 人等候
            </div>
          </div>
        </div>
        <el-button round class="back-home-btn" @click="$router.push('/')">返回首页</el-button>
      </template>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../../utils/request'
import { usePatientStore } from '../../stores/patient'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { ElMessage } from 'element-plus'

const router = useRouter()
const patientStore = usePatientStore()

const queryRegNo = ref('')
const loading = ref(false)
const regNo = ref('')
const currentCallNo = ref('')
const currentCallName = ref('')
const myQueueNo = ref('')
const myWaitCount = ref(0)

let deptId: number | null = null
let doctorId: number | null = null
let stompClient: Client | null = null

async function queryQueue() {
  if (!queryRegNo.value) { ElMessage.warning('请输入挂号号'); return }
  loading.value = true
  try {
    const res: any = await request.get('/kiosk/registration/records', {
      params: { patientId: patientStore.patientId }
    })
    const records: any[] = res.data || []
    const match = records.find((r: any) => r.regNo === queryRegNo.value)
    if (!match) { ElMessage.warning('未找到该挂号记录'); return }

    regNo.value = queryRegNo.value
    deptId = match.departmentId
    doctorId = match.doctorId
    myQueueNo.value = match.queueNo || '---'
    await refreshStatus()
    connectWebSocket()
  } finally { loading.value = false }
}

async function refreshStatus() {
  if (!deptId || !doctorId) return
  try {
    const res: any = await request.get('/kiosk/queue/status', { params: { departmentId: deptId, doctorId } })
    if (res.data) {
      if (res.data.currentCalling) {
        currentCallNo.value = res.data.currentCalling.queueNo || '---'
        currentCallName.value = res.data.currentCalling.patientName || ''
      }
      const mine = (res.data.waitingList || []).find((w: any) => w.queueNo === myQueueNo.value)
      myWaitCount.value = mine ? mine.rank + 1 : 0
    }
  } catch {}
}

function connectWebSocket() {
  if (!deptId || !doctorId) return
  stompClient = new Client({
    webSocketFactory: () => new SockJS('/api/ws-stomp'),
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient!.subscribe(`/topic/queue/${deptId}/${doctorId}`, () => {
        refreshStatus()
      })
    }
  })
  stompClient.activate()
}

onUnmounted(() => {
  stompClient?.deactivate()
})
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: #f5f7fa; }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid #eef0f4; }
.back-btn { font-size: 15px; color: #666; }
.page-title { font-size: 20px; font-weight: 600; color: #222; text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 24px; padding: 32px; }

.query-card { background: white; border-radius: 20px; padding: 44px 36px; text-align: center; box-shadow: 0 4px 24px rgba(0,0,0,.06); max-width: 420px; width: 100%; }
.icon-wrap { width: 72px; height: 72px; border-radius: 50%; background: #eff6ff; color: #2563eb; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 12px; }
.query-card h3 { font-size: 22px; font-weight: 600; color: #222; margin: 0 0 4px; }
.hint { font-size: 14px; color: #888; margin: 0 0 20px; }
.query-input { max-width: 300px; margin: 0 auto 16px; }

.status-card { background: white; border-radius: 24px; padding: 40px 60px; text-align: center; box-shadow: 0 4px 24px rgba(0,0,0,.06); display: flex; gap: 40px; align-items: center; }
.section-label { display: block; font-size: 16px; color: #888; margin-bottom: 8px; }
.current-section { flex: 1; }
.current-no { font-size: 56px; font-weight: 800; color: #2563eb; letter-spacing: 4px; line-height: 1.1; }
.current-name { font-size: 18px; color: #666; margin-top: 4px; }
.divider { width: 1px; height: 100px; background: #eef0f4; }
.my-section { flex: 1; }
.my-no { font-size: 40px; font-weight: 700; color: #222; line-height: 1.1; }
.my-wait { font-size: 16px; color: #888; margin-top: 8px; }
.wait-num { font-size: 28px; font-weight: 700; color: #e74c3c; }
.back-home-btn { min-width: 160px; }
</style>
