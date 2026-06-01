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
      <div class="queue-card">
        <div class="queue-current">
          <span class="queue-label">当前叫号</span>
          <span class="queue-number">{{ currentQueue || '---' }}</span>
        </div>
        <div class="queue-wait">
          <span class="wait-label">您前面还有</span>
          <span class="wait-number">{{ waitCount }}</span>
          <span class="wait-label">人等候</span>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const currentQueue = ref('')
const waitCount = ref(0)
</script>

<style scoped>
.page { width: 100%; height: 100%; display: flex; flex-direction: column; background: var(--bg); }
.page-header { display: flex; align-items: center; padding: 20px 32px; background: white; border-bottom: 1px solid var(--border); }
.back-btn { font-size: 15px; }
.page-title { font-size: 20px; font-weight: 600; color: var(--text); text-align: center; flex: 1; }
.page-spacer { width: 80px; }
.page-body { flex: 1; display: flex; align-items: center; justify-content: center; padding: 32px; }

.queue-card {
  background: white;
  border-radius: 24px;
  padding: 60px 80px;
  text-align: center;
  box-shadow: var(--shadow-lg);
}

.queue-current {
  margin-bottom: 40px;
}

.queue-label {
  display: block;
  font-size: 20px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.queue-number {
  font-size: 64px;
  font-weight: 800;
  color: var(--primary);
  letter-spacing: 4px;
}

.queue-wait {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 8px;
}

.wait-label {
  font-size: 18px;
  color: var(--text-secondary);
}

.wait-number {
  font-size: 48px;
  font-weight: 800;
  color: var(--text);
}
</style>
