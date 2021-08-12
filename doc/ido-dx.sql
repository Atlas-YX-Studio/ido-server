
CREATE DATABASE IF NOT EXISTS 'ido_server' DEFAULT CHARACTER SET utf8mb4 ;

DROP TABLE IF EXISTS ido_dx_product;
CREATE TABLE ido_dx_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdName VARCHAR(100) NOT NULL COMMENT '项目名称/代币全称',
    currency VARCHAR(10) NOT NULL COMMENT '需要购买的代币名称',
    baseCurrency VARCHAR(10) NOT NULL COMMENT '用户质押币种名称',
    rate DECIMAL(18,8) NOT NULL COMMENT '兑换率',
    raiseTotal DECIMAL(36,18) NOT NULL COMMENT '筹集总量',
    currencyTotal DECIMAL(36,18) NOT NULL COMMENT '代币预发行总量',
    address VARCHAR(300) NOT NULL COMMENT '公链地址',
    tokenPrecision SMALLINT DEFAULT 9 COMMENT '精度',
    icon VARCHAR(500) NOT NULL COMMENT '图标',
    state VARCHAR(20) NOT NULL COMMENT '项目状态：init，processing，finish',
    prdDesc VARCHAR(2500) NOT NULL COMMENT '项目描述',
    ruleDesc VARCHAR(2500) NOT NULL COMMENT '规则描述',
    prdDescEn VARCHAR(2500) NOT NULL COMMENT '项目描述',
    ruleDescEn VARCHAR(2500) NOT NULL COMMENT '规则描述',
    createTime BIGINT(20) NOT NULL COMMENT '创建时间',
    updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
    startTime BIGINT(20) NOT NULL COMMENT '项目开始时间',
    endTime BIGINT(20) NOT NULL COMMENT '项目结束时间',
    pledgeStartTime BIGINT(20) NOT NULL COMMENT '质押开始时间',
    pledgeEndTime BIGINT(20) NOT NULL COMMENT '质押结束时间',
    lockStartTime BIGINT(20) NOT NULL COMMENT '锁仓开始时间',
    lockEndTime BIGINT(20) NOT NULL COMMENT '锁仓结束时间',
    payStartTime BIGINT(20) NOT NULL COMMENT '支付开始时间',
    payEndTime BIGINT(20) NOT NULL COMMENT '支付结束时间',
    assignmentStartTime BIGINT(20) NOT NULL COMMENT '代币分配开始时间',
    assignmentEndTime BIGINT(20) NOT NULL COMMENT '代币分配结束时间',
    PRIMARY KEY(id),
    INDEX idx_stime(startTime),
    INDEX idx_etime(endTime),
    INDEX idx_state(state),
    INDEX idx_state(address)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '打新项目详情';

DROP TABLE IF EXISTS ido_dx_label;
CREATE TABLE ido_dx_label (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdId BIGINT(20) NOT NULL COMMENT '项目Id',
    label VARCHAR(50) NOT NULL COMMENT '标签',
    createTime BIGINT(20) NOT NULL COMMENT '创建时间',
    updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(id),
    INDEX idx_pid(prdId)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '打新card标签';

DROP TABLE IF EXISTS ido_dx_link;
CREATE TABLE ido_dx_link (
     id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
     prdId BIGINT(20) NOT NULL COMMENT '项目Id',
     name VARCHAR(50) NOT NULL COMMENT '快捷链接名称：facebook twitter youtube telegram medium instagram weibo',
     url VARCHAR(250) NOT NULL COMMENT '快捷链接',
     createTime BIGINT(20) NOT NULL COMMENT '创建时间',
     updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
     PRIMARY KEY(id),
     INDEX idx_pid(prdId)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '打新card快捷链接';

DROP TABLE IF EXISTS ido_dx_attribute;
CREATE TABLE ido_dx_attribute (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdId BIGINT(20) NOT NULL COMMENT '项目Id',
    name VARCHAR(50) NOT NULL COMMENT '属性名称',
    createTime BIGINT(20) NOT NULL COMMENT '创建时间',
    updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(id),
    INDEX idx_pid(prdId)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '打新card属性';

DROP TABLE IF EXISTS ido_dx_user_record;
CREATE TABLE ido_dx_user_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdAddress VARCHAR(50) NOT NULL COMMENT '项目公链地址',
    userAddress VARCHAR(50) NOT NULL COMMENT '用户地址',
    amount DECIMAL(36,18) NOT NULL COMMENT '该用户质押量',
    tokenAmount DECIMAL(36,18) DEFAULT -1 COMMENT '链上实际质押量',
    gasCost DECIMAL(36,18) DEFAULT -1 COMMENT 'gas费用',
    currency VARCHAR(10) NOT NULL COMMENT '质押币种',
    extInfo VARCHAR(2500) DEFAULT NULL COMMENT '扩展字段：质押/解押记录 等',
    tokenVersion SMALLINT DEFAULT 0 COMMENT '更新链上实际质押量 版本号',
    createTime BIGINT(20) NOT NULL COMMENT '创建时间',
    updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(id),
    INDEX idx_pid(prdId),
    INDEX idx_address(userAddress)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '打新用户质押记录';
