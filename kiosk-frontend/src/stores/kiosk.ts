import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useKioskStore = defineStore('kiosk', () => {
  const kioskId = ref<number>(1)
  const lastActiveTime = ref<Date>(new Date())

  function updateActive() {
    lastActiveTime.value = new Date()
  }

  return { kioskId, lastActiveTime, updateActive }
})
