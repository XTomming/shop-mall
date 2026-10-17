/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80041
 Source Host           : localhost:3306
 Source Schema         : mall

 Target Server Type    : MySQL
 Target Server Version : 80041
 File Encoding         : 65001

 Date: 17/10/2026 15:50:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for after_sale
-- ----------------------------
DROP TABLE IF EXISTS `after_sale`;
CREATE TABLE `after_sale`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `after_sale_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '售后单号',
  `type` tinyint NOT NULL COMMENT '类型：1退款，2发票，3纠纷',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '申请原因/描述',
  `refund_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '退款金额（退款单）',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '凭证图片，逗号分隔',
  `invoice_type` tinyint NULL DEFAULT NULL COMMENT '发票抬头类型：1个人，2公司',
  `invoice_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发票抬头',
  `tax_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '税号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接收邮箱',
  `invoice_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发票号码（开具后填写）',
  `source_refund_id` bigint NULL DEFAULT NULL COMMENT '纠纷来源退款单ID（纠纷单）',
  `service_id` bigint NULL DEFAULT NULL COMMENT '处理客服ID',
  `handle_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客服处理说明',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0待处理，1处理中，2已完成，3已拒绝，4已关闭',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_after_sale_no`(`after_sale_no` ASC) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '售后单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of after_sale
-- ----------------------------
INSERT INTO `after_sale` VALUES (1, 'AS202610171507171682', 1, 1, 9, '损坏', 44.00, '/upload/e7290b6ba43c495b818980e21b04d467.webp', NULL, NULL, NULL, NULL, NULL, NULL, 2, '', '2026-10-17 15:07:46', 2, '2026-10-17 15:07:18', '2026-10-17 15:07:46');
INSERT INTO `after_sale` VALUES (2, 'AS202610171542593226', 2, 3, 11, '', NULL, NULL, 1, '123', '', '123@qq.com', '3213123', NULL, 3, '', '2026-10-17 15:43:26', 2, '2026-10-17 15:42:59', '2026-10-17 15:43:26');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片',
  `link` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '1', '/upload/6060139c04fc475ea4b25ff443984db7.gif', 'https://s.taobao.com/', 1, 1, '2026-10-12 21:01:52', '2026-10-13 14:38:28');
INSERT INTO `banner` VALUES (2, '2', '/upload/249d728e11874002afb241f0d0cface8.gif', NULL, 2, 1, '2026-10-12 21:01:52', '2026-10-13 14:36:17');
INSERT INTO `banner` VALUES (3, '3', '/upload/9aa5ab50b33d402599917aa16d501fec.gif', NULL, 3, 1, '2026-10-12 21:01:52', '2026-10-13 14:38:20');

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `checked` tinyint NULL DEFAULT 1 COMMENT '是否选中：1选中，0未选中',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_member_product`(`member_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (8, 3, 13, 100, 0, '2026-10-13 14:56:39', '2026-10-17 15:41:24');
INSERT INTO `cart` VALUES (9, 3, 20, 20, 0, '2026-10-13 14:56:54', '2026-10-17 15:41:24');

-- ----------------------------
-- Table structure for conversation
-- ----------------------------
DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `service_id` bigint NULL DEFAULT NULL COMMENT '客服ID（自动分配后填充）',
  `product_id` bigint NULL DEFAULT NULL COMMENT '咨询的商品ID（可选）',
  `status` tinyint NULL DEFAULT 0 COMMENT '会话状态：0待接入，1进行中，2已结束',
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后一条消息摘要',
  `last_message_time` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE,
  INDEX `idx_service_id`(`service_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversation
-- ----------------------------
INSERT INTO `conversation` VALUES (1, 1, 2, NULL, 2, '3Q', '2026-10-17 15:10:29', '2026-10-17 15:01:02', '2026-10-17 15:10:31');
INSERT INTO `conversation` VALUES (2, 1, 2, NULL, 2, NULL, NULL, '2026-10-17 15:05:39', '2026-10-17 15:06:01');
INSERT INTO `conversation` VALUES (3, 1, 2, NULL, 2, NULL, NULL, '2026-10-17 15:05:44', '2026-10-17 15:06:04');
INSERT INTO `conversation` VALUES (4, 1, 2, NULL, 2, 'eee', '2026-10-17 15:39:30', '2026-10-17 15:24:59', '2026-10-17 15:39:33');
INSERT INTO `conversation` VALUES (5, 3, 3, NULL, 2, '好', '2026-10-17 15:39:45', '2026-10-17 15:39:04', '2026-10-17 15:39:49');
INSERT INTO `conversation` VALUES (6, 3, 2, NULL, 2, '可以的', '2026-10-17 15:41:50', '2026-10-17 15:41:37', '2026-10-17 15:42:04');

-- ----------------------------
-- Table structure for conversation_message
-- ----------------------------
DROP TABLE IF EXISTS `conversation_message`;
CREATE TABLE `conversation_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `sender_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发送方：MEMBER会员，SERVICE客服',
  `sender_id` bigint NOT NULL COMMENT '发送方ID',
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of conversation_message
-- ----------------------------
INSERT INTO `conversation_message` VALUES (1, 1, 'MEMBER', 1, '你好', '2026-10-17 15:01:15');
INSERT INTO `conversation_message` VALUES (2, 1, 'SERVICE', 2, 'hello', '2026-10-17 15:01:28');
INSERT INTO `conversation_message` VALUES (3, 1, 'SERVICE', 2, '123', '2026-10-17 15:02:03');
INSERT INTO `conversation_message` VALUES (4, 1, 'MEMBER', 1, '我想问一下这个商品', '2026-10-17 15:09:39');
INSERT INTO `conversation_message` VALUES (5, 1, 'SERVICE', 2, '123312', '2026-10-17 15:09:55');
INSERT INTO `conversation_message` VALUES (6, 1, 'MEMBER', 1, '好的没问题', '2026-10-17 15:10:13');
INSERT INTO `conversation_message` VALUES (7, 1, 'SERVICE', 2, '3Q', '2026-10-17 15:10:29');
INSERT INTO `conversation_message` VALUES (8, 4, 'MEMBER', 1, '1', '2026-10-17 15:25:07');
INSERT INTO `conversation_message` VALUES (9, 5, 'MEMBER', 3, '1111', '2026-10-17 15:39:22');
INSERT INTO `conversation_message` VALUES (10, 4, 'SERVICE', 2, 'eee', '2026-10-17 15:39:30');
INSERT INTO `conversation_message` VALUES (11, 5, 'SERVICE', 3, '好', '2026-10-17 15:39:45');
INSERT INTO `conversation_message` VALUES (12, 6, 'MEMBER', 3, '这个怎么样', '2026-10-17 15:41:42');
INSERT INTO `conversation_message` VALUES (13, 6, 'SERVICE', 2, '可以的', '2026-10-17 15:41:50');

-- ----------------------------
-- Table structure for member
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别：0未知，1男，2女',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '前台会员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member
-- ----------------------------
INSERT INTO `member` VALUES (1, 'Tom', '$2a$10$lTP0E7Z9XpPtErRxXVdiNezOdirkF7ILYzm/YpWKoZRLdk5j/MtWa', '汤姆', '19512312312', '123456@qq.com', NULL, 1, 1, '2026-10-12 21:55:24', '2026-10-12 22:11:20');
INSERT INTO `member` VALUES (2, 'admin', '$2a$10$SoRorV7kLwKwnYkLUHMzi.sBCqXnTvCrklP.vfsgM3rzs7x8ZXtE6', '超级管理员', '', NULL, NULL, 0, 1, '2026-10-12 22:06:53', '2026-10-12 22:11:20');
INSERT INTO `member` VALUES (3, 'aaa', '$2a$10$N2/Mm4f6UcxoDaXFoHImtOmjfGpeA3wCuOOx57aCi7ivkZk5vI5S2', 'aaa', '12311111111', NULL, NULL, 0, 1, '2026-10-13 13:43:58', '2026-10-13 13:43:58');

-- ----------------------------
-- Table structure for member_address
-- ----------------------------
DROP TABLE IF EXISTS `member_address`;
CREATE TABLE `member_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '区',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认：1是，0否',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of member_address
-- ----------------------------
INSERT INTO `member_address` VALUES (1, 1, 'Tom', '19807891793', '广西', '北海市', '银海区', '南珠大道9号', 1, '2026-10-12 22:03:29', '2026-10-12 22:04:01');
INSERT INTO `member_address` VALUES (2, 1, 'Tom', '19807891222', '广东', '广州', '白云', 'XXXXXXXXXXXXXXXXXXXX', 0, '2026-10-12 23:29:41', '2026-10-12 23:29:56');
INSERT INTO `member_address` VALUES (3, 3, 'aaa', '1223123213', '', '', '', '北京天安门', 1, '2026-10-13 13:56:43', '2026-10-13 13:56:44');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1发布，0隐藏',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '欢迎使用 mall 电商系统', '本系统覆盖商品浏览、购物车、下单、模拟支付、订单超时自动取消等核心功能。', 1, '2026-10-12 21:01:52', '2026-10-12 21:02:28');
INSERT INTO `notice` VALUES (2, '系统上线公告', '系统支持 Redis 缓存与 RabbitMQ 延迟队列。', 1, '2026-10-12 21:01:52', '2026-10-12 23:36:00');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL COMMENT '数量',
  `total_price` decimal(10, 2) NOT NULL COMMENT '小计',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 2, '某品牌中端手机', NULL, 2299.00, 1, 2299.00, '2026-10-12 22:04:14');
INSERT INTO `order_item` VALUES (2, 2, 11, '乐事薯片混合口味 520g', NULL, 39.90, 1, 39.90, '2026-10-13 13:56:52');
INSERT INTO `order_item` VALUES (3, 3, 2, 'OPPO Find X8 Ultra', '/upload/025f807eec264f6db273e61ba0dfcf32.webp', 2299.00, 10, 22990.00, '2026-10-13 14:54:15');
INSERT INTO `order_item` VALUES (4, 4, 6, '美拉德棕色翻领夹克', '/upload/3ac8276518ca4cd0a686f1a086db556d.webp', 98.44, 8, 787.52, '2026-10-15 21:52:23');
INSERT INTO `order_item` VALUES (5, 4, 1, 'iphone 18 pro', '/upload/70cf57af26ee4be2a0d7fda298e7596a.jpg', 9988.90, 1, 9988.90, '2026-10-15 21:52:23');
INSERT INTO `order_item` VALUES (6, 4, 3, 'XPS13Air', '/upload/8bd550aea7814d6d90d467d20551118d.jpg', 8110.94, 3, 24332.82, '2026-10-15 21:52:23');
INSERT INTO `order_item` VALUES (7, 4, 9, '松下白月光5.0洗烘套装洗衣机', '/upload/5f13d388bca9458cb48662411cc0cb35.webp', 1899.00, 9, 17091.00, '2026-10-15 21:52:23');
INSERT INTO `order_item` VALUES (8, 4, 13, '海天金标生抽 1.9L', '/upload/98cb2fe6800141749a64672c0e40902c.webp', 19.90, 1, 19.90, '2026-10-15 21:52:23');
INSERT INTO `order_item` VALUES (9, 5, 17, '迪卡侬登山杖 轻量铝合金', '/upload/aa5abbe617924c3f8f3d18c5d57a071c.webp', 9.90, 1, 9.90, '2026-10-15 22:42:09');
INSERT INTO `order_item` VALUES (10, 6, 25, '双立人刀具套装 6件套', '/upload/0fd30c079c744b27a93b0ef87e7e0800.webp', 9.90, 1, 9.90, '2026-10-15 22:42:22');
INSERT INTO `order_item` VALUES (11, 7, 17, '迪卡侬登山杖 轻量铝合金', '/upload/aa5abbe617924c3f8f3d18c5d57a071c.webp', 5.00, 1, 5.00, '2026-10-15 22:42:24');
INSERT INTO `order_item` VALUES (12, 8, 26, '《活着》余华 精装版', '/upload/563e3681c52241b5a0ddb4bc6fc4badf.webp', 0.00, 1, 0.00, '2026-10-15 22:42:24');
INSERT INTO `order_item` VALUES (13, 9, 27, '《三体》全集 1-3 刘慈欣', '/upload/281430cb7f2f44bca507239aff9f009c.webp', 44.00, 1, 44.00, '2026-10-15 22:42:25');
INSERT INTO `order_item` VALUES (14, 10, 25, '双立人刀具套装 6件套', '/upload/0fd30c079c744b27a93b0ef87e7e0800.webp', 9.90, 1, 9.90, '2026-10-15 22:46:03');
INSERT INTO `order_item` VALUES (15, 11, 7, '法式茶歇白色系带V领腰带镂空连衣裙', '/upload/cd390196a63548e7b4c9adbba2cf881d.webp', 199.00, 1, 199.00, '2026-10-17 15:41:30');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `pay_amount` decimal(10, 2) NOT NULL COMMENT '实付金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '订单状态：0待付款，1待发货，2待收货，3已完成，4已关闭',
  `pay_type` tinyint NULL DEFAULT NULL COMMENT '支付方式：1支付宝，2微信，3模拟支付',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '收货时间',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货电话',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `seckill_item_id` bigint NULL DEFAULT NULL COMMENT '秒杀商品ID，普通单为NULL',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_seckill_item`(`seckill_item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, '202610122204146473', 1, 2299.00, 2299.00, 3, 3, '2026-10-12 22:04:16', '2026-10-12 23:25:33', '2026-10-12 23:25:41', 'Tom', '19807891793', '广西北海市银海区南珠大道9号', '2026-10-12 22:04:14', '2026-10-12 23:25:41', NULL);
INSERT INTO `orders` VALUES (2, '202610131356522079', 3, 39.90, 39.90, 4, NULL, NULL, NULL, NULL, 'aaa', '1223123213', '北京天安门', '2026-10-13 13:56:52', '2026-10-13 14:11:52', NULL);
INSERT INTO `orders` VALUES (3, '202610131454144056', 3, 22990.00, 22990.00, 3, 3, '2026-10-13 14:54:16', '2026-10-13 14:54:39', '2026-10-13 14:55:14', 'aaa', '1223123213', '北京天安门', '2026-10-13 14:54:15', '2026-10-13 14:55:14', NULL);
INSERT INTO `orders` VALUES (4, '202610152152235847', 1, 52220.14, 52220.14, 3, 3, '2026-10-15 21:52:25', '2026-10-15 21:52:32', '2026-10-15 21:52:42', 'Tom', '19807891222', '广东广州白云XXXXXXXXXXXXXXXXXXXX', '2026-10-15 21:52:23', '2026-10-15 21:52:42', NULL);
INSERT INTO `orders` VALUES (5, '202610152242099777', 1, 9.90, 9.90, 3, 3, '2026-10-15 22:42:33', '2026-10-15 22:43:59', '2026-10-15 22:44:17', 'Tom', '19807891793', '广西北海市银海区南珠大道9号', '2026-10-15 22:42:09', '2026-10-15 22:44:17', 1);
INSERT INTO `orders` VALUES (6, '202610152242222063', 1, 9.90, 9.90, 4, NULL, NULL, NULL, NULL, 'Tom', '19807891793', '广西北海市银海区南珠大道9号', '2026-10-15 22:42:22', '2026-10-15 22:47:22', 2);
INSERT INTO `orders` VALUES (7, '202610152242237435', 1, 5.00, 5.00, 3, 3, '2026-10-15 22:42:35', '2026-10-15 22:43:55', '2026-10-15 22:44:10', 'Tom', '19807891793', '广西北海市银海区南珠大道9号', '2026-10-15 22:42:24', '2026-10-15 22:44:10', 3);
INSERT INTO `orders` VALUES (8, '202610152242248907', 1, 0.00, 0.00, 4, NULL, NULL, NULL, NULL, 'Tom', '19807891793', '广西北海市银海区南珠大道9号', '2026-10-15 22:42:24', '2026-10-15 22:43:57', 7);
INSERT INTO `orders` VALUES (9, '202610152242240407', 1, 44.00, 44.00, 5, 3, '2026-10-15 22:42:37', '2026-10-15 22:43:53', '2026-10-15 22:44:08', 'Tom', '19807891793', '广西北海市银海区南珠大道9号', '2026-10-15 22:42:25', '2026-10-17 15:07:46', 8);
INSERT INTO `orders` VALUES (10, '202610152246025272', 3, 9.90, 9.90, 4, NULL, NULL, NULL, NULL, 'aaa', '1223123213', '北京天安门', '2026-10-15 22:46:03', '2026-10-17 13:56:44', 2);
INSERT INTO `orders` VALUES (11, '202610171541292764', 3, 199.00, 199.00, 3, 3, '2026-10-17 15:42:32', '2026-10-17 15:45:08', '2026-10-17 15:45:37', 'aaa', '1223123213', '北京天安门', '2026-10-17 15:41:30', '2026-10-17 15:45:37', NULL);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '副标题',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主图',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `sales` int NULL DEFAULT 0 COMMENT '销量',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品详情',
  `status` tinyint NULL DEFAULT 1 COMMENT '上架状态：1上架，0下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_name`(`name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 5, 'iphone 18 pro', 'Apple/苹果【需签收激活】/苹果iPhone 18 Pro 256GB 银色 支持移动联通电信5G支持eSIM', '/upload/70cf57af26ee4be2a0d7fda298e7596a.jpg', 9988.90, 11999.00, 99, 24, '主摄像素\n4800万\n上市时间\n2026-09\n售后服务\n全国联保\n是否支持无线充电\n是\n耳机插头类型\nTYPE-C\n操作系统\niOS\n套餐类型\n官方标配 套餐一\n前置摄像头像素\n18百万像素\n电池容量\n5000mAh\n解锁方式\n面部识别\n存储容量\n512GB 1TB 2TB 256GB\n无线充电功率\n15W\n电信设备进网许可证编号\n00-8573-268498\n机身颜色\n银色 黑色 勃艮第酒红色 冰川蓝色\n品牌\nApple/苹果\nApple型号\niPhone 18 Pro\n是否支持NFC\n是\nCPU品牌\nApple/苹果\nCPU核心数\n六核\n最大光圈\nF1.48\n屏幕材质\n超视网膜 XDR 显示屏\n接口类型\nType-C\n蓝牙版本\n6.0\n后壳材质\n超瓷晶面板', 1, '2026-10-12 21:01:52', '2026-10-15 21:52:23');
INSERT INTO `product` VALUES (2, 5, 'OPPO Find X8 Ultra', '性价比之选', '/upload/025f807eec264f6db273e61ba0dfcf32.webp', 2299.00, 2499.00, 189, 67, '【正品】OPPO Find X8 Ultra 旗舰手机 夜景人像专业镜头 丹霞原彩镜头骁龙8至尊版 拍照游戏', 1, '2026-10-12 21:01:52', '2026-10-13 14:54:14');
INSERT INTO `product` VALUES (3, 7, 'XPS13Air', '【国家补贴15%】Dell/戴尔笔记本电脑 XPS13Air第三代英特尔酷睿Core5/UItra7 2.5K触控全面屏办公学生轻薄本', '/upload/8bd550aea7814d6d90d467d20551118d.jpg', 8110.94, 10999.00, 17, 15, '<p>平板电脑商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-15 21:52:23');
INSERT INTO `product` VALUES (4, 7, '华硕a豆14 Air', '14英寸 16G 512G', '/upload/12f104b8b6d04dc69bb66f7f414e3f01.webp', 5599.00, 5999.00, 60, 8, '<p>轻薄本商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-12 23:20:05');
INSERT INTO `product` VALUES (5, 8, 'AOC联名办公主机', '高性能办公主机', '/upload/3e8c2efcf3cd407da2f7e03aee44a7a4.webp', 3999.00, 4299.00, 40, 5, '<p>台式机商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-12 23:20:45');
INSERT INTO `product` VALUES (6, 9, '美拉德棕色翻领夹克', '春秋季休闲外套', '/upload/3ac8276518ca4cd0a686f1a086db556d.webp', 98.44, 98.44, 492, 96, '<p>男装夹克商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-15 21:52:23');
INSERT INTO `product` VALUES (7, 10, '法式茶歇白色系带V领腰带镂空连衣裙', '夏季新款', '/upload/cd390196a63548e7b4c9adbba2cf881d.webp', 199.00, 259.00, 599, 121, '<p>女装连衣裙商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-17 15:41:29');
INSERT INTO `product` VALUES (8, 11, '美的249/251三门小冰箱', '三门 节能静音', '/upload/9438e1a0a5494191b4ba1ea94739a118.webp', 2799.00, 3099.00, 30, 4, '<p>冰箱商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-12 23:22:26');
INSERT INTO `product` VALUES (9, 12, '松下白月光5.0洗烘套装洗衣机', '10公斤 滚筒', '/upload/5f13d388bca9458cb48662411cc0cb35.webp', 1899.00, 2199.00, 26, 15, '<p>洗衣机商品详情示例。</p>', 1, '2026-10-12 21:01:52', '2026-10-15 21:52:23');
INSERT INTO `product` VALUES (10, 19, '三只松鼠每日坚果 750g/30袋', '混合果仁 独立小包装', '/upload/1306949553a9437292102eaab81cb58f.webp', 89.90, 129.00, 500, 320, '内含巴旦木、腰果、核桃仁、蔓越莓干等，独立小包装，适合办公零食。', 1, '2026-10-13 13:38:04', '2026-10-13 14:23:45');
INSERT INTO `product` VALUES (11, 19, '乐事薯片混合口味 520g', '无限薯片 休闲零食', '/upload/d86c8fd1ea084770b5b1696109fe05a8.webp', 39.90, 49.90, 800, 560, '混合口味整箱装，酥脆可口，休闲追剧零食。', 1, '2026-10-13 13:38:04', '2026-10-13 14:18:10');
INSERT INTO `product` VALUES (12, 20, '金龙鱼东北大米 10kg', '粳米 当季新米', '/upload/7254bc5b1199499eb7ba8bd19839c734.webp', 69.90, 89.90, 300, 210, '东北优质粳米，颗粒饱满，米香浓郁，适合家庭日常食用。', 1, '2026-10-13 13:38:04', '2026-10-13 14:24:15');
INSERT INTO `product` VALUES (13, 20, '海天金标生抽 1.9L', '酿造酱油 厨房调味', '/upload/98cb2fe6800141749a64672c0e40902c.webp', 19.90, 25.90, 599, 431, '金标生抽，适合炒菜、凉拌、点蘸，家庭厨房常备调味品。', 1, '2026-10-13 13:38:04', '2026-10-15 21:52:23');
INSERT INTO `product` VALUES (14, 21, '安踏男子跑步鞋 氮科技', '缓震回弹 运动鞋', '/upload/525846fe03234284a5ac3a8df23ac46c.webp', 399.00, 499.00, 200, 150, '氮科技中底，轻量缓震，适合日常慢跑和休闲运动。', 1, '2026-10-13 13:38:04', '2026-10-13 14:25:37');
INSERT INTO `product` VALUES (15, 21, '李宁运动卫衣 春秋款', '宽松休闲 圆领套头', '/upload/cf57ddd462534275a9ee311bef7faa56.webp', 199.00, 269.00, 300, 180, '亲肤面料，宽松版型，适合春秋季运动休闲穿着。', 1, '2026-10-13 13:38:04', '2026-10-13 14:26:49');
INSERT INTO `product` VALUES (16, 22, '牧高笛户外帐篷 3-4人', '自动速开 防雨防晒', '/upload/3d2c87332d4f4929876dcf55c80c81d6.webp', 459.00, 599.00, 80, 45, '适合公园露营、野餐，自动速开设计，搭建方便。', 1, '2026-10-13 13:38:04', '2026-10-13 14:27:20');
INSERT INTO `product` VALUES (17, 22, '迪卡侬登山杖 轻量铝合金', '一对装 可调节', '/upload/aa5abbe617924c3f8f3d18c5d57a071c.webp', 129.90, 159.90, 148, 92, '轻量铝合金杖身，可调节长度，适合徒步登山。', 1, '2026-10-13 13:38:04', '2026-10-15 22:42:23');
INSERT INTO `product` VALUES (18, 23, '珀莱雅双抗精华 2.0 30ml', '抗氧抗糖 提亮肤色', '/upload/3b61c8b6793a4f1098bf20a198355496.webp', 239.00, 329.00, 400, 300, '双抗精华，改善暗沉，适合熬夜肌日常护肤。', 1, '2026-10-13 13:38:04', '2026-10-13 14:28:29');
INSERT INTO `product` VALUES (19, 23, '薇诺娜舒敏保湿特护霜 50g', '敏感肌修护 保湿面霜', '/upload/058126ba18e047fe9330b69af7aacb03.webp', 268.00, 328.00, 250, 190, '专为敏感肌设计，舒缓保湿，帮助修护肌肤屏障。', 1, '2026-10-13 13:38:04', '2026-10-13 14:28:55');
INSERT INTO `product` VALUES (20, 24, '蓝月亮洗衣液 薰衣草 3kg', '机洗手洗 持久留香', '/upload/47bf7bd026ae445ab47e2360eeab2e64.webp', 49.90, 69.90, 500, 410, '薰衣草香型，深层洁净，低泡易漂，适合机洗手洗。', 1, '2026-10-13 13:38:04', '2026-10-13 14:29:44');
INSERT INTO `product` VALUES (21, 24, '云南白药牙膏 薄荷 180g', '清新口气 护龈', '/upload/81e93c648a2c44a78a0f138c29a103b1.webp', 29.90, 39.90, 600, 520, '薄荷香型，帮助清新口气，日常护龈清洁。', 1, '2026-10-13 13:38:04', '2026-10-13 14:30:13');
INSERT INTO `product` VALUES (22, 25, '水星家纺全棉四件套 1.8m床', '100%棉 简约纯色', '/upload/38bb38539a614f5686e871858f5eb039.webp', 399.00, 599.00, 120, 70, '全棉面料，柔软透气，适合1.8米床使用。', 1, '2026-10-13 13:38:04', '2026-10-13 14:30:44');
INSERT INTO `product` VALUES (23, 25, '网易严选乳胶枕 泰国进口', '93%乳胶 颈椎枕', '/upload/7f2a7e0deb93478880ea7e3fec0f9318.webp', 199.00, 299.00, 180, 120, '泰国进口乳胶，柔软支撑，透气舒适，适合日常睡眠。', 1, '2026-10-13 13:38:04', '2026-10-13 14:31:25');
INSERT INTO `product` VALUES (24, 26, '苏泊尔麦饭石不粘炒锅 32cm', '少油烟 燃气电磁炉通用', '/upload/c76e799c34b6467891481b033a34328f.webp', 159.00, 229.00, 200, 130, '麦饭石色不粘涂层，炒菜少油烟，燃气电磁炉通用。', 1, '2026-10-13 13:38:04', '2026-10-13 14:18:57');
INSERT INTO `product` VALUES (25, 26, '双立人刀具套装 6件套', '不锈钢 厨房刀具', '/upload/0fd30c079c744b27a93b0ef87e7e0800.webp', 499.00, 699.00, 100, 60, '不锈钢刀身，锋利耐用，含刀架，适合家庭厨房。', 1, '2026-10-13 13:38:04', '2026-10-17 13:56:44');
INSERT INTO `product` VALUES (26, 27, '《活着》余华 精装版', '经典文学 当代小说', '/upload/563e3681c52241b5a0ddb4bc6fc4badf.webp', 29.90, 45.00, 300, 260, '余华代表作，讲述普通人在时代变迁中的命运与坚韧。', 1, '2026-10-13 13:38:04', '2026-10-15 22:43:56');
INSERT INTO `product` VALUES (27, 27, '《三体》全集 1-3 刘慈欣', '科幻巨著 地球往事', '/upload/281430cb7f2f44bca507239aff9f009c.webp', 89.00, 128.00, 200, 180, '刘慈欣科幻代表作，含三体、黑暗森林、死神永生。', 1, '2026-10-13 13:38:04', '2026-10-17 15:07:46');
INSERT INTO `product` VALUES (28, 28, '得力中性笔 0.5mm 黑色 60支', '办公签字笔 学生用水笔', '/upload/4a56473d3f334734921d610729cc3617.webp', 39.90, 59.90, 500, 350, '0.5mm子弹头，书写顺滑，60支整盒，办公学习适用。', 1, '2026-10-13 13:38:04', '2026-10-13 14:21:28');
INSERT INTO `product` VALUES (29, 28, '晨光活页笔记本 A5 100页', '可拆卸 横线内页', '/upload/8ed94815885943e19e11b55b742799a3.webp', 19.90, 29.90, 400, 210, 'A5尺寸，活页可拆，横线内页，适合办公学习记录。', 1, '2026-10-13 13:38:04', '2026-10-13 14:21:53');
INSERT INTO `product` VALUES (30, 29, '飞鹤星飞帆幼儿配方奶粉 3段 700g', '适合12-36个月', '/upload/1ea2a97b853a44049baea102ffbe7fd6.webp', 268.00, 328.00, 150, 95, '幼儿配方奶粉，3段，700g罐装，适合12-36个月宝宝。', 1, '2026-10-13 13:38:04', '2026-10-13 14:22:24');
INSERT INTO `product` VALUES (31, 29, '亨氏婴儿米粉 铁锌钙 400g', '宝宝辅食 营养米粉', '/upload/c9470b7b40754d1da4521dc2e45f7de5.webp', 59.90, 79.90, 200, 140, '含铁锌钙，适合宝宝辅食添加期，冲调方便。', 1, '2026-10-13 13:38:04', '2026-10-13 14:22:52');
INSERT INTO `product` VALUES (32, 30, '乐高城市系列消防救援车 60374', '拼搭积木 儿童玩具', '/upload/c8b8319016624d77a8c7c89e5a7016aa.webp', 299.00, 399.00, 80, 50, '乐高城市系列，含消防车和消防员人仔，适合儿童拼搭。', 1, '2026-10-13 13:38:04', '2026-10-13 14:23:25');
INSERT INTO `product` VALUES (33, 30, '布鲁可大颗粒积木 百变布鲁可小队', '益智拼装 男孩玩具', '/upload/c032bcf14de347daa85b9ed855953d6a.webp', 149.00, 199.00, 120, 80, '大颗粒积木，适合低龄儿童拼搭，锻炼动手能力。', 1, '2026-10-13 13:38:04', '2026-10-13 14:27:51');

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父分类ID，0为一级分类',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `level` int NULL DEFAULT 1 COMMENT '层级：1一级，2二级',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图标',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, 0, '手机数码', 1, 1, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (2, 0, '电脑办公', 1, 2, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (3, 0, '服饰鞋包', 1, 3, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (4, 0, '家用电器', 1, 4, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (5, 1, '手机', 2, 2, NULL, 1, '2026-10-12 21:01:52', '2026-10-13 13:59:27');
INSERT INTO `product_category` VALUES (6, 1, '平板电脑', 2, 1, NULL, 1, '2026-10-12 21:01:52', '2026-10-13 13:59:25');
INSERT INTO `product_category` VALUES (7, 2, '笔记本电脑', 2, 1, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (8, 2, '台式机', 2, 2, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (9, 3, '男装', 2, 1, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (10, 3, '女装', 2, 2, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (11, 4, '冰箱', 2, 1, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (12, 4, '洗衣机', 2, 2, NULL, 1, '2026-10-12 21:01:52', '2026-10-12 21:01:52');
INSERT INTO `product_category` VALUES (13, 0, '食品生鲜', 1, 5, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (14, 0, '运动户外', 1, 6, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (15, 0, '美妆个护', 1, 7, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (16, 0, '家居家装', 1, 8, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (17, 0, '图书文娱', 1, 9, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (18, 0, '母婴玩具', 1, 10, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (19, 13, '休闲零食', 2, 1, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (20, 13, '粮油调味', 2, 2, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (21, 14, '运动鞋服', 2, 1, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (22, 14, '户外装备', 2, 2, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (23, 15, '面部护肤', 2, 1, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (24, 15, '洗护清洁', 2, 2, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (25, 16, '床品家纺', 2, 1, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (26, 16, '厨房用品', 2, 2, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (27, 17, '图书小说', 2, 1, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (28, 17, '文具办公', 2, 2, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (29, 18, '奶粉辅食', 2, 1, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');
INSERT INTO `product_category` VALUES (30, 18, '益智玩具', 2, 2, NULL, 1, '2026-10-13 13:38:04', '2026-10-13 13:38:04');

-- ----------------------------
-- Table structure for product_comment
-- ----------------------------
DROP TABLE IF EXISTS `product_comment`;
CREATE TABLE `product_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `member_id` bigint NOT NULL COMMENT '会员ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '订单ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评价内容',
  `rating` int NULL DEFAULT 5 COMMENT '评分：1-5',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_member_id`(`member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_comment
-- ----------------------------
INSERT INTO `product_comment` VALUES (1, 2, 1, 1, '好用', 5, '2026-10-12 23:25:57');
INSERT INTO `product_comment` VALUES (2, 2, 3, 3, '哈哈哈哈哈哈哈哈哈哈', 5, '2026-10-13 14:55:27');
INSERT INTO `product_comment` VALUES (3, 7, 3, 11, '好看', 5, '2026-10-17 15:45:50');

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片地址',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (9, 11, '/upload/92a67c4b061a4f13a05b7737f84f76e3.webp', 0, '2026-10-13 14:18:10');

-- ----------------------------
-- Table structure for seckill_activity
-- ----------------------------
DROP TABLE IF EXISTS `seckill_activity`;
CREATE TABLE `seckill_activity`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场次名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '秒杀场次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seckill_activity
-- ----------------------------
INSERT INTO `seckill_activity` VALUES (1, '双十一特惠', '2026-10-15 21:03:05', '2026-10-16 00:00:00', 1, '2026-10-15 21:34:19', '2026-10-15 21:34:19');

-- ----------------------------
-- Table structure for seckill_item
-- ----------------------------
DROP TABLE IF EXISTS `seckill_item`;
CREATE TABLE `seckill_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint NOT NULL COMMENT '场次ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `seckill_price` decimal(10, 2) NOT NULL COMMENT '秒杀价',
  `seckill_stock` int NOT NULL DEFAULT 0 COMMENT '秒杀库存',
  `limit_per_user` int NULL DEFAULT 1 COMMENT '每人限购数量',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '秒杀商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seckill_item
-- ----------------------------
INSERT INTO `seckill_item` VALUES (1, 1, 17, 9.90, 100, 1, 1, '2026-10-15 21:34:49', '2026-10-15 21:34:49');
INSERT INTO `seckill_item` VALUES (2, 1, 25, 9.90, 10, 1, 1, '2026-10-15 21:35:03', '2026-10-15 21:35:03');
INSERT INTO `seckill_item` VALUES (3, 1, 17, 5.00, 1, 1, 1, '2026-10-15 21:35:17', '2026-10-15 21:35:17');
INSERT INTO `seckill_item` VALUES (7, 1, 26, 0.00, 100, 1, 1, '2026-10-15 21:48:04', '2026-10-15 21:57:23');
INSERT INTO `seckill_item` VALUES (8, 1, 27, 44.00, 100, 1, 1, '2026-10-15 21:48:04', '2026-10-15 21:48:04');
INSERT INTO `seckill_item` VALUES (9, 1, 28, 19.00, 100, 1, 1, '2026-10-15 21:48:04', '2026-10-15 21:48:04');
INSERT INTO `seckill_item` VALUES (10, 1, 29, 9.00, 100, 1, 1, '2026-10-15 21:48:04', '2026-10-15 21:48:04');
INSERT INTO `seckill_item` VALUES (17, 1, 5, 1999.00, 90, 1, 1, '2026-10-17 14:00:11', '2026-10-17 14:00:11');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ADMIN' COMMENT '角色',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
  `online` tinyint NULL DEFAULT 0 COMMENT '是否在线（客服）：1在线，0离线',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$hCom62bQ5n0nUZ9vh3DFlOacq6SvG4q1iogMcCzCBjd3yNtKuZS5a', '超级管理员', NULL, 'ADMIN', 1, 0, '2026-10-12 21:03:36', '2026-10-12 22:11:11');
INSERT INTO `sys_user` VALUES (2, 'service', '$2a$10$.P7K6IYTqC3vqz2Sw3y6aeMJ6Z6y2EjADY/c12pN73rZfDe9RtXk6', '在线客服', NULL, 'SERVICE', 1, 1, '2026-10-17 14:58:39', '2026-10-17 15:10:00');
INSERT INTO `sys_user` VALUES (3, 'ppp', '$2a$10$jlB91UQNYG.Uj4FA8QJpDuKUNGVt/M452SqIjyJd..CpAq.MRrqs6', 'PPP', NULL, 'SERVICE', 1, 1, '2026-10-17 15:22:51', '2026-10-17 15:23:11');

SET FOREIGN_KEY_CHECKS = 1;
