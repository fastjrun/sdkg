-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 192.168.5.217    Database: fast_demo
-- ------------------------------------------------------
-- Server version	5.7.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_user`
--

DROP TABLE IF EXISTS `t_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `loginName` varchar(50) DEFAULT NULL,
  `loginPwd` char(32) DEFAULT NULL,
  `mobileNo` varchar(20) DEFAULT NULL COMMENT '手机号',
  `nickName` varchar(20) DEFAULT NULL COMMENT '昵称',
  `sex` smallint(6) DEFAULT '1' COMMENT '1：男；2：女：3；未知',
  `email` char(30) DEFAULT NULL COMMENT '邮件',
  `createTime` datetime DEFAULT NULL,
  `lastModifyTime` datetime DEFAULT NULL,
  `lastLoginTime` char(17) DEFAULT NULL,
  `loginErrCount` smallint(6) DEFAULT '0',
  `lastRecordLoginErrTime` char(17) DEFAULT NULL,
  `status` char(1) DEFAULT '1' COMMENT '1：正常；2：密码锁定；3：人工锁定',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `t_user_login`
--

DROP TABLE IF EXISTS `t_user_login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_user_login` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` int(11) NOT NULL,
  `deviceId` char(32) DEFAULT NULL,
  `loginCredential` char(32) DEFAULT NULL,
  `createTime` char(17) DEFAULT NULL,
  `logOutTime` char(17) DEFAULT NULL COMMENT '凭证实际注销时间',
  `inValidateTime` char(17) DEFAULT NULL COMMENT '按照系统设置，凭证应该失效的时间',
  `status` char(1) DEFAULT '1' COMMENT '1：正常；2：无效',
  PRIMARY KEY (`id`)
) COMMENT='用户登录表' ENGINE=InnoDB;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-10-20 22:31:41
