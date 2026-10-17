# mall 电商系统 项目总结

> 项目类型：前后端分离电商系统
> 架构演进：单体架构 → 微服务架构（Spring Cloud Alibaba）

---

## 一、项目简介

mall 是一个前后端分离的 PC 端电商系统，包含**前台商城、后台管理、客服工作台**三个前端，覆盖从商品浏览、购物车、下单、支付到售后退款、在线客服的完整电商闭环。

项目采用**两阶段演进**：
1. **单体版**：Spring Boot 单体应用 + Redis 缓存 + RabbitMQ 消息队列；
2. **微服务版**：拆分为网关 + 5 个业务服务，引入 Nacos 注册/配置中心、OpenFeign、Sentinel、Seata，实现服务化。

## 二、系统角色与端

| 端 | 技术 | 用户 | 职责 |
|---|---|---|---|
| 前台商城 `mall-portal-web` | Vue3 | 会员 | 浏览/搜索/购物车/下单/支付/售后/聊天 |
| 后台管理 `mall-admin-web` | Vue3 | 管理员 | 商品/订单/会员/售后/客服管理 + 统计 |
| 客服工作台 `mall-service-web` | Vue3 | 人工客服 | 会话接待 + 售后单办结 |

## 三、技术栈

**后端**
- 语言/框架：Java 、Spring Boot 
- 微服务：Spring Cloud Alibaba（Nacos、OpenFeign、Sentinel、Seata）、Spring Cloud Gateway
- ORM：MyBatis-Plus
- 数据库/中间件：MySQL 8、Redis、RabbitMQ
- 安全：JWT、Spring Security、BCrypt
- 通信：REST、WebSocket（STOMP）
- 其他：Hutool、Knife4j、Lombok

**前端**
- Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios + ECharts + @stomp/stompjs

## 四、系统架构

### 单体版

```
三个前端 → Spring Boot 单体(controller/service/mapper)
             → MySQL / Redis(缓存) / RabbitMQ(订单超时)
```

### 微服务版

```
mall-cloud（6 模块）
├── mall-common            公共：Result/枚举/JWT/用户上下文/异常处理
├── mall-gateway  :8080    路由 + JWT鉴权 + 请求头传递 + WebSocket转发
├── mall-user-service :8101  会员/登录/地址/系统管理
├── mall-product-service :8102 商品/分类
├── mall-order-service :8103   购物车/订单/支付
└── mall-after-sale-service :8104 售后 + 客服聊天(WebSocket)
        ↓ 全部注册到 Nacos，通过 OpenFeign 跨服务调用
```

## 五、核心功能模块

| 模块 | 功能点 |
|---|---|
| 商品 | 分类树、搜索、价格/销量排序、详情缓存、上下架 |
| 购物车 | 加购、改数量、选中、删除 |
| 订单 | 提交、模拟支付、发货、确认收货、超时自动取消 |
| 秒杀 | 场次管理、秒杀商品管理、限时抢购、Redis 预减库存、限购、异步下单削峰 |
| 售后 | 退款(整单仅退款)、发票(号码自动生成唯一)、纠纷(退款被拒升级) |
| 客服 | 在线聊天(WebSocket)、自动分配最闲客服、发图片/商品/订单卡片 |
| 系统管理 | 管理员/客服账号增删改查、角色 |

## 六、核心技术细节

### 1. 认证与鉴权
- 登录签发 JWT（含 userId/username/role）。
- **微服务版**：网关统一校验 JWT，把用户信息写入 `X-User-Id`/`X-Username`/`X-User-Role` 请求头传给下游；各服务用公共拦截器解析到 ThreadLocal，`SecurityUtils` 读取。
- 密码 BCrypt 加密，密钥由 Nacos 配置中心统一管理。

### 2. 缓存与消息队列
- Redis 缓存商品详情、首页数据、分类树，更新后删除缓存保证一致性。
- RabbitMQ 延迟队列实现「待付款订单超时自动取消 + 恢复库存」。

### 3. 实时聊天（WebSocket + STOMP）
- 写消息走 REST（受鉴权、校验参与者），读消息走 WebSocket 订阅 `/topic/conversation/{id}` 实时推送。
- 会员发起会话自动分配给「在线且最闲」的客服；无在线客服则挂「待接入」池。
- 消息支持文本/图片/商品卡片/订单卡片（订单卡片含明细快照、带状态）。

### 4. 售后退款（跨服务事务）
- 退款办结横跨 3 个服务：`after-sale → order(改订单状态) → product(恢复库存)`。
- 微服务版用 **Seata AT 模式 + `@GlobalTransactional`** 保证一致性，`undo_log` 表做回滚日志。

### 5. 限流与配置中心
- Sentinel 对商品服务接口做 QPS 限流。
- Nacos 配置中心集中管理共享配置（如 JWT 密钥）。

### 6. 秒杀商城（高并发设计）

秒杀作为独立模块，用「场次 + 秒杀商品」两层建模，核心解决高并发下的**超卖**与**重复抢购**：

1. **库存预热**：活动开始前把秒杀库存预热到 Redis（`seckill:stock:{itemId}`）。
2. **原子预减库存**：抢购时用 Redis `decrement` 原子扣减，扣到负数即判定售罄并回滚，从源头防止超卖。
3. **限购去重**：Redis `setIfAbsent` 记录「同一用户同一商品只能抢一次」（`seckill:user:{itemId}:{memberId}`），天然防重复抢购。
4. **异步下单（削峰）**：抢购成功后不直接落库，而是发 RabbitMQ 消息异步创建订单（`SeckillOrderProducer` → `SeckillOrderConsumer`），抢购接口快速返回，数据库写入被削峰；MQ 关闭时同步兜底。
5. **超时回补**：秒杀单 5 分钟未支付自动关闭，回补 Redis 秒杀库存并释放限购，避免库存被长时间锁定。
6. **降级容错**：Redis 异常时库存展示降级为数据库值，扣减/回补均做 try-catch 容错，保证 Redis 宕机不拖垮秒杀主流程。

## 七、项目优化点

1. **热点数据缓存**：商品详情/首页/分类用 Redis 缓存，降低数据库压力，更新后删缓存保证一致。
2. **异步解耦**：订单超时取消用 RabbitMQ 延迟队列，替代轮询定时任务。
3. **实时通信**：客服聊天用 WebSocket 推送，替代轮询，消息即时到达。
4. **无状态认证**：JWT + 网关统一鉴权，服务无状态、易横向扩展。
5. **服务化拆分**：按业务域拆分为 5 个服务，高内聚低耦合，可独立部署扩展。
6. **分布式一致性**：Seata 全局事务解决跨服务下单/退款的一致性。
7. **限流熔断**：Sentinel 保护核心接口，防止突发流量打垮服务。
8. **配置集中管理**：Nacos 配置中心，改一处、处处生效。
9. **数据唯一性**：发票号时间戳+随机+唯一索引三重保证。
10. **前端体验**：登录态用 sessionStorage 按标签页隔离，支持多账号同屏调试。
11. **客服负载均衡**：自动分配最闲客服，均衡会话负载。
12. **秒杀防超卖**：Redis 原子预减库存 + 限购去重 + MQ 异步下单削峰，多管齐下保证秒杀高并发下的数据一致性。

---
