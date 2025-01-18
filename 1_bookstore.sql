/*
 Navicat Premium Dump SQL

 Source Server         : mmq
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : bookstore

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 18/01/2025 05:44:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Book ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Title',
  `price` decimal(10, 2) NOT NULL COMMENT 'Price',
  `stock` int NOT NULL DEFAULT 0 COMMENT 'Stock Quantity',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Category',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Books Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (2, '123', 123.00, 104, 'Novel');
INSERT INTO `books` VALUES (3, '1232', 123.00, 108, 'Science Fiction');
INSERT INTO `books` VALUES (4, '12', 132.00, 17, 'Science Fiction');
INSERT INTO `books` VALUES (6, '123', 1223.00, 1227, 'Science Fiction');
INSERT INTO `books` VALUES (7, '2', 12.01, 1221, 'Novel');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Order ID, auto-increment primary key',
  `book_id` int NOT NULL COMMENT 'Book ID, foreign key associated with the id field in the books table',
  `quantity` int NOT NULL COMMENT 'Quantity purchased, indicating the number of books a user has bought',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Order date, default value is current timestamp, recording the time when the order was created',
  `user_identifier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Orders Table, used to record information about book purchases by users' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 2, 3, '2024-12-30 13:39:33', '123');
INSERT INTO `orders` VALUES (2, 3, 5, '2024-12-30 13:40:05', '123');
INSERT INTO `orders` VALUES (3, 2, 6, '2024-12-30 13:47:42', '123');
INSERT INTO `orders` VALUES (4, 3, 6, '2024-12-30 13:51:19', '123');
INSERT INTO `orders` VALUES (5, 3, 2, '2024-12-30 13:51:45', '123');
INSERT INTO `orders` VALUES (6, 2, 1, '2025-01-18 04:41:02', '1234');
INSERT INTO `orders` VALUES (7, 4, 1, '2025-01-18 04:41:02', '1234');
INSERT INTO `orders` VALUES (8, 6, 1, '2025-01-18 04:41:02', '1234');
INSERT INTO `orders` VALUES (9, 2, 1, '2025-01-18 04:41:13', '1234');
INSERT INTO `orders` VALUES (10, 4, 1, '2025-01-18 04:41:13', '1234');
INSERT INTO `orders` VALUES (11, 6, 1, '2025-01-18 04:41:13', '1234');
INSERT INTO `orders` VALUES (12, 3, 1, '2025-01-18 04:41:49', '1234');
INSERT INTO `orders` VALUES (13, 4, 1, '2025-01-18 04:41:49', '1234');
INSERT INTO `orders` VALUES (14, 6, 1, '2025-01-18 04:41:49', '1234');
INSERT INTO `orders` VALUES (15, 2, 1, '2025-01-18 04:45:34', '1234');
INSERT INTO `orders` VALUES (16, 6, 1, '2025-01-18 04:45:34', '1234');
INSERT INTO `orders` VALUES (17, 4, 1, '2025-01-18 04:47:08', '1234');
INSERT INTO `orders` VALUES (18, 6, 1, '2025-01-18 04:47:08', '1234');
INSERT INTO `orders` VALUES (19, 3, 1, '2025-01-18 04:48:37', '1234');
INSERT INTO `orders` VALUES (20, 2, 6, '2025-01-18 04:50:50', '1234');
INSERT INTO `orders` VALUES (21, 2, 1, '2025-01-18 04:52:08', '123');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'User ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Name',
  `gender` enum('Male','Female') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Gender',
  `age` int NULL DEFAULT NULL COMMENT 'Age',
  `identity` enum('User','Admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Identity (User, Admin)',
  `identifier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Identifier (student number or employee number or ID card number, etc.)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Phone Number',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Email',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Password',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `identifier`(`identifier` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Users Information Table' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (17, 'mmq', 'Male', NULL, 'User', '123', '15888888888', '123@qq.com', '1234');
INSERT INTO `users` VALUES (18, 'mmq12', 'Male', 0, 'Admin', '1234', '15888888888', '123@qq.com', '123');
INSERT INTO `users` VALUES (19, '12345', 'Male', 0, 'Admin', '12345', '1232', '123', '123');
INSERT INTO `users` VALUES (21, '12333', 'Male', 0, 'Admin', '134', '123', '123', '123');
INSERT INTO `users` VALUES (22, '4534', 'Male', NULL, 'User', '2343', '34', '345', '23');
INSERT INTO `users` VALUES (25, '12367', 'Male', 123, 'User', '4342', '4123', '1213', 'defaultpassword');
INSERT INTO `users` VALUES (26, '4234', 'Male', 32, 'User', '23', '2323', '23', 'defaultpassword');
INSERT INTO `users` VALUES (29, '34342', 'Male', 1213, 'User', '12132', '1212', '1213', 'defaultpassword');
INSERT INTO `users` VALUES (31, '12', 'Male', 12, 'User', '12', '12', '12', 'defaultpassword');

SET FOREIGN_KEY_CHECKS = 1;
