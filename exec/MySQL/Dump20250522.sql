-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 43.202.61.150    Database: fitpt
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `admin_id` bigint NOT NULL AUTO_INCREMENT,
  `admin_login_id` varchar(255) NOT NULL,
  `admin_pw` varchar(255) NOT NULL,
  `gym_name` varchar(255) DEFAULT NULL,
  `gym_addr` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `admin_login_id_UNIQUE` (`admin_login_id`),
  UNIQUE KEY `admin_id_UNIQUE` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `composition_log`
--

DROP TABLE IF EXISTS `composition_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `composition_log` (
  `composition_log_id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `protein` float DEFAULT NULL,
  `bmr` float DEFAULT NULL,
  `mineral` float DEFAULT NULL,
  `body_age` int DEFAULT NULL,
  `smm` float DEFAULT NULL,
  `icw` float DEFAULT NULL,
  `ecw` float DEFAULT NULL,
  `bfm` float DEFAULT NULL,
  `bfp` float DEFAULT NULL,
  `weight` float DEFAULT NULL,
  PRIMARY KEY (`composition_log_id`),
  KEY `FKfb00rgghpfb7pb56lxyflwy5j` (`member_id`),
  CONSTRAINT `FKfb00rgghpfb7pb56lxyflwy5j` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=139 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fcm_token`
--

DROP TABLE IF EXISTS `fcm_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fcm_token` (
  `fcm_token_id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `token` varchar(255) NOT NULL,
  `mac_addr` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`fcm_token_id`),
  KEY `FKf1rbjf8lle4r2in6ovkcgl0w8` (`member_id`),
  CONSTRAINT `FKf1rbjf8lle4r2in6ovkcgl0w8` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `member`
--

