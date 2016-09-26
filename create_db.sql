CREATE DATABASE IF NOT EXISTS `inventory` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `inventory`;

--
-- Table structure for table `asset`
--

DROP TABLE IF EXISTS `asset`;
CREATE TABLE `asset` (
  `id`               BIGINT(20) NOT NULL AUTO_INCREMENT,
  `comment`          VARCHAR(255)        DEFAULT NULL,
  `inventory_number` VARCHAR(255)        DEFAULT NULL,
  `serial_number`    VARCHAR(255)        DEFAULT NULL,
  `status`           VARCHAR(255)        DEFAULT NULL,
  `device_model_id`  BIGINT(20)          DEFAULT NULL,
  `employee_id`      BIGINT(20)          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbqpd37upa0kv7s84u9qbu358m` (`device_model_id`),
  KEY `FK3u2kcr6mkx71ijin5xoxq0kxn` (`employee_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `cost_center`
--

DROP TABLE IF EXISTS `cost_center`;
CREATE TABLE `cost_center` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255)          DEFAULT NULL,
  `number`      VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `device_brand`
--

DROP TABLE IF EXISTS `device_brand`;
CREATE TABLE `device_brand` (
  `id`    BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `brand` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `device_model`
--

DROP TABLE IF EXISTS `device_model`;
CREATE TABLE `device_model` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `item_number`     VARCHAR(255)          DEFAULT NULL,
  `model`           VARCHAR(255) NOT NULL,
  `device_brand_id` BIGINT(20)            DEFAULT NULL,
  `device_type_id`  BIGINT(20)            DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm4tclb91khee4msei6gskef6b` (`device_brand_id`),
  KEY `FKtnd4gbur7eiutcpsfbr9knajq` (`device_type_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `device_type`
--

DROP TABLE IF EXISTS `device_type`;
CREATE TABLE `device_type` (
  `id`   BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `first_name`     VARCHAR(255) NOT NULL,
  `last_name`      VARCHAR(255) NOT NULL,
  `middle_name`    VARCHAR(255)          DEFAULT NULL,
  `position`       VARCHAR(255) NOT NULL,
  `cost_center_id` BIGINT(20)            DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb8o64tbj1yboy7s34v8wqchfi` (`cost_center_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `employee_asset`
--

DROP TABLE IF EXISTS `employee_asset`;
CREATE TABLE `employee_asset` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `asset_id`    BIGINT(20)          DEFAULT NULL,
  `employee_id` BIGINT(20)          DEFAULT NULL,
  `invoice_id`  BIGINT(20)          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfe98dtvv56yumjke21gt8h4gm` (`asset_id`),
  KEY `FK1qxg5t6hoo8po9fly0dilnu1b` (`employee_id`),
  KEY `FK46kpfruo4cm71nluo7kk51gyp` (`invoice_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
CREATE TABLE `invoice` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `date`        DATETIME            DEFAULT NULL,
  `status`      VARCHAR(255)        DEFAULT NULL,
  `employee_id` BIGINT(20)          DEFAULT NULL,
  `issuer_id`   BIGINT(20)          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKau92vqwrrlsflir3v65262ucw` (`employee_id`),
  KEY `FK3parkudt5pmwq27irionsxtf3` (`issuer_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `tracking`
--

DROP TABLE IF EXISTS `tracking`;
CREATE TABLE `tracking` (
  `id`        BIGINT(20) NOT NULL AUTO_INCREMENT,
  `date`      DATETIME            DEFAULT NULL,
  `event`     VARCHAR(255)        DEFAULT NULL,
  `asset_id`  BIGINT(20)          DEFAULT NULL,
  `issuer_id` BIGINT(20)          DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKko4xoanes4fbj3128m95c9dhs` (`asset_id`),
  KEY `FKjut5huc7kyrm5x613sq0l4y6u` (`issuer_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `first_name`  VARCHAR(255) NOT NULL,
  `last_name`   VARCHAR(255) NOT NULL,
  `login_name`  VARCHAR(255) NOT NULL,
  `middle_name` VARCHAR(255)          DEFAULT NULL,
  `position`    VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE asset
  ADD CONSTRAINT `FK3u2kcr6mkx71ijin5xoxq0kxn` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  ADD CONSTRAINT `FKbqpd37upa0kv7s84u9qbu358m` FOREIGN KEY (`device_model_id`) REFERENCES `device_model` (`id`);

ALTER TABLE device_model
  ADD CONSTRAINT `FKm4tclb91khee4msei6gskef6b` FOREIGN KEY (`device_brand_id`) REFERENCES `device_brand` (`id`),
  ADD CONSTRAINT `FKtnd4gbur7eiutcpsfbr9knajq` FOREIGN KEY (`device_type_id`) REFERENCES `device_type` (`id`);

ALTER TABLE employee
  ADD CONSTRAINT `FKb8o64tbj1yboy7s34v8wqchfi` FOREIGN KEY (`cost_center_id`) REFERENCES `cost_center` (`id`);

ALTER TABLE employee_asset
  ADD CONSTRAINT `FK1qxg5t6hoo8po9fly0dilnu1b` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  ADD CONSTRAINT `FK46kpfruo4cm71nluo7kk51gyp` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`),
  ADD CONSTRAINT `FKfe98dtvv56yumjke21gt8h4gm` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`);

ALTER TABLE invoice
  ADD CONSTRAINT `FK3parkudt5pmwq27irionsxtf3` FOREIGN KEY (`issuer_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKau92vqwrrlsflir3v65262ucw` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`);

ALTER TABLE tracking
  ADD CONSTRAINT `FKjut5huc7kyrm5x613sq0l4y6u` FOREIGN KEY (`issuer_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKko4xoanes4fbj3128m95c9dhs` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`id`);

CREATE USER 'cis'@'localhost'
  IDENTIFIED BY 'P@ssw0rd';
GRANT ALL PRIVILEGES ON inventory.* TO 'cis'@'localhost';
