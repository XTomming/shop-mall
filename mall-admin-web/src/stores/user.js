import { defineStore } from 'pinia'
import { login as apiLogin } from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: sessionStorage.getItem('token') || '',
    userInfo: JSON.parse(sessionStorage.getItem('userInfo') || 'null')
  }),
  getters: {
    nickname: (state) => state.userInfo?.nickname || state.userInfo?.username || '管理员'
  },
  actions: {
    async login(loginForm) {
      const data = await apiLogin(loginForm)
      this.token = data.token
      this.userInfo = data
      sessionStorage.setItem('token', data.token)
      sessionStorage.setItem('userInfo', JSON.stringify(data))
      return data
    },
    logout() {
      this.token = ''
      this.userInfo = null
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('userInfo')
    }
  }
})
