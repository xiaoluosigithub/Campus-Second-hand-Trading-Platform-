import { defineStore } from 'pinia'
import * as AuthApi from '../api/auth'
import * as UsersApi from '../api/users'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    nickname: '',
    role: 'user',
    avatarUrl: '',
    initialized: false,
  }),
  actions: {
    setUser(payload) {
      this.userId = payload.userId
      this.nickname = payload.nickname
      this.role = payload.role
      this.avatarUrl = payload.avatarUrl || ''
      this.initialized = true
    },
    clear() {
      this.userId = null
      this.nickname = ''
      this.role = 'user'
      this.avatarUrl = ''
      this.initialized = false
    },
    async getMe() {
      try {
        const res = await UsersApi.getMe()
        if (res?.data) this.setUser(res.data)
        else this.initialized = true
      } catch {
        this.initialized = true
      }
    },
    async login(payload) {
      const res = await AuthApi.login(payload)
      if (res?.data) this.setUser(res.data)
      return res
    },
    async logout() {
      await AuthApi.logout()
      this.clear()
    },
  },
})
