// 格式化金额，保留两位小数
export function formatPrice(v) {
  return Number(v ?? 0).toFixed(2)
}

// 商品图片占位符（后端暂无图片时使用）
export const PLACEHOLDER_IMG = 'data:image/svg+xml;utf8,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="200" height="200"><rect width="100%" height="100%" fill="#f0f0f0"/><text x="50%" y="50%" fill="#bbb" font-size="14" text-anchor="middle" dominant-baseline="middle">暂无图片</text></svg>'
)
