import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userId = ref<number | null>(null)
  const username = ref('')
  const realName = ref('')
  const role = ref('')
  const token = ref('')

  function setLogin(data: { userId: number; username: string; realName: string; role: string; token: string }) {
    userId.value = data.userId
    username.value = data.username
    realName.value = data.realName
    role.value = data.role
    token.value = data.token
    localStorage.setItem('token', data.token)
  }

  function logout() {
    userId.value = null
    username.value = ''
    realName.value = ''
    role.value = ''
    token.value = ''
    localStorage.removeItem('token')
  }

  return { userId, username, realName, role, token, setLogin, logout }
})
