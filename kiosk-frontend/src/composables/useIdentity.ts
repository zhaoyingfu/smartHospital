import { ref } from 'vue'
import request from '../utils/request'
import { usePatientStore } from '../stores/patient'

export function useIdentity() {
  const loading = ref(false)
  const patientStore = usePatientStore()

  async function verify(type: string, value: string) {
    loading.value = true
    try {
      const payload: Record<string, string> = { verifyType: type }
      if (type === 'idCard') payload.idCard = value
      else if (type === 'medicareCard') payload.medicareCard = value
      else if (type === 'phone') payload.phone = value

      const res: any = await request.post('/kiosk/patient/verify', payload)
      if (res.data) {
        patientStore.setPatient({
          id: res.data.id,
          name: res.data.name,
          medicalNo: res.data.medicalNo
        })
        return true
      }
      return false
    } finally {
      loading.value = false
    }
  }

  return { loading, verify }
}
