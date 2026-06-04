<template>
  <div>
    <el-card>
      <div class="filter-bar">
        <el-select v-model="deptId" placeholder="选择科室" @change="onDeptChange">
          <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
        </el-select>
        <el-select v-model="doctorId" placeholder="选择医生" :disabled="!deptId">
          <el-option v-for="d in doctors" :key="d.id" :label="d.name + ' ' + (d.title||'')" :value="d.id" />
        </el-select>
        <el-button type="primary" @click="loadPanel" :disabled="!deptId || !doctorId">查询队列</el-button>
        <el-tag v-if="wsConnected" type="success" size="small">实时连接</el-tag>
        <el-tag v-else type="info" size="small">轮询模式</el-tag>
      </div>
    </el-card>

    <el-card v-if="panel" style="margin-top: 16px">
      <template #header>
        <span>当前叫号</span>
      </template>
      <div class="current-call" v-if="panel.currentCalling">
        <span class="call-no">{{ panel.currentCalling.queueNo }}</span>
        <span class="call-name">{{ panel.currentCalling.patientName || '---' }}</span>
        <span class="call-type">{{ typeLabel(panel.currentCalling.type) }}</span>
        <div class="call-actions">
          <el-button type="warning" @click="recall(panel.currentCalling.id)">复叫</el-button>
          <el-button type="danger" @click="skip(panel.currentCalling.id)">过号</el-button>
          <el-button type="success" @click="startDiag(panel.currentCalling.id)">开始就诊</el-button>
        </div>
      </div>
      <div v-else class="current-call empty">--- 暂无叫号 ---</div>
    </el-card>

    <el-card v-if="panel" style="margin-top: 16px; position: relative">
      <template #header>
        <span>等待列表 (共 {{ panel.totalWaiting }} 人)</span>
        <el-button size="small" type="primary" style="float:right" @click="callNext">呼叫下一个</el-button>
      </template>
      <el-table :data="panel.waitingList" stripe v-loading="loading">
        <el-table-column prop="rank" label="序号" width="60">
          <template #default="{ row }">{{ row.rank + 1 }}</template>
        </el-table-column>
        <el-table-column prop="queueNo" label="排队号" width="100" />
        <el-table-column prop="patientName" label="姓名" width="120" />
        <el-table-column prop="regNo" label="挂号号" width="180" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'REVISIT' ? 'warning' : ''" size="small">{{ typeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="callNext">呼叫</el-button>
            <el-button size="small" type="danger" @click="skip(row.id)">过号</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import request from '../../utils/request'

const departments = ref<any[]>([])
const doctors = ref<any[]>([])
const deptId = ref<number | null>(null)
const doctorId = ref<number | null>(null)
const panel = ref<any>(null)
const loading = ref(false)
const wsConnected = ref(false)

let stompClient: any = null
let pollTimer: ReturnType<typeof setInterval> | null = null

async function loadDepts() {
  try {
    const res: any = await request.get('/kiosk/registration/departments')
    departments.value = res.data || []
  } catch (e) {
    console.error('loadDepts failed:', e)
  }
}

async function loadDoctors() {
  if (!deptId.value) return
  try {
    const res: any = await request.get('/kiosk/registration/doctors', { params: { departmentId: deptId.value } })
    doctors.value = res.data || []
  } catch (e) {
    console.error('loadDoctors failed:', e)
  }
}

function onDeptChange() {
  doctorId.value = null
  panel.value = null
  disconnectWs()
  loadDoctors()
}

async function loadPanel() {
  if (!deptId.value || !doctorId.value) return
  loading.value = true
  try {
    const res: any = await request.get('/admin/queue/panel', { params: { departmentId: deptId.value, doctorId: doctorId.value } })
    panel.value = res.data
  } catch (e) {
    console.error('loadPanel failed:', e)
  } finally { loading.value = false }
}

async function callNext() {
  if (!deptId.value || !doctorId.value) return
  await request.post('/admin/queue/call-next', null, { params: { departmentId: deptId.value, doctorId: doctorId.value } })
  await loadPanel()
}

async function recall(id: number) {
  await request.post(`/admin/queue/recall/${id}`)
  await loadPanel()
}

async function skip(id: number) {
  await request.post(`/admin/queue/skip/${id}`)
  await loadPanel()
}

async function startDiag(id: number) {
  await request.post(`/admin/queue/start/${id}`)
  await loadPanel()
}

function typeLabel(t: string) {
  const m: Record<string, string> = { FIRST_VISIT: '初诊', REVISIT: '复诊', EMERGENCY: '急诊' }
  return m[t] || t
}

function statusLabel(s: string) {
  const m: Record<string, string> = { WAITING: '等待', CALLING: '呼叫中', SEEING: '就诊中', DONE: '完成', SKIPPED: '过号' }
  return m[s] || s
}

async function connectWebSocket() {
  try {
    const [{ Client }, SockJSModule] = await Promise.all([
      import('@stomp/stompjs'),
      import('sockjs-client')
    ])
    const SockJS = (SockJSModule as any).default || SockJSModule

    stompClient = new Client({
      webSocketFactory: () => new SockJS('/api/ws-stomp'),
      reconnectDelay: 5000,
      onConnect: () => {
        wsConnected.value = true
        if (deptId.value && doctorId.value) {
          stompClient!.subscribe(`/topic/queue/${deptId.value}/${doctorId.value}`, () => {
            loadPanel()
          })
        }
      },
      onDisconnect: () => { wsConnected.value = false },
      onStompError: () => { wsConnected.value = false }
    })
    stompClient.activate()
  } catch (e) {
    console.warn('WebSocket not available, falling back to polling:', e)
    startPolling()
  }
}

function startPolling() {
  if (pollTimer) return
  pollTimer = setInterval(() => {
    if (deptId.value && doctorId.value) loadPanel()
  }, 5000)
}

function disconnectWs() {
  try { stompClient?.deactivate() } catch {}
  stompClient = null
  wsConnected.value = false
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null }
}

onMounted(() => {
  loadDepts()
  connectWebSocket()
})

onUnmounted(() => {
  disconnectWs()
})
</script>

<style scoped>
.filter-bar { display: flex; gap: 12px; align-items: center; }
.current-call { display: flex; align-items: center; gap: 20px; font-size: 18px; }
.current-call.empty { color: #999; font-size: 16px; justify-content: center; }
.call-no { font-size: 48px; font-weight: 800; color: #2563eb; letter-spacing: 2px; }
.call-name { font-size: 24px; font-weight: 600; color: #222; }
.call-type { font-size: 14px; }
.call-actions { margin-left: auto; display: flex; gap: 8px; }
</style>
