import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useKioskStore } from '../stores/kiosk'
import { usePatientStore } from '../stores/patient'

const IDLE_SECONDS = 60

export function useTimeout() {
  const router = useRouter()
  const kioskStore = useKioskStore()
  const patientStore = usePatientStore()
  let timer: ReturnType<typeof setInterval> | null = null

  function resetTimer() {
    kioskStore.updateActive()
  }

  function checkTimeout() {
    const now = new Date()
    const diff = (now.getTime() - kioskStore.lastActiveTime.getTime()) / 1000
    if (diff >= IDLE_SECONDS && router.currentRoute.value.path !== '/') {
      patientStore.clearPatient()
      router.push('/')
    }
  }

  onMounted(() => {
    timer = setInterval(checkTimeout, 1000)
    document.addEventListener('click', resetTimer)
    document.addEventListener('touchstart', resetTimer)
    document.addEventListener('keydown', resetTimer)
  })

  onUnmounted(() => {
    if (timer) clearInterval(timer)
    document.removeEventListener('click', resetTimer)
    document.removeEventListener('touchstart', resetTimer)
    document.removeEventListener('keydown', resetTimer)
  })
}
