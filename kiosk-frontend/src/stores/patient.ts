import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePatientStore = defineStore('patient', () => {
  const patientId = ref<number | null>(null)
  const patientName = ref('')
  const medicalNo = ref('')

  function setPatient(data: { id: number; name: string; medicalNo: string }) {
    patientId.value = data.id
    patientName.value = data.name
    medicalNo.value = data.medicalNo
  }

  function clearPatient() {
    patientId.value = null
    patientName.value = ''
    medicalNo.value = ''
  }

  return { patientId, patientName, medicalNo, setPatient, clearPatient }
})
