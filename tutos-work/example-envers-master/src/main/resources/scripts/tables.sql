-----------------------------------------------------------
-- DDL: Data Definition Language
-- Scripts for DROP TABLES IF THEM EXIST
-----------------------------------------------------------

DROP TABLE IF EXISTS `example-envers`.`env_order_detail_aud`;
DROP TABLE IF EXISTS `example-envers`.`env_product_aud`;
DROP TABLE IF EXISTS `example-envers`.`env_order_aud`;
DROP TABLE IF EXISTS `example-envers`.`env_person_aud`;
DROP TABLE IF EXISTS `example-envers`.`env_audit_envers_info`;
DROP TABLE IF EXISTS `example-envers`.`env_order_detail`;
DROP TABLE IF EXISTS `example-envers`.`env_product`;
DROP TABLE IF EXISTS `example-envers`.`env_order`;
DROP TABLE IF EXISTS `example-envers`.`env_person`;

-----------------------------------------------------------
-- DDL: Data Definition Language
-- Scripts for CREATE TABLES
-----------------------------------------------------------

-- PERSON TABLE
CREATE TABLE `example-envers`.`env_person` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `first_name`  VARCHAR(255) DEFAULT NULL,
  `last_name`   VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- PRODUCT TABLE
CREATE TABLE `example-envers`.`env_product` (
  `product_id`        INT          NOT NULL AUTO_INCREMENT,
  `description`       VARCHAR(255) DEFAULT NULL,
  `name`              VARCHAR(255) DEFAULT NULL,
  `quantity_per_unit` INT          DEFAULT NULL,
  `unit_price`        DOUBLE       DEFAULT NULL,
  PRIMARY KEY (`product_id`)
);

-- ORDER TABLE
CREATE TABLE `example-envers`.`env_order` (
  `order_id`     INT  NOT NULL AUTO_INCREMENT,
  `order_status` INT  DEFAULT NULL,
  `sale_date`    DATE DEFAULT NULL,
  `customer_id`  INT  DEFAULT NULL,
  `employee_id`  INT  DEFAULT NULL,
  PRIMARY KEY (`order_id`),
  KEY `customer_id_pk` (`customer_id`),
  KEY `employee_id_pk` (`employee_id`),
  CONSTRAINT `employee_id_fk` FOREIGN KEY (`employee_id`) REFERENCES `env_person` (`id`),
  CONSTRAINT `customer_id_fk` FOREIGN KEY (`customer_id`) REFERENCES `env_person` (`id`)
);

-- ORDER_DETAIL TABLE
CREATE TABLE `example-envers`.`env_order_detail` (
  `order_id`   INT    NOT NULL,
  `product_id` INT    NOT NULL,
  `discount`   DOUBLE DEFAULT NULL,
  `quantity`   INT    DEFAULT NULL,
  `unit_price` DOUBLE DEFAULT NULL,
  PRIMARY KEY (`order_id`,`product_id`),
  KEY `product_id_pk` (`product_id`),
  CONSTRAINT `order_id_fk` FOREIGN KEY (`order_id`) REFERENCES `env_order` (`order_id`),
  CONSTRAINT `product_id_fk` FOREIGN KEY (`product_id`) REFERENCES `env_product` (`product_id`)
);