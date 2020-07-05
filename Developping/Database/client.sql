# SQL-Front 5.1  (Build 4.16)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: localhost    Database: tetris
# ------------------------------------------------------
# Server version 5.5.16

DROP DATABASE IF EXISTS `tetris`;
CREATE DATABASE `tetris` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `tetris`;

#
# Source for table client
#

DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `username` varchar(30) CHARACTER SET ascii NOT NULL DEFAULT '',
  `password` varchar(255) CHARACTER SET ascii DEFAULT NULL,
  `score` int(11) DEFAULT '0',
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

#
# Dumping data for table client
#

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
INSERT INTO `client` VALUES ('binh','1',24,NULL);
INSERT INTO `client` VALUES ('cuong','1',841,NULL);
INSERT INTO `client` VALUES ('duc','1',42,NULL);
INSERT INTO `client` VALUES ('harunaga','1',493,NULL);
INSERT INTO `client` VALUES ('hung','1',0,NULL);
INSERT INTO `client` VALUES ('manh','1',0,NULL);
INSERT INTO `client` VALUES ('tai','1',0,NULL);
INSERT INTO `client` VALUES ('tung','1',24,NULL);
INSERT INTO `client` VALUES ('xuan','1',2651,NULL);
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
