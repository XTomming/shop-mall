import request from './request'

// 认证
export const login = (data) => request.post('/admin/login', data)

// 统计
export const getDashboard = () => request.get('/admin/dashboard')

// 商品
export const getProductPage = (params) => request.get('/admin/product/page', { params })
export const getProductDetail = (id) => request.get(`/admin/product/${id}`)
export const addProduct = (data) => request.post('/admin/product', data)
export const updateProduct = (id, data) => request.put(`/admin/product/${id}`, data)
export const deleteProduct = (id) => request.delete(`/admin/product/${id}`)
export const updateProductStatus = (id, status) => request.put(`/admin/product/${id}/status`, null, { params: { status } })

// 分类
export const getCategoryList = () => request.get('/admin/category/list')
export const addCategory = (data) => request.post('/admin/category', data)
export const updateCategory = (id, data) => request.put(`/admin/category/${id}`, data)
export const deleteCategory = (id) => request.delete(`/admin/category/${id}`)

// 订单
export const getOrderPage = (params) => request.get('/admin/order/page', { params })
export const getOrderDetail = (id) => request.get(`/admin/order/${id}`)
export const deliveryOrder = (id) => request.put(`/admin/order/${id}/delivery`)
export const closeOrder = (id) => request.put(`/admin/order/${id}/close`)

// 会员
export const getMemberPage = (params) => request.get('/admin/member/page', { params })
export const getMemberDetail = (id) => request.get(`/admin/member/${id}`)
export const updateMemberStatus = (id, status) => request.put(`/admin/member/${id}/status`, null, { params: { status } })

// 轮播图
export const getBannerPage = (params) => request.get('/admin/banner/page', { params })
export const addBanner = (data) => request.post('/admin/banner', data)
export const updateBanner = (id, data) => request.put(`/admin/banner/${id}`, data)
export const deleteBanner = (id) => request.delete(`/admin/banner/${id}`)

// 公告
export const getNoticePage = (params) => request.get('/admin/notice/page', { params })
export const addNotice = (data) => request.post('/admin/notice', data)
export const updateNotice = (id, data) => request.put(`/admin/notice/${id}`, data)
export const deleteNotice = (id) => request.delete(`/admin/notice/${id}`)

// 评论
export const getCommentPage = (params) => request.get('/admin/comment/page', { params })
export const deleteComment = (id) => request.delete(`/admin/comment/${id}`)

// 系统管理 - 管理员
export const getSysUserPage = (params) => request.get('/admin/sys/user/page', { params })
export const addSysUser = (data) => request.post('/admin/sys/user', data)
export const updateSysUser = (id, data) => request.put(`/admin/sys/user/${id}`, data)
export const deleteSysUser = (id) => request.delete(`/admin/sys/user/${id}`)
export const updateSysUserStatus = (id, status) => request.put(`/admin/sys/user/${id}/status`, null, { params: { status } })

// 系统管理 - 角色
export const getSysRoleList = () => request.get('/admin/sys/role/list')
export const addSysRole = (data) => request.post('/admin/sys/role', data)
export const updateSysRole = (id, data) => request.put(`/admin/sys/role/${id}`, data)
export const deleteSysRole = (id) => request.delete(`/admin/sys/role/${id}`)

// 秒杀 - 场次
export const getSeckillActivityPage = (params) => request.get('/admin/seckill/activity/page', { params })
export const addSeckillActivity = (data) => request.post('/admin/seckill/activity', data)
export const updateSeckillActivity = (id, data) => request.put(`/admin/seckill/activity/${id}`, data)
export const deleteSeckillActivity = (id) => request.delete(`/admin/seckill/activity/${id}`)
// 秒杀 - 商品
export const getSeckillItemPage = (params) => request.get('/admin/seckill/item/page', { params })
export const addSeckillItem = (data) => request.post('/admin/seckill/item', data)
export const addSeckillItemBatch = (data) => request.post('/admin/seckill/item/batch', data)
export const updateSeckillItem = (id, data) => request.put(`/admin/seckill/item/${id}`, data)
export const deleteSeckillItem = (id) => request.delete(`/admin/seckill/item/${id}`)

// 上传
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  // 不要手动设置 Content-Type，让浏览器自动带上 multipart boundary
  return request.post('/admin/upload', formData)
}

// 售后
export const getAfterSalePage = (params) => request.get('/admin/after-sale/page', { params })
export const getAfterSaleStat = () => request.get('/admin/after-sale/stat')

// 客服管理
export const getServiceUserPage = (params) => request.get('/admin/service-user/page', { params })
export const addServiceUser = (data) => request.post('/admin/service-user', data)
export const updateServiceUser = (id, data) => request.put(`/admin/service-user/${id}`, data)
export const deleteServiceUser = (id) => request.delete(`/admin/service-user/${id}`)
export const updateServiceUserStatus = (id, status) => request.put(`/admin/service-user/${id}/status`, null, { params: { status } })
