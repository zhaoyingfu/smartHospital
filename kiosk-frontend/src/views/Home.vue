<template>
  <div class="home">
    <header class="home-header">
      <div class="header-left">
        <div class="logo-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 12h-4l-3 9L9 3l-3 9H2"/>
          </svg>
        </div>
        <div>
          <h1 class="hospital-name">智慧医院</h1>
          <p class="hospital-sub">自助服务系统</p>
        </div>
      </div>
      <div class="header-right">
        <div class="date-display">{{ currentDate }}</div>
        <div class="time-display">{{ currentTime }}</div>
      </div>
    </header>

    <main class="home-main">
      <div class="grid">
        <button
          v-for="item in menuItems"
          :key="item.path"
          class="grid-card"
          :style="{ '--card-color': item.color }"
          @click="navigate(item.path)"
        >
          <div class="card-icon">
            <el-icon :size="36"><component :is="item.icon" /></el-icon>
          </div>
          <span class="card-label">{{ item.label }}</span>
        </button>
      </div>
    </main>

    <footer class="home-footer">
      <span>如需帮助请咨询导医台  ·  请触摸屏幕操作</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { usePatientStore } from '../stores/patient'

const router = useRouter()
const patientStore = usePatientStore()
const currentTime = ref('')
const currentDate = ref('')

const menuItems = [
  { label: '当日挂号', icon: 'Calendar', path: '/registration', color: '#2563eb' },
  { label: '预约挂号', icon: 'Clock', path: '/registration?type=appointment', color: '#059669' },
  { label: '门诊缴费', icon: 'Wallet', path: '/outpatient', color: '#d97706' },
  { label: '住院充值', icon: 'CreditCard', path: '/inpatient', color: '#dc2626' },
  { label: '挂号记录', icon: 'Document', path: '/reg-record', color: '#6366f1' },
  { label: '缴费查询', icon: 'Search', path: '/outpatient-detail', color: '#0891b2' },
  { label: '住院查询', icon: 'List', path: '/inpatient-record', color: '#7c3aed' },
  { label: '药品查询', icon: 'Box', path: '/drug', color: '#0d9488' },
  { label: '排队签到', icon: 'Checked', path: '/signin', color: '#ea580c' }
]

let timer: ReturnType<typeof setInterval> | null = null

function updateClock() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
}

function navigate(path: string) {
  const needsIdentity = ['/registration', '/outpatient', '/inpatient', '/signin']
  const base = path.split('?')[0]
  if (needsIdentity.includes(base)) {
    const type = path.includes('appointment') ? 'appointment' : base.replace('/', '')
    router.push(`/identity?next=${type}`)
  } else {
    router.push(path)
  }
}

onMounted(() => {
  updateClock()
  timer = setInterval(updateClock, 1000)
  patientStore.clearPatient()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.home {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 40px 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.hospital-name {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
  letter-spacing: 2px;
}

.hospital-sub {
  font-size: 14px;
  color: var(--text-light);
  letter-spacing: 4px;
}

.header-right {
  text-align: right;
}

.date-display {
  font-size: 16px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.time-display {
  font-size: 32px;
  font-weight: 700;
  color: var(--text);
  font-variant-numeric: tabular-nums;
  letter-spacing: 2px;
}

.home-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 40px 20px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  max-width: 900px;
  width: 100%;
}

.grid-card {
  --card-color: #2563eb;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  height: 180px;
  background: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  position: relative;
  overflow: hidden;
}

.grid-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--card-color);
  transition: height 0.25s ease;
}

.grid-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0,0,0,0.1);
}

.grid-card:hover::before {
  height: 6px;
}

.grid-card:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--card-color);
  background: color-mix(in srgb, var(--card-color) 10%, white);
  transition: transform 0.25s ease;
}

.grid-card:hover .card-icon {
  transform: scale(1.1);
}

.card-label {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
}

.home-footer {
  text-align: center;
  padding: 16px 0 24px;
  color: var(--text-light);
  font-size: 14px;
  letter-spacing: 1px;
}
</style>
