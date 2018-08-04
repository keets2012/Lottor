SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------

-- Table structure for `user`

-- ----------------------------

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` VARCHAR(128) NOT NULL,
  `username` VARCHAR(200) NOT NULL,
  `password` VARCHAR(200) NOT NULL,
  `self_desc` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


