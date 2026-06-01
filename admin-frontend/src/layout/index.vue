<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">智慧医院管理后台</div>
      <el-menu
        :default-active="$route.path"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>首页概览</span>
        </el-menu-item>
        <el-menu-item index="/transaction">
          <el-icon><List /></el-icon>
          <span>交易流水</span>
        </el-menu-item>
        <el-sub-menu index="recon-group">
          <template #title><el-icon><Connection /></el-icon><span>对账管理</span></template>
          <el-menu-item index="/recon">对账记录</el-menu-item>
          <el-menu-item index="/recon/tickets">差异工单</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/refund">
          <el-icon><RefreshLeft /></el-icon>
          <span>退款管理</span>
        </el-menu-item>
        <el-menu-item index="/settlement">
          <el-icon><Wallet /></el-icon>
          <span>日终结算</span>
        </el-menu-item>
        <el-menu-item index="/kiosk">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item index="/rule">
          <el-icon><Setting /></el-icon>
          <span>规则配置</span>
        </el-menu-item>
        <el-menu-item index="/blacklist">
          <el-icon><Warning /></el-icon>
          <span>黑名单</span>
        </el-menu-item>
        <el-menu-item index="/anomaly">
          <el-icon><AlarmClock /></el-icon>
          <span>异常监控</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ $route.meta.title || '' }}</span>
        <div class="header-right">
          <span>{{ userStore.realName }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container { height: 100%; }
.sidebar { background: #304156; overflow-y: auto; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: white; font-size: 18px; font-weight: bold; white-space: nowrap; }
.sidebar-menu { border-right: none; }
.header { display: flex; justify-content: space-between; align-items: center; background: white; box-shadow: 0 1px 4px rgba(0,0,0,0.1); font-size: 18px; font-weight: bold; }
.header-right { display: flex; align-items: center; gap: 16px; font-size: 14px; font-weight: normal; }
.main { background: #f0f2f5; }
</style>
