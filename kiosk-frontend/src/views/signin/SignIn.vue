<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2 class="page-title">就诊签到</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body centered">
      <div class="signin-card">
        <div class="signin-icon">
          <el-icon :size="40"><Checked /></el-icon>
        </div>
        <h3>{{ mode === 'reenter' ? '过号重签' : '请输入挂号号签到' }}</h3>
        <p class="signin-desc">{{ mode === 'reenter' ? '您有已过号的挂号记录，重新排入队列' : '输入挂号凭条上的挂号号进行签到' }}</p>
        <el-input v-model="regNo" placeholder="挂号号" size="large" class="signin-input" />
        <el-button type="primary" size="large" round class="signin-btn" @click="doSignIn" :loading="loading">
          签到
        </el-button>
        <el-button v-if="hasSkippedRecord" text type="warning" class="reenter-link" @click="switchToReenter">
          ⚠ 我有过号记录，点此重签
        </el-button>
      </div>

      <div v-if="queueInfo" class="queue-result-card">
        <div class="result-icon success"><el-icon :size="32"><CircleCheckFilled /></el-icon></div>
        <p class="result-label">{{ resultLabel }}</p>
        <p v-if="isRevisit" class="result-hint">您为复诊患者，已优先排位，请关注叫号</p>
        <div class="result-detail">
          <div class="detail-item">
            <span>排队号</span>
            <span class="highlight">{{ queueInfo.queueNo }}</span>
          </div>
          <div class="detail-item">
            <span>前面等待</span>
            <span class="highlight">{{ queueInfo.waitCount }}人</span>
          </div>
        </div>
        <el-button type="primary" round class="signin-btn" @click="$router.push('/queue')">查看排队进度</el-button>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { usePatientStore } from '../../stores/patient'
import { ElMessage } from 'element-plus'

const patientStore = usePatientStore()
const regNo = ref('')
const loading = ref(false)
const queueInfo = ref<any>(null)
const hasSkippedRecord = ref(false)
const isRevisit = ref(false)
const mode = ref<'signin' | 'reenter'>('signin')
const resultLabel = ref('签到成功')

async function doSignIn() {
  if (!regNo.value) { ElMessage.warning('请输入挂号号'); return }
  loading.value = true
  try {
    const endpoint = mode.value === 'reenter' ? '/kiosk/queue/reenter' : '/kiosk/queue/signin'
    const res: any = await request.post(endpoint, { patientId: patientStore.patientId, regNo: regNo.value })
    queueInfo.value = res.data
    isRevisit.value = res.data?.type === 'REVISIT'
    resultLabel.value = mode.value === 'reenter' ? '重签成功' : '签到成功'
    ElMessage.success(resultLabel.value)
  } finally { loading.value = false }
}

function switchToReenter() {
  mode.value = 'reenter'
  hasSkippedRecord.value = false
}

onMounted(() => {
  if (patientStore.patientId) {
    request.get(`/kiosk/registration/records`, { params: { patientId: patientStore.patientId } }).then((res: any) => {
      const records = res.data || []
      hasSkippedRecord.value = records.some((r: any) => r.status === 'REFUNDED')
    }).catch(() => {})
  }
})
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; display: flex; align-items: center; justify-content: center; flex-direction: column; gap: 20px; padding: 32px; }
.signin-card { background: white; border-radius: 20px; padding: 40px; text-align: center; box-shadow: var(--shadow); max-width: 460px; width: 100%; }
.signin-icon { width: 72px; height: 72px; border-radius: 50%; background: var(--primary-bg); color: var(--primary); display: inline-flex; align-items: center; justify-content: center; margin-bottom: 16px; }
.signin-card h3 { font-size: 22px; font-weight: 600; color: var(--text); margin-bottom: 6px; }
.signin-desc { font-size: 14px; color: var(--text-light); margin-bottom: 24px; }
.signin-input { max-width: 320px; margin: 0 auto 12px; }
.signin-btn { width: 200px; height: 48px; font-size: 16px; margin-top: 8px; }
.reenter-link { display: block; margin-top: 8px; }
.queue-result-card { background: white; border-radius: 20px; padding: 36px; text-align: center; box-shadow: var(--shadow); max-width: 400px; width: 100%; }
.result-icon { width: 56px; height: 56px; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 10px; }
.result-icon.success { background: #ecfdf5; color: var(--success); }
.result-label { font-size: 18px; font-weight: 600; color: var(--text); margin-bottom: 4px; }
.result-hint { font-size: 13px; color: var(--warning); margin-bottom: 16px; }
.result-detail { text-align: left; border: 1px solid var(--border); border-radius: 10px; overflow: hidden; }
.detail-item { display: flex; justify-content: space-between; padding: 12px 16px; border-bottom: 1px solid var(--border); font-size: 15px; }
.detail-item:last-child { border-bottom: none; }
.detail-item span:first-child { color: var(--text-secondary); }
.highlight { font-weight: 700; color: var(--primary); }
</style>
