CREATE DATABASE `sagedb_bison` /*!40100 DEFAULT CHARACTER SET latin1 */;

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `user_email` varchar(45) NOT NULL,
  `_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_email_UNIQUE` (`user_email`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `android_node` (
  `android_id` varchar(64) NOT NULL,
  `node_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `info` blob,
  `_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`node_id`),
  UNIQUE KEY `android_id_UNIQUE` (`android_id`),
  KEY `fk_node_owner_idx` (`owner_id`),
  CONSTRAINT `fk_node_owner` FOREIGN KEY (`owner_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `java` (
  `java_id` int(11) NOT NULL AUTO_INCREMENT,
  `creator_id` int(11) NOT NULL,
  `encoded_java` longtext NOT NULL,
  `encoded_dex` longtext,
  `_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`java_id`),
  KEY `fk_java_user_idx` (`creator_id`),
  CONSTRAINT `fk_java_user` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `job` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `orderer_id` int(11) NOT NULL,
  `node_id` int(11) DEFAULT NULL,
  `java_id` int(11) NOT NULL,
  `bounty` decimal(19,4) NOT NULL,
  `status` varchar(8) NOT NULL,
  `timeout` bigint(64) NOT NULL,
  `completion` datetime DEFAULT NULL,
  `data` longtext,
  `result` longtext,
  `_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`job_id`),
  KEY `fk_job_user_idx` (`orderer_id`),
  KEY `fk_job_android_node_idx` (`node_id`),
  KEY `fk_job_java_idx` (`java_id`),
  CONSTRAINT `fk_job_user` FOREIGN KEY (`orderer_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_job_android_node` FOREIGN KEY (`node_id`) REFERENCES `android_node` (`node_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_job_java` FOREIGN KEY (`java_id`) REFERENCES `java` (`java_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




