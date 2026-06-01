<template>
  <div class="page">
    <header class="page-header">
      <el-button text class="back-btn" @click="$router.push('/')">
        <el-icon :size="20"><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2 class="page-title">身份验证</h2>
      <div class="page-spacer"></div>
    </header>
    <main class="page-body centered">
      <div class="verify-card">
        <div class="verify-icon">
          <el-icon :size="48"><UserFilled /></el-icon>
        </div>
        <h3 class="verify-title">请选择验证方式</h3>
        <p class="verify-desc">请选择以下任一方式验证您的身份</p>
        <div class="verify-methods">
          <button class="method-btn" style="--btn-color: #2563eb" @click="doVerify('idCard')">
            <el-icon :size="32"><Postcard /></el-icon>
            <span>刷身份证</span>
            <small>将身份证放在感应区</small>
          </button>
          <button class="method-btn" style="--btn-color: #059669" @click="doVerify('medicareCard')">
            <el-icon :size="32"><Stamp /></el-icon>
            <span>医保卡</span>
            <small>插入医保卡</small>
          </button>
          <button class="method-btn" style="--btn-color: #d97706" @click="showPhoneInput = true">
            <el-icon :size="32"><Iphone /></el-icon>
            <span>手机号</span>
            <small>输入预留手机号</small>
          </button>
        </div>
      </div>

      <el-dialog v-model="showPhoneInput" title="验证手机号" width="420px" top="25vh" :close-on-click-modal="false">
        <div class="phone-dialog">
          <el-input v-model="phone" placeholder="请输入手机号" size="large" maxlength="11" :prefix-icon="'Iphone'" />
        </div>
        <template #footer>
          <el-button @click="showPhoneInput = false">取消</el-button>
          <el-button type="primary" @click="doVerify('phone')" size="large">确认</el-button>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useIdentity } from '../../composables/useIdentity'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const { verify } = useIdentity()
const showPhoneInput = ref(false)
const phone = ref('')

async function doVerify(type: string) {
  const value = type === 'phone' ? phone.value : 'MOCK_' + type.toUpperCase()
  const success = await verify(type, value)
  if (success) {
    ElMessage.success('验证成功')
    const next = (route.query.next as string) || 'registration'
    router.push(`/${next}`)
  } else {
    ElMessage.error('验证失败，请重试')
  }
}
</script>

<style scoped>
.page {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.page-header {
  display: flex;
  align-items: center;
  padding: 20px 32px;
  background: white;
  border-bottom: 1px solid var(--border);
}

.back-btn { font-size: 15px; }

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text);
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

.verify-card {
  background: white;
  border-radius: 20px;
  padding: 56px 48px;
  text-align: center;
  box-shadow: var(--shadow);
  max-width: 720px;
  width: 100%;
}

.verify-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--primary-bg);
  color: var(--primary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.verify-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}

.verify-desc {
  color: var(--text-secondary);
  font-size: 15px;
  margin-bottom: 40px;
}

.verify-methods {
  display: flex;
  gap: 24px;
  justify-content: center;
}

.method-btn {
  --btn-color: #2563eb;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 180px;
  padding: 32px 20px;
  background: white;
  border: 2px solid var(--border);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text);
  font-size: 18px;
  font-weight: 500;
  font-family: inherit;
}

.method-btn:hover {
  border-color: var(--btn-color);
  box-shadow: 0 4px 16px color-mix(in srgb, var(--btn-color) 20%, transparent);
  transform: translateY(-2px);
}

.method-btn:active {
  transform: translateY(0);
}

.method-btn el-icon {
  color: var(--btn-color);
}

.method-btn small {
  font-size: 12px;
  color: var(--text-light);
  font-weight: 400;
}

.method-btn span {
  color: var(--text);
}

.phone-dialog { padding: 8px 0; }
</style>
