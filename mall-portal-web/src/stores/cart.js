import { defineStore } from 'pinia'
import { getCartList } from '@/api'

export const useCartStore = defineStore('cart', {
  state: () => ({
    count: 0
  }),
  actions: {
    // 刷新购物车角标数量
    async refreshCount() {
      const token = sessionStorage.getItem('token')
      if (!token) {
        this.count = 0
        return
      }
      try {
        const list = await getCartList()
        this.count = (list || []).length
      } catch (e) {
        this.count = 0
      }
    }
  }
})
