# mall-cloud 微服务改造

电商单体 `shop-mall` 的 Spring Cloud Alibaba 微服务版本（学习练手）。设计见 `../docs/microservices.md`。

## 模块

| 模块 | 端口 | 说明 | 状态 |
|---|---|---|---|
| mall-common | — | 统一返回/异常/枚举/JWT/用户上下文 | ✅ |
| mall-gateway | 8080 | 路由 + JWT 鉴权 + 请求头传递 | ✅ 骨架 |
| mall-user-service | 8101 | 会员/登录/地址/系统管理 | 🚧 骨架（待补业务） |
| mall-product-service | 8102 | 商品/分类/评价/秒杀/内容 | 🚧 骨架（待补业务） |
| mall-order-service | 8103 | 购物车/订单/支付 | ⏳ M2 |
| mall-after-sale-service | 8104 | 售后/客服聊天 | ⏳ M3 |

## 启动

1. 启动 Nacos 单机（默认 `localhost:8848`）。
2. 确保 MySQL（`mall` 库）可用。
3. 依次启动：`mall-user-service`、`mall-product-service`、`mall-gateway`。
4. 三个前端把 `/api`、`/ws` 的代理目标改为网关 `http://localhost:8080`。

## M1 验收

- 网关鉴权：不带 token 访问受保护接口返回 401；带 token 放行并透传 `X-User-Id` 等头。
- 登录/商品接口经网关路由到对应服务。

## 说明

- 第一阶段共享一个 MySQL，不做分布式事务。
- 单体现有代码（`src/`、根 `pom.xml`）保留，作为迁移来源与兜底。
