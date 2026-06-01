<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.back()">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2 class="page-title">{{ departmentName }}</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body">
      <div class="section-label">选择医生</div>
      <div class="doctor-list">
        <article
          v-for="doc in doctors"
          :key="doc.id"
          class="doctor-card"
          @click="selectDoctor(doc)"
        >
          <div class="doc-avatar" :style="{ background: doc.color || '#2563eb' }">
            {{ doc.name[0] }}
          </div>
          <div class="doc-info">
            <div class="doc-name">{{ doc.name }}</div>
            <div class="doc-title">{{ doc.title }}</div>
          </div>
          <div class="doc-action">
            <el-button type="primary" size="large" round>挂号</el-button>
          </div>
        </article>
        <div v-if="doctors.length === 0" class="empty-state">
          <el-empty description="暂无排班医生" />
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()
const departmentId = route.query.departmentId as string
const departmentName = (route.query.departmentName as string) || ''
const doctors = ref<any[]>([])
const docColors = ['#2563eb', '#059669', '#d97706', '#7c3aed', '#0891b2', '#dc2626']

async function loadDoctors() {
  const res: any = await request.get('/kiosk/registration/doctors', { params: { departmentId } })
  doctors.value = (res.data || []).map((d: any, i: number) => ({ ...d, color: docColors[i % docColors.length] }))
}

function selectDoctor(doc: any) {
  router.push({
    path: '/registration/schedule',
    query: { doctorId: doc.id, doctorName: doc.name, departmentId, departmentName }
  })
}

onMounted(loadDoctors)
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; padding: 28px 32px; overflow-y: auto; }

.section-label {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
  padding-left: 4px;
}

.doctor-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.doctor-card {
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

.doctor-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-lg);
}

.doc-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 22px;
  font-weight: 600;
  flex-shrink: 0;
}

.doc-info { flex: 1; }

.doc-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.doc-title {
  font-size: 14px;
  color: var(--text-secondary);
}

.doc-action { flex-shrink: 0; }

.empty-state { padding: 60px 0; }
</style>
