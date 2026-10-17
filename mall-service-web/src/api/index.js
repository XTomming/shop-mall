import request from './request'

// 认证
export const login = (data) => request.post('/service/login', data)
export const setOnline = (online) => request.put('/service/online', null, { params: { online } })
export const registerServiceUser = (data) => request.post('/service/user/register', data)

// 会话
export const getConversationList = () => request.get('/service/conversation/list')
export const claimConversation = (id) => request.put(`/service/conversation/${id}/claim`)
export const getConversationMessages = (id) => request.get(`/service/conversation/${id}/message`)
export const sendConversationMessage = (id, data) => request.post(`/service/conversation/${id}/message`, data)
export const endConversation = (id) => request.put(`/service/conversation/${id}/end`)

// 售后单
export const getAfterSalePage = (params) => request.get('/service/after-sale/page', { params })
export const acceptAfterSale = (id) => request.put(`/service/after-sale/${id}/accept`)
export const completeAfterSale = (id, data) => request.put(`/service/after-sale/${id}/complete`, data)
export const rejectAfterSale = (id, data) => request.put(`/service/after-sale/${id}/reject`, data)
