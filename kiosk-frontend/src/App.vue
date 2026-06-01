<template>
  <div class="app-shell">
    <div v-if="patientStore.patientName" class="patient-bar">
      <div class="patient-bar-inner">
        <div class="patient-avatar">{{ patientStore.patientName[0] }}</div>
        <div class="patient-info">
          <span class="patient-name">{{ patientStore.patientName }}</span>
          <span class="patient-no">{{ patientStore.medicalNo }}</span>
        </div>
        <el-button text class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出
        </el-button>
      </div>
    </div>
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useTimeout } from './composables/useTimeout'
import { usePatientStore } from './stores/patient'

const router = useRouter()
const patientStore = usePatientStore()
useTimeout()

function handleLogout() {
  patientStore.clearPatient()
  router.push('/')
}
</script>

<style scoped>
.app-shell {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.patient-bar {
  background: white;
  border-bottom: 1px solid var(--border);
  padding: 0 32px;
  flex-shrink: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
}

.patient-bar-inner {
  display: flex;
  align-items: center;
  gap: 14px;
  height: 60px;
}

.patient-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  flex-shrink: 0;
}

.patient-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.patient-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text);
  line-height: 1.3;
}

.patient-no {
  font-size: 12px;
  color: var(--text-light);
  line-height: 1.3;
}

.logout-btn {
  color: var(--text-light);
  font-size: 13px;
}
.logout-btn:hover {
  color: var(--danger);
}
</style>
