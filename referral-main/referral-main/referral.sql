/*
 Navicat Premium Data Transfer

 Source Server         : uc-debug
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : rm-wz9lm2gta039bp9jbdo.mysql.rds.aliyuncs.com:3306
 Source Schema         : referral

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 08/04/2024 18:13:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_referral_code
-- ----------------------------
DROP TABLE IF EXISTS `t_referral_code`;
CREATE TABLE `t_referral_code`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int NOT NULL COMMENT '用户ID',
  `referral_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '邀请码',
  `total_volume` decimal(10, 2) NULL DEFAULT NULL COMMENT '总交易量',
  `referral_num` int NULL DEFAULT NULL COMMENT '邀请人数',
  `total_rebates` decimal(10, 6) NULL DEFAULT NULL COMMENT '总收益',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_referral_code`(`referral_code` ASC) USING BTREE COMMENT '邀请码索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邀请码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_referral_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_referral_relation`;
CREATE TABLE `t_referral_relation`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `referral_id` int NOT NULL COMMENT '邀请码id',
  `user_id` int NOT NULL COMMENT '被邀请用户ID',
  `user_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户地址',
  `user_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户logo',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邀请关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_referral_user
-- ----------------------------
DROP TABLE IF EXISTS `t_referral_user`;
CREATE TABLE `t_referral_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int NOT NULL COMMENT '用户ID',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户地址',
  `referral_num` int NULL DEFAULT 0 COMMENT '邀请人数',
  `referral_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '总收益',
  `rebates` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '回扣',
  `claimable_amount` decimal(10, 6) NULL DEFAULT 0.000000 COMMENT '可提现金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id_idx`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '邀请用户表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
