import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/identity',
    name: 'Identity',
    component: () => import('../views/identity/IdentityVerify.vue')
  },
  {
    path: '/registration',
    name: 'Registration',
    component: () => import('../views/registration/DepartmentList.vue')
  },
  {
    path: '/registration/doctor',
    name: 'DoctorList',
    component: () => import('../views/registration/DoctorList.vue')
  },
  {
    path: '/registration/schedule',
    name: 'ScheduleDetail',
    component: () => import('../views/registration/ScheduleDetail.vue')
  },
  {
    path: '/registration/confirm',
    name: 'RegConfirm',
    component: () => import('../views/registration/RegConfirm.vue')
  },
  {
    path: '/registration/result',
    name: 'RegResult',
    component: () => import('../views/registration/RegResult.vue')
  },
  {
    path: '/outpatient',
    name: 'OutpatientPay',
    component: () => import('../views/payment/OutpatientPay.vue')
  },
  {
    path: '/inpatient',
    name: 'InpatientDeposit',
    component: () => import('../views/payment/InpatientDeposit.vue')
  },
  {
    path: '/outpatient-detail',
    name: 'OutpatientDetail',
    component: () => import('../views/query/OutpatientDetail.vue')
  },
  {
    path: '/inpatient-record',
    name: 'InpatientRecord',
    component: () => import('../views/query/InpatientRecord.vue')
  },
  {
    path: '/reg-record',
    name: 'RegRecord',
    component: () => import('../views/query/RegRecord.vue')
  },
  {
    path: '/drug',
    name: 'DrugPrice',
    component: () => import('../views/query/DrugPrice.vue')
  },
  {
    path: '/queue',
    name: 'QueueInfo',
    component: () => import('../views/query/QueueInfo.vue')
  },
  {
    path: '/payment/select',
    name: 'PaymentSelect',
    component: () => import('../views/payment/PaymentSelect.vue')
  },
  {
    path: '/signin',
    name: 'SignIn',
    component: () => import('../views/signin/SignIn.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
