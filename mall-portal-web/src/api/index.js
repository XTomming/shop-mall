import request from './request'

// 认证
export const register = (data) => request.post('/portal/register', data)
export const login = (data) => request.post('/portal/login', data)

// 首页 / 分类 / 商品
export const getHome = () => request.get('/portal/home')
export const getCategoryList = () => request.get('/portal/category/list')
export const getProductPage = (params) => request.get('/portal/product/page', { params })
export const getProductDetail = (id) => request.get(`/portal/product/${id}`)
export const getComments = (productId) => request.get('/portal/comment/list', { params: { productId } })

// 购物车
export const getCartList = () => request.get('/portal/cart/list')
export const addCart = (data) => request.post('/portal/cart', data)
export const updateCart = (id, data) => request.put(`/portal/cart/${id}`, data)
export const deleteCart = (id) => request.delete(`/portal/cart/${id}`)

// 订单 / 支付
export const submitOrder = (data) => request.post('/portal/order', data)
export const getOrderList = (params) => request.get('/portal/order/list', { params })
export const getOrderDetail = (id) => request.get(`/portal/order/${id}`)
export const receiveOrder = (id) => request.put(`/portal/order/${id}/receive`)
export const mockPay = (data) => request.post('/portal/pay/mock', data)

// 评价
export const publishComment = (data) => request.post('/portal/comment', data)

// 个人中心
export const getProfile = () => request.get('/portal/member/profile')
export const updateProfile = (data) => request.put('/portal/member/profile', data)
export const updatePassword = (data) => request.put('/portal/member/password', data)

// 收货地址
export const getAddressList = () => request.get('/portal/address/list')
export const addAddress = (data) => request.post('/portal/address', data)
export const updateAddress = (id, data) => request.put(`/portal/address/${id}`, data)
export const deleteAddress = (id) => request.delete(`/portal/address/${id}`)

// 秒杀
export const getSeckillList = () => request.get('/portal/seckill/list')
export const seckillBuy = (itemId) => request.post(`/portal/seckill/${itemId}/buy`)

// 售后
export const getAfterSaleList = (params) => request.get('/portal/after-sale/list', { params })
export const applyRefund = (data) => request.post('/portal/after-sale/refund', data)
export const applyInvoice = (data) => request.post('/portal/after-sale/invoice', data)
export const escalateAfterSale = (id) => request.post(`/portal/after-sale/${id}/escalate`)
export const cancelAfterSale = (id) => request.put(`/portal/after-sale/${id}/cancel`)

// 客服会话
export const createConversation = (data) => request.post('/portal/conversation', data)
export const getConversationList = () => request.get('/portal/conversation/list')
export const getConversationMessages = (id) => request.get(`/portal/conversation/${id}/message`)
export const sendConversationMessage = (id, data) => request.post(`/portal/conversation/${id}/message`, data)

// 上传（售后凭证图）
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/portal/upload', formData)
}
