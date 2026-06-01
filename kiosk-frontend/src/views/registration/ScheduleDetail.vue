<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="page-title">{{ doctorName }} - 选择号源</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="schedule-list">
        <div
          v-for="s in schedules"
          :key="s.id"
          class="schedule-card"
          :class="{ disabled: s.availableQuota <= 0 }"
          @click="selectSchedule(s)"
        >
          <div class="schedule-date">
            <div class="date-day">{{ formatDay(s.scheduleDate) }}</div>
            <div class="date-week">{{ formatWeekday(s.scheduleDate) }}</div>
          </div>
          <div class="schedule-period">
            <span class="period-badge" :class="s.timePeriod === 'AM' ? 'morning' : s.timePeriod === 'PM' ? 'afternoon' : 'evening'">
              {{ s.timePeriod === 'AM' ? '上午' : s.timePeriod === 'PM' ? '下午' : '晚间' }}
            </span>
          </div>
          <div class="schedule-fee">
            <span class="fee-amount">¥{{ formatAmount(s.regFee + s.treatFee) }}</span>
            <span class="fee-label">挂号费</span>
          </div>
          <div class="schedule-quota">
            <span class="quota-num" :class="{ low: s.availableQuota <= 5, full: s.availableQuota <= 0 }">
              {{ s.availableQuota <= 0 ? '已满' : s.availableQuota + ' 号' }}
            </span>
          </div>
          <div class="schedule-action">
            <el-button
              type="primary"
              :disabled="s.availableQuota <= 0"
              size="large"
              round
            >
              {{ s.availableQuota <= 0 ? '已约满' : '预约' }}
            </el-button>
          </div>
        </div>
        <div v-if="schedules.length === 0" class="empty-state">
          <el-empty description="暂无可用号源" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../../utils/request'
import { formatAmount } from '../../utils/format'

const router = useRouter()
const route = useRoute()
const doctorId = route.query.doctorId as string
const doctorName = (route.query.doctorName as string) || ''
const departmentId = route.query.departmentId as string
const schedules = ref<any[]>([])

function formatDay(date: string) {
  return date ? date.slice(8) : ''
}

function formatWeekday(date: string) {
  if (!date) return ''
  const days = ['日', '一', '二', '三', '四', '五', '六']
  const d = new Date(date)
  return `周${days[d.getDay()]}`
}

async function loadSchedules() {
  const res: any = await request.get('/kiosk/registration/schedules', {
    params: { departmentId, doctorId }
  })
  schedules.value = res.data || []
}

function selectSchedule(s: any) {
  if (s.availableQuota <= 0) return
  router.push({
    path: '/registration/confirm',
    query: {
      scheduleId: s.id,
      doctorName,
      departmentName: route.query.departmentName as string,
      date: s.scheduleDate,
      period: s.timePeriod === 'AM' ? '上午' : s.timePeriod === 'PM' ? '下午' : '晚间',
      fee: s.regFee + s.treatFee
    }
  })
}

onMounted(loadSchedules)
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; padding: 28px 32px; overflow-y: auto; }

.schedule-list { display: flex; flex-direction: column; gap: 12px; }

.schedule-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 24px;
  background: white;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow);
}

.schedule-card:hover:not(.disabled) {
  transform: translateX(4px);
  box-shadow: var(--shadow-lg);
}

.schedule-card.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.schedule-date { text-align: center; width: 60px; flex-shrink: 0; }
.date-day { font-size: 28px; font-weight: 700; color: var(--text); line-height: 1.1; }
.date-week { font-size: 13px; color: var(--text-light); margin-top: 2px; }

.period-badge {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}
.period-badge.morning { background: #eff6ff; color: #2563eb; }
.period-badge.afternoon { background: #fef3c7; color: #d97706; }
.period-badge.evening { background: #e0e7ff; color: #4338ca; }

.schedule-fee { flex: 1; }
.fee-amount { font-size: 20px; font-weight: 700; color: var(--text); display: block; }
.fee-label { font-size: 12px; color: var(--text-light); }

.quota-num { font-size: 18px; font-weight: 600; }
.quota-num.low { color: var(--warning); }
.quota-num.full { color: var(--danger); }

.schedule-action { flex-shrink: 0; }
.empty-state { padding: 60px 0; }
</style>
