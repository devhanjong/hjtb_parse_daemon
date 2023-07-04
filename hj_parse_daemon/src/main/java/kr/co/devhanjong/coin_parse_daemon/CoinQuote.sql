

DROP TABLE coin_quote IF EXISTS;
DROP TABLE coin_quote_history IF EXISTS;

CREATE TABLE `coin_quote` (
`coin_type` VARCHAR(10) NOT NULL COLLATE 'utf8_general_ci',
`usdt` DECIMAL(10,2) NULL DEFAULT '0.00',
`price` DECIMAL(16,2) NULL DEFAULT '0.00',
`reg_date` DATETIME(3) NULL DEFAULT NULL,
PRIMARY KEY (`coin_type`) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;


CREATE TABLE `coin_quote_history` (
`seq` INT(11) NOT NULL AUTO_INCREMENT,
`coin_type` VARCHAR(10) NOT NULL COLLATE 'utf8_general_ci',
`usdt` DECIMAL(10,2) NULL DEFAULT '0.00',
`price` DECIMAL(16,2) NULL DEFAULT '0.00',
`reg_date` DATETIME(3) NULL DEFAULT NULL,
PRIMARY KEY (`seq`) USING BTREE
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
ROW_FORMAT=DYNAMIC
;

DELETE FROM `coin_quote` WHERE `coin_type`='BCH';
INSERT INTO `coin_quote` (`coin_type`, `usdt`, `price`, `reg_date`) VALUES ('BCH', 0.00, 1000.00, '2021-01-22 18:25:36.000');
DELETE FROM `coin_quote` WHERE `coin_type`='BTC';
INSERT INTO `coin_quote` (`coin_type`, `usdt`, `price`, `reg_date`) VALUES ('BTC', 0.00, 20000000.00, '2020-12-01 09:48:01.000');
DELETE FROM `coin_quote` WHERE `coin_type`='ETH';
INSERT INTO `coin_quote` (`coin_type`, `usdt`, `price`, `reg_date`) VALUES ('ETH', 0.00, 2000000.00, '2018-11-12 10:24:18.519');


