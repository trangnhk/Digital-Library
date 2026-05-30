-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: librarydb
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `access_histories`
--

DROP TABLE IF EXISTS `access_histories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `access_time` datetime(6) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `document_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh6b69l4fteioj4wshx03v231s` (`document_id`),
  KEY `FKggwu6nys4bvlvvdvy5e2c014q` (`user_id`),
  CONSTRAINT `FKggwu6nys4bvlvvdvy5e2c014q` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKh6b69l4fteioj4wshx03v231s` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_histories`
--

LOCK TABLES `access_histories` WRITE;
/*!40000 ALTER TABLE `access_histories` DISABLE KEYS */;
INSERT INTO `access_histories` VALUES (1,'2026-05-19 23:44:56.000000','127.0.0.1',1,4),(2,'2026-05-19 23:44:56.000000','127.0.0.1',2,4),(3,'2026-05-19 23:44:56.000000','127.0.0.1',1,5),(4,'2026-05-19 23:44:56.000000','127.0.0.1',3,6),(5,'2026-05-19 23:44:56.000000','127.0.0.1',2,6);
/*!40000 ALTER TABLE `access_histories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bookmarks`
--

DROP TABLE IF EXISTS `bookmarks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookmarks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `document_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmh5yuctvtx7m5r7405n4ys7x7` (`document_id`),
  KEY `FKdbsho2e05w5r13fkjqfjmge5f` (`user_id`),
  CONSTRAINT `FKdbsho2e05w5r13fkjqfjmge5f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmh5yuctvtx7m5r7405n4ys7x7` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookmarks`
--

LOCK TABLES `bookmarks` WRITE;
/*!40000 ALTER TABLE `bookmarks` DISABLE KEYS */;
INSERT INTO `bookmarks` VALUES (1,'2026-05-19 23:44:56.000000',1,4),(2,'2026-05-19 23:44:56.000000',2,4),(3,'2026-05-19 23:44:56.000000',1,5),(4,'2026-05-19 23:44:56.000000',3,6);
/*!40000 ALTER TABLE `bookmarks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrow_histories`
--

DROP TABLE IF EXISTS `borrow_histories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_histories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `borrow_date` datetime(6) DEFAULT NULL,
  `return_date` datetime(6) DEFAULT NULL,
  `status` enum('BORROWING','RETURNED','EXPIRED') NOT NULL,
  `document_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKspy29wra08g908l6teyb5yhbc` (`document_id`),
  KEY `FKaqvx45q063nlp9vrg7hyucv5o` (`user_id`),
  CONSTRAINT `FKaqvx45q063nlp9vrg7hyucv5o` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKspy29wra08g908l6teyb5yhbc` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`),
  CONSTRAINT `borrow_histories_chk_1` CHECK ((`status` between 0 and 2))
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_histories`
--

LOCK TABLES `borrow_histories` WRITE;
/*!40000 ALTER TABLE `borrow_histories` DISABLE KEYS */;
INSERT INTO `borrow_histories` VALUES (8,'2026-05-19 23:43:12.000000',NULL,'BORROWING',1,4),(9,'2026-05-19 23:43:12.000000','2026-05-19 23:43:12.000000','RETURNED',2,5),(10,'2026-05-19 23:43:12.000000',NULL,'BORROWING',3,6),(11,'2026-05-19 23:43:14.000000',NULL,'BORROWING',1,4),(12,'2026-05-19 23:43:14.000000','2026-05-19 23:43:14.000000','RETURNED',2,5),(13,'2026-05-19 23:43:14.000000',NULL,'BORROWING',3,6);
/*!40000 ALTER TABLE `borrow_histories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_date` datetime(6) NOT NULL,
  `description` text NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,_binary '','2026-05-19 22:31:25.000000','Tài liệu ngành Khoa học máy tính','Computer Science'),(2,_binary '','2026-05-19 22:31:25.000000','Tài liệu ngành Công nghệ phần mềm','Software Engineering'),(3,_binary '','2026-05-19 22:31:25.000000','Tài liệu ngành Hệ thống thông tin','Information Systems'),(4,_binary '','2026-05-19 22:31:25.000000','Tài liệu ngành Trí tuệ nhân tạo','Artificial Intelligence'),(5,_binary '','2026-05-19 22:31:25.000000','Tài liệu ngành Kinh doanh','Business');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message` text,
  `sent_time` datetime(6) DEFAULT NULL,
  `conversation_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc8ljv426x8fj9tcywei40stu9` (`conversation_id`),
  KEY `FKgiqeap8ays4lf684x7m0r2729` (`sender_id`),
  CONSTRAINT `FKc8ljv426x8fj9tcywei40stu9` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`),
  CONSTRAINT `FKgiqeap8ays4lf684x7m0r2729` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_messages`
--

