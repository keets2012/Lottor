
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` varchar(128) NOT NULL,
  `permission` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `permission_index` (`permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `permission` WRITE;
INSERT INTO `permission` VALUES ('4fcd9da1-faa7-49be-80e2-c47ea8c1af98','ALL','所有权限'),('a38e277a-0b04-4347-ae92-9b653bd0d15b','READ_ONLY','只读权限');
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` varchar(128) NOT NULL,
  `name` varchar(255) NOT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_name_desc_index` (`name`,`description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT INTO `role` VALUES ('a309b198-e5cc-4cc4-b08b-12c91ead3b1a','admin','2018-08-04 03:53:11','管理员'),('ce804be7-b2aa-486e-87cb-c39916f7683d','employee','2018-08-04 03:53:12','普通员工');
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` varchar(128) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  `permission_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
INSERT INTO `role_permission` VALUES ('2b6fc54a-1602-466d-b950-d19d259049f8','ce804be7-b2aa-486e-87cb-c39916f7683d','a38e277a-0b04-4347-ae92-9b653bd0d15b'),('c6d52825-d50d-4ad6-aa3f-54debcc3a4fa','a309b198-e5cc-4cc4-b08b-12c91ead3b1a','4fcd9da1-faa7-49be-80e2-c47ea8c1af98');
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` varchar(128) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `role_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
INSERT INTO `user_role` VALUES ('02f489b0-35c2-47b0-b8cb-395324e8e322','650f0ace-0b92-4e0e-a5c5-a2522c669de8','a309b198-e5cc-4cc4-b08b-12c91ead3b1a');
UNLOCK TABLES;
