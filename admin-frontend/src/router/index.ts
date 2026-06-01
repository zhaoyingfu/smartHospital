import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/dashboard/Dashboard.vue'), meta: { title: '首页概览' } },
      { path: 'transaction', name: 'Transaction', component: () => import('../views/transaction/TransactionList.vue'), meta: { title: '交易流水' } },
      { path: 'transaction/:id', name: 'TransactionDetail', component: () => import('../views/transaction/TransactionDetail.vue'), meta: { title: '交易详情', hidden: true } },
      { path: 'recon', name: 'Recon', component: () => import('../views/recon/ReconRecord.vue'), meta: { title: '对账管理' } },
      { path: 'recon/detail', name: 'ReconDetail', component: () => import('../views/recon/ReconDetail.vue'), meta: { title: '对账明细', hidden: true } },
      { path: 'recon/tickets', name: 'ReconTickets', component: () => import('../views/recon/ReconTicket.vue'), meta: { title: '差异工单' } },
      { path: 'refund', name: 'Refund', component: () => import('../views/refund/RefundList.vue'), meta: { title: '退款管理' } },
      { path: 'settlement', name: 'Settlement', component: () => import('../views/settlement/SettlementList.vue'), meta: { title: '日终结算' } },
      { path: 'kiosk', name: 'Kiosk', component: () => import('../views/kiosk/KioskList.vue'), meta: { title: '设备管理' } },
      { path: 'rule', name: 'Rule', component: () => import('../views/rule/RuleConfig.vue'), meta: { title: '规则配置' } },
      { path: 'blacklist', name: 'Blacklist', component: () => import('../views/blacklist/BlacklistManage.vue'), meta: { title: '黑名单' } },
      { path: 'anomaly', name: 'Anomaly', component: () => import('../views/anomaly/AnomalyList.vue'), meta: { title: '异常监控' } },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  if (to.path !== '/login' && !localStorage.getItem('token')) {
    next('/login')
  } else {
    next()
  }
})

export default router