LOCK TABLES `chat_messages` WRITE;
/*!40000 ALTER TABLE `chat_messages` DISABLE KEYS */;
INSERT INTO `chat_messages` VALUES (1,'Chào em, em cần tài liệu nào?','2026-05-20 03:34:45.000000',1,2),(2,'Em cần tài liệu Spring Boot ạ','2026-05-20 03:34:45.000000',1,4),(3,'Bạn có tài liệu ReactJS không?','2026-05-20 03:34:45.000000',2,4),(4,'Có, mình vừa bookmark một tài liệu hay','2026-05-20 03:34:45.000000',2,5);
/*!40000 ALTER TABLE `chat_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversation_users`
--

DROP TABLE IF EXISTS `conversation_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversation_users` (
  `user_id` bigint NOT NULL,
  `conversation_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`conversation_id`),
  KEY `FK63p0tbk14kd41sn7v1cw6xp2f` (`conversation_id`),
  CONSTRAINT `FK63p0tbk14kd41sn7v1cw6xp2f` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`),
  CONSTRAINT `FKpn6opxurwrxsbkr91jqj2qfdy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation_users`
--

LOCK TABLES `conversation_users` WRITE;
/*!40000 ALTER TABLE `conversation_users` DISABLE KEYS */;
INSERT INTO `conversation_users` VALUES (2,1),(4,1),(4,2),(5,2);
/*!40000 ALTER TABLE `conversation_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversations`
--

DROP TABLE IF EXISTS `conversations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversations`
--

LOCK TABLES `conversations` WRITE;
/*!40000 ALTER TABLE `conversations` DISABLE KEYS */;
INSERT INTO `conversations` VALUES (1,'2026-05-20 03:34:43.000000'),(2,'2026-05-20 03:34:43.000000');
/*!40000 ALTER TABLE `conversations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_files`
--

DROP TABLE IF EXISTS `document_files`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document_files` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_extension` varchar(255) DEFAULT NULL,
  `file_size` bigint DEFAULT NULL,
  `file_url` varchar(1000) NOT NULL,
  `public_id` varchar(255) DEFAULT NULL,
  `uploaded_date` datetime(6) DEFAULT NULL,
  `document_id` bigint DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK4i3qsa26kqr3ppokq33k1l0pi` (`document_id`),
  CONSTRAINT `FK4i3qsa26kqr3ppokq33k1l0pi` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_files`
--

LOCK TABLES `document_files` WRITE;
/*!40000 ALTER TABLE `document_files` DISABLE KEYS */;
INSERT INTO `document_files` VALUES (1,'pdf',2048000,'https://github.com/marangelologic/books/blob/master/Spring%20Boot%20in%20Action.pdf','springboot-basic','2026-05-19 22:53:24.000000',1,1),(2,'pdf',3072000,'https://phanindra-reddy.github.io/portfolio/static/media/The%20Road%20to%20learn%20React.83694f81.pdf','reactjs-advanced','2026-05-19 22:53:24.000000',2,1),(3,'docx',1024000,'https://www.nrigroupindia.com/e-book/Introduction%20to%20Machine%20Learning%20with%20Python%20(%20PDFDrive.com%20)-min.pdf','ml-notes','2026-05-19 22:53:24.000000',3,1),(4,'epub',1536000,'https://users.encs.concordia.ca/~gregb/home/PDF/se_design_patterns.pdf','design-patterns','2026-05-19 22:53:24.000000',4,0),(5,'pdf',2048000,'https://github.com/marangelologic/books/blob/master/Spring%20Boot%20in%20Action.pdf','springboot-basic','2026-05-19 22:53:28.000000',1,1),(6,'pdf',3072000,'https://phanindra-reddy.github.io/portfolio/static/media/The%20Road%20to%20learn%20React.83694f81.pdf','reactjs-advanced','2026-05-19 22:53:28.000000',2,1),(7,'docx',1024000,'https://www.nrigroupindia.com/e-book/Introduction%20to%20Machine%20Learning%20with%20Python%20(%20PDFDrive.com%20)-min.pdf','ml-notes','2026-05-19 22:53:28.000000',3,1),(8,'epub',1536000,'https://users.encs.concordia.ca/~gregb/home/PDF/se_design_patterns.pdf','design-patterns','2026-05-19 22:53:28.000000',4,0);
/*!40000 ALTER TABLE `document_files` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `documents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved` bit(1) DEFAULT NULL,
  `author` varchar(255) NOT NULL,
  `average_rating` double DEFAULT NULL,
  `created_date` datetime(6) NOT NULL,
  `description` text,
  `document_type` enum('AUDIO','DOCX','EPUB','PDF','VIDEO') NOT NULL,
  `premium` bit(1) NOT NULL,
  `price` double NOT NULL,
  `publish_year` int NOT NULL,
  `publisher` varchar(255) NOT NULL,
  `thumbnail` varchar(500) DEFAULT NULL,
  `title` varchar(500) NOT NULL,
  `total_downloads` int DEFAULT NULL,
  `total_views` int DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `upload_by` bigint DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK70g21yw6d0n958n3khscvgbls` (`category_id`),
  KEY `FKm90sbe3jeej3t1xfwv0of62wf` (`upload_by`),
  CONSTRAINT `FK70g21yw6d0n958n3khscvgbls` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `FKm90sbe3jeej3t1xfwv0of62wf` FOREIGN KEY (`upload_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `documents`
--

LOCK TABLES `documents` WRITE;
/*!40000 ALTER TABLE `documents` DISABLE KEYS */;
INSERT INTO `documents` VALUES (1,_binary '','Nguyen Van A',4.5,'2026-05-19 22:39:51.000000','Tài liệu nhập môn Spring Boot','PDF',_binary '\0',0,2024,'OU Publishing','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204986/spring-boot-in-action-book_im8cgj.png','Spring Boot Cơ Bản',45,120,'2026-05-19 22:39:51.000000',2,2,NULL),(2,_binary '','Tran Thi B',4.8,'2026-05-19 22:39:51.000000','Học ReactJS thực chiến','PDF',_binary '',50000,2023,'Tech Books','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204985/0414995a-f04a-48e3-8ff2-508fc6973e27_bjqkid.webp','ReactJS Từ Cơ Bản Đến Nâng Cao',80,230,'2026-05-19 22:39:51.000000',2,2,NULL),(3,_binary '','Le C',4.2,'2026-05-19 22:39:51.000000','Ghi chú học máy','DOCX',_binary '\0',0,2025,'AI House','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204985/ml-pocket-ref_yval3a.jpg','Machine Learning Notes',20,95,'2026-05-19 22:39:51.000000',4,2,NULL),(4,_binary '\0','Pham D',0,'2026-05-19 22:39:51.000000','Thiết kế phần mềm với design patterns','EPUB',_binary '',75000,2022,'SE Press','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204986/81IGFC6oFmL._AC_UF1000_1000_QL80__zp6qnu.jpg','Software Design Patterns',3,15,'2026-05-19 22:39:51.000000',2,2,NULL),(5,_binary '','Nguyen Van A',4.5,'2026-05-19 22:40:06.000000','Tài liệu nhập môn Spring Boot','PDF',_binary '\0',0,2024,'OU Publishing','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204986/spring-boot-in-action-book_im8cgj.png','Spring Boot Cơ Bản',45,120,'2026-05-19 22:40:06.000000',2,2,NULL),(6,_binary '','Tran Thi B',4.8,'2026-05-19 22:40:06.000000','Học ReactJS thực chiến','PDF',_binary '',50000,2023,'Tech Books','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204985/0414995a-f04a-48e3-8ff2-508fc6973e27_bjqkid.webp','ReactJS Từ Cơ Bản Đến Nâng Cao',80,230,'2026-05-19 22:40:06.000000',2,2,NULL),(7,_binary '','Le C',4.2,'2026-05-19 22:40:06.000000','Ghi chú học máy','DOCX',_binary '\0',0,2025,'AI House','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204985/ml-pocket-ref_yval3a.jpg','Machine Learning Notes',20,95,'2026-05-19 22:40:06.000000',4,2,NULL),(8,_binary '\0','Pham D',0,'2026-05-19 22:40:06.000000','Thiết kế phần mềm với design patterns','EPUB',_binary '',75000,2022,'SE Press','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779204986/81IGFC6oFmL._AC_UF1000_1000_QL80__zp6qnu.jpg','Software Design Patterns',3,15,'2026-05-19 22:40:06.000000',2,2,NULL);
/*!40000 ALTER TABLE `documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `created_date` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` VALUES (1,'Chào mừng bạn đến với thư viện số','2026-05-20 03:34:45.000000',_binary '\0','Chào mừng',4),(2,'Có sinh viên vừa gửi tin nhắn cho bạn','2026-05-20 03:34:45.000000',_binary '\0','Có yêu cầu mới',2),(3,'Có 1 librarian mới cần được phê duyệt','2026-05-20 03:34:45.000000',_binary '\0','Librarian chờ duyệt',1);
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_method` enum('CASH','MOMO','PAYPAL','STRIPE','ZALOPAY') DEFAULT NULL,
  `payment_status` enum('FAILED','PENDING','SUCCESS') DEFAULT NULL,
  `transaction_code` varchar(255) DEFAULT NULL,
  `document_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8inpv30544qjykcwa6ck7pusy` (`transaction_code`),
  KEY `FKee4ad3tihc1nj3ost1j53vas9` (`document_id`),
  KEY `FKj94hgy9v5fw1munb90tar2eje` (`user_id`),
  CONSTRAINT `FKee4ad3tihc1nj3ost1j53vas9` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`),
  CONSTRAINT `FKj94hgy9v5fw1munb90tar2eje` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,50000,'2026-05-19 23:43:51.000000','MOMO','SUCCESS','TXN_MOMO_001',2,4),(2,75000,'2026-05-19 23:43:51.000000','PAYPAL','PENDING','TXN_PAYPAL_001',4,5);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_date` datetime(6) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `document_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmhyixicnke2me5r8ehgfv2fvt` (`document_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKmhyixicnke2me5r8ehgfv2fvt` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'Tài liệu rất dễ hiểu','2026-05-19 23:06:15.000000',5,1,4),(2,'Phù hợp cho người mới bắt đầu','2026-05-19 23:06:15.000000',4,1,5),(3,'ReactJS giải thích rất thực tế','2026-05-19 23:06:15.000000',5,2,6),(4,'Tài liệu ổn','2026-05-19 23:06:15.000000',4,3,4),(5,'Tài liệu rất dễ hiểu','2026-05-19 23:06:17.000000',5,1,4),(6,'Phù hợp cho người mới bắt đầu','2026-05-19 23:06:17.000000',4,1,5),(7,'ReactJS giải thích rất thực tế','2026-05-19 23:06:17.000000',5,2,6),(8,'Tài liệu ổn','2026-05-19 23:06:17.000000',4,3,4);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `created_date` datetime(6) NOT NULL,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `librarian_verified` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `provider` varchar(20) DEFAULT NULL,
  `role` enum('ROLE_ADMIN','ROLE_LECTURER','ROLE_LIBRARIAN','ROLE_STUDENT') NOT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UKdu5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','https://res.cloudinary.com/dxfbpkmen/image/upload/v1765476183/setting_gjan84.png','2026-05-19 22:26:50.000000','admin@gmail.com','System','Admin',_binary '','$2a$10$3zy9vFRusQLk1EAAGkacOOdRSa8VGxedSYHzJzAnYYBm7uVoGsSu6','0900000001','LOCAL','ROLE_ADMIN','2026-05-19 22:26:50.000000','admin'),(2,_binary '','https://res.cloudinary.com/dxfbpkmen/image/upload/v1764923514/cnge8gjobbsufynicddf.png','2026-05-19 22:26:50.000000','librarian01@gmail.com','Thu','Thu',_binary '','$2a$10$3zy9vFRusQLk1EAAGkacOOdRSa8VGxedSYHzJzAnYYBm7uVoGsSu6','0900000002','LOCAL','ROLE_LIBRARIAN','2026-05-19 22:26:50.000000','librarian01'),(3,_binary '','https://res.cloudinary.com/dxfbpkmen/image/upload/v1764923514/cnge8gjobbsufynicddf.png','2026-05-19 22:26:50.000000','librarian02@gmail.com','Pending','Librarian',_binary '\0','$2a$10$3zy9vFRusQLk1EAAGkacOOdRSa8VGxedSYHzJzAnYYBm7uVoGsSu6','0900000003','LOCAL','ROLE_LIBRARIAN','2026-05-19 22:26:50.000000','librarian02'),(4,_binary '','https://res.cloudinary.com/dxfbpkmen/image/upload/v1779219473/sqexmsbeulzmv3o3t3zs.jpg','2026-05-19 22:26:50.000000','student01@gmail.com','Nguyen','Van A updated',_binary '\0','$2a$10$3zy9vFRusQLk1EAAGkacOOdRSa8VGxedSYHzJzAnYYBm7uVoGsSu6','0900000004','LOCAL','ROLE_STUDENT','2026-05-19 22:26:50.000000','student01'),(5,_binary '','https://res.cloudinary.com/dxfbpkmen/image/upload/v1764923514/cnge8gjobbsufynicddf.png','2026-05-19 22:26:50.000000','student02@gmail.com','Tran','Thi B',_binary '\0','$2a$10$3zy9vFRusQLk1EAAGkacOOdRSa8VGxedSYHzJzAnYYBm7uVoGsSu6','0900000005','LOCAL','ROLE_STUDENT','2026-05-19 22:26:50.000000','student02'),(6,_binary '','https://res.cloudinary.com/dxfbpkmen/image/upload/v1764923514/cnge8gjobbsufynicddf.png','2026-05-19 22:26:50.000000','lecturer01@gmail.com','Le','Giang Vien',_binary '\0','$2a$10$3zy9vFRusQLk1EAAGkacOOdRSa8VGxedSYHzJzAnYYBm7uVoGsSu6','0900000006','LOCAL','ROLE_LECTURER','2026-05-19 22:26:50.000000','lecturer01');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-31  0:01:36