DROP TABLE IF EXISTS `member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member` (
  `member_id` bigint NOT NULL AUTO_INCREMENT,
  `trainer_id` bigint DEFAULT NULL,
  `admin_id` bigint DEFAULT NULL,
  `member_name` varchar(255) DEFAULT NULL,
  `member_gender` varchar(255) DEFAULT NULL,
  `member_birth` date DEFAULT NULL,
  `member_height` float DEFAULT NULL,
  `member_weight` float DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `role` enum('MEMBER','TRAINER') NOT NULL,
  `kakao_id` bigint DEFAULT NULL,
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `UKtqi1nx9ul3nx7guxpqycuvgue` (`kakao_id`),
  KEY `FKkxetm3bjsguwr3wsv9v8eteqe` (`admin_id`),
  KEY `FK69nb5p8qg4vcwj2m5nc0cnag9` (`trainer_id`),
  CONSTRAINT `FK69nb5p8qg4vcwj2m5nc0cnag9` FOREIGN KEY (`trainer_id`) REFERENCES `trainer` (`trainer_id`),
  CONSTRAINT `FKkxetm3bjsguwr3wsv9v8eteqe` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `muscle_part`
--

DROP TABLE IF EXISTS `muscle_part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `muscle_part` (
  `music_part_id` bigint NOT NULL AUTO_INCREMENT,
  `report_exercise_id` bigint DEFAULT NULL,
  PRIMARY KEY (`music_part_id`),
  KEY `FKa3px8tlv2c0p1qlh7mmt3ran4` (`report_exercise_id`),
  CONSTRAINT `FKa3px8tlv2c0p1qlh7mmt3ran4` FOREIGN KEY (`report_exercise_id`) REFERENCES `report_exercise` (`report_exercise_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint DEFAULT NULL,
  `report_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `notification_message` varchar(255) NOT NULL,
  `is_read` bit(1) DEFAULT NULL,
  PRIMARY KEY (`notification_id`),
  UNIQUE KEY `UKf6ng56xlq05jpe7xi5dp0pryj` (`report_id`),
  KEY `FK1xep8o2ge7if6diclyyx53v4q` (`member_id`),
  CONSTRAINT `FK1xep8o2ge7if6diclyyx53v4q` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKp08scjy9jhgcs9c3ccyely0ne` FOREIGN KEY (`report_id`) REFERENCES `report` (`report_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report`
--

DROP TABLE IF EXISTS `report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
  `report_id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `composition_log_id` bigint NOT NULL,
  `trainer_id` bigint NOT NULL,
  `report_comment` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`report_id`),
  UNIQUE KEY `UKgauv4rvhwjguwvos4ieefgq6j` (`composition_log_id`),
  KEY `FKel7y5wyx42a6njav1dbe2torl` (`member_id`),
  KEY `FK7evcegxt75w3cm0s419g9gj8t` (`trainer_id`),
  CONSTRAINT `FK7evcegxt75w3cm0s419g9gj8t` FOREIGN KEY (`trainer_id`) REFERENCES `trainer` (`trainer_id`),
  CONSTRAINT `FKel7y5wyx42a6njav1dbe2torl` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
  CONSTRAINT `FKi51apfu1rgfo0v0j7qnwpi3y5` FOREIGN KEY (`composition_log_id`) REFERENCES `composition_log` (`composition_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `report_exercise`
--

DROP TABLE IF EXISTS `report_exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_exercise` (
  `report_exercise_id` bigint NOT NULL AUTO_INCREMENT,
  `report_id` bigint NOT NULL,
  `exercise_name` varchar(255) DEFAULT NULL,
  `exercise_achievement` varchar(255) DEFAULT NULL,
  `exercise_comment` varchar(255) DEFAULT NULL,
  `exercise_explanation` varchar(255) DEFAULT NULL,
  `exercise_level` int DEFAULT NULL,
  PRIMARY KEY (`report_exercise_id`),
  KEY `FKdwf940v1g0d8u2912r65ox5kw` (`report_id`),
  CONSTRAINT `FKdwf940v1g0d8u2912r65ox5kw` FOREIGN KEY (`report_id`) REFERENCES `report` (`report_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `schedule`
--

DROP TABLE IF EXISTS `schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule` (
  `schedule_id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint DEFAULT NULL,
  `trainer_id` bigint DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `schedule_content` text,
  PRIMARY KEY (`schedule_id`),
  KEY `FKn7js9p799qcts7le20pec9bxr` (`member_id`),
  KEY `FKl1u2pb62wun0njqsorvvlvoy8` (`trainer_id`),
  CONSTRAINT `FKl1u2pb62wun0njqsorvvlvoy8` FOREIGN KEY (`trainer_id`) REFERENCES `trainer` (`trainer_id`),
  CONSTRAINT `FKn7js9p799qcts7le20pec9bxr` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trainer`
--

DROP TABLE IF EXISTS `trainer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainer` (
  `trainer_id` bigint NOT NULL AUTO_INCREMENT,
  `admin_id` bigint DEFAULT NULL,
  `trainer_name` varchar(255) NOT NULL,
  `trainer_login_id` varchar(255) NOT NULL,
  `trainer_pw` varchar(255) NOT NULL,
  `role` enum('MEMBER','TRAINER') NOT NULL,
  PRIMARY KEY (`trainer_id`),
  KEY `FKnw8hg3twihquui4xtusf5usc5` (`admin_id`),
  CONSTRAINT `FKnw8hg3twihquui4xtusf5usc5` FOREIGN KEY (`admin_id`) REFERENCES `admin` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `workout_muscle`
--

DROP TABLE IF EXISTS `workout_muscle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_muscle` (
  `workout_muscle_id` bigint NOT NULL AUTO_INCREMENT,
  `report_exercise_id` bigint DEFAULT NULL,
  `activation_muscle_id` bigint DEFAULT NULL,
  PRIMARY KEY (`workout_muscle_id`),
  KEY `FKojbm9ersb0vvmi0v3lb95rhn8` (`report_exercise_id`),
  CONSTRAINT `FKojbm9ersb0vvmi0v3lb95rhn8` FOREIGN KEY (`report_exercise_id`) REFERENCES `report_exercise` (`report_exercise_id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-22 13:20:59
