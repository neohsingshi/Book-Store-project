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

 Date: 30/12/2024 13:54:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'bookID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'book title',
  `price` decimal(10, 2) NOT NULL COMMENT 'price',
  `stock` int NOT NULL DEFAULT 0 COMMENT 'stockpile',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'form',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Book Information Sheet' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (2, '123', 123.00, 114, 'fiction');
INSERT INTO `books` VALUES (3, '1232', 123.00, 110, 'sci-fi');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'Order ID, self-incrementing primary key',
  `book_id` int NOT NULL COMMENT 'Book ID, foreign key associated to id field in books table',
  `quantity` int NOT NULL COMMENT 'Quantity Purchased, which indicates the number of copies of the book purchased by the user',
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Order Date, defaults to the current timestamp, records when the order was created',
  `user_identifier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'Order form, used to record information about the books purchased by users' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 2, 3, '2024-12-30 13:39:33', '123');
INSERT INTO `orders` VALUES (2, 3, 5, '2024-12-30 13:40:05', '123');
INSERT INTO `orders` VALUES (3, 2, 6, '2024-12-30 13:47:42', '123');
INSERT INTO `orders` VALUES (4, 3, 6, '2024-12-30 13:51:19', '123');
INSERT INTO `orders` VALUES (5, 3, 2, '2024-12-30 13:51:45', '123');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'userID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'name',
  `gender` enum('male','female') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'gender',
  `age` int NULL DEFAULT NULL COMMENT 'age',
  `identity` enum('user','manager') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'identity（user、manager）',
  `identifier` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IDnumber（Student number or faculty number or ID number, etc.）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'phonenumber',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'email',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'password',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `identifier`(`identifier` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'user information sheet' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (17, 'mmq', 'male', NULL, 'user', '123', '15888888888', '123@qq.com', '1234');
INSERT INTO `users` VALUES (18, 'mmq1', 'male', NULL, 'manager', '1234', '15888888888', '123@qq.com', '123');

SET FOREIGN_KEY_CHECKS = 1;
