
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ido_server DEFAULT CHARACTER SET utf8mb4 ;

-- 给用户授权
grant all privileges on xxl_job.* to 'admin'@'localhost' identified by 'OdeWzZNalcTPk2LAo0Lg';
grant all privileges on xxl_job.* to 'admin'@'%' identified by 'OdeWzZNalcTPk2LAo0Lg';
flush privileges;

use ido_server;

DROP TABLE IF EXISTS ido_dx_product;
CREATE TABLE ido_dx_product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdName VARCHAR(128) DEFAULT NULL COMMENT '项目名称/代币全称',
    pledgeCurrency VARCHAR(128) DEFAULT NULL COMMENT '质押币种',
    pledgePrecision SMALLINT DEFAULT 9 COMMENT '质押精度',
    pledgeAddress VARCHAR(128) DEFAULT NULL COMMENT '质押地址',
    payCurrency VARCHAR(128) DEFAULT NULL COMMENT '支付币种',
    payPrecision SMALLINT DEFAULT 9 COMMENT '支付精度',
    payAddress VARCHAR(128) DEFAULT NULL COMMENT '支付地址',
    assignCurrency VARCHAR(128) DEFAULT NULL COMMENT '分配币种',
    assignPrecision SMALLINT DEFAULT 9 COMMENT '分配精度',
    assignAddress VARCHAR(128) DEFAULT NULL COMMENT '分配地址',
    rate DECIMAL(18,9) DEFAULT 0 COMMENT '兑换率',
    raiseTotal DECIMAL(36,18) DEFAULT 0 COMMENT '筹集总量',
    currencyTotal DECIMAL(36,18) DEFAULT 0 COMMENT '代币预发行总量',
    saleTotal DECIMAL(36,18) DEFAULT 0 COMMENT '总销售量',
    icon VARCHAR(500) DEFAULT NULL COMMENT '图标',
    state VARCHAR(20) DEFAULT NULL COMMENT '项目状态：init，processing，finish',
    prdDesc VARCHAR(2500) DEFAULT NULL COMMENT '项目描述',
    prdDescEn VARCHAR(2500) DEFAULT NULL COMMENT '项目描述',
    ruleDesc VARCHAR(2500) DEFAULT NULL COMMENT '规则描述',
    ruleDescEn VARCHAR(2500) DEFAULT NULL COMMENT '规则描述',
    createTime BIGINT(20) DEFAULT NULL COMMENT '创建时间',
    updateTime BIGINT(20) DEFAULT NULL COMMENT '更新时间',
    startTime BIGINT(20) DEFAULT NULL COMMENT '项目开始时间',
    endTime BIGINT(20) DEFAULT NULL COMMENT '项目结束时间',
    pledgeStartTime BIGINT(20) DEFAULT NULL COMMENT '质押开始时间',
    pledgeEndTime BIGINT(20) DEFAULT NULL COMMENT '质押结束时间',
    lockStartTime BIGINT(20) DEFAULT NULL COMMENT '锁仓开始时间',
    lockEndTime BIGINT(20) DEFAULT NULL COMMENT '锁仓结束时间',
    payStartTime BIGINT(20) DEFAULT NULL COMMENT '支付开始时间',
    payEndTime BIGINT(20) DEFAULT NULL COMMENT '支付结束时间',
    assignmentStartTime BIGINT(20) DEFAULT NULL COMMENT '代币分配开始时间',
    assignmentEndTime BIGINT(20) DEFAULT NULL COMMENT '代币分配结束时间',
    PRIMARY KEY(id),
    INDEX idx_stime(startTime),
    INDEX idx_etime(endTime),
    INDEX idx_state(state),
    INDEX idx_sadd(pledgeAddress)
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
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '标签';

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
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '快捷链接';

DROP TABLE IF EXISTS ido_dx_attribute;
CREATE TABLE ido_dx_attribute (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdId BIGINT(20) NOT NULL COMMENT '项目Id',
    name VARCHAR(50) NOT NULL COMMENT '属性名称',
    createTime BIGINT(20) NOT NULL COMMENT '创建时间',
    updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(id),
    INDEX idx_pid(prdId)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '属性';

DROP TABLE IF EXISTS ido_dx_user_record;
CREATE TABLE ido_dx_user_record (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
    prdAddress VARCHAR(128) NOT NULL COMMENT '项目公链地址、质押币种地址',
    userAddress VARCHAR(128) NOT NULL COMMENT '用户地址',
    amount DECIMAL(36,18) NOT NULL COMMENT '该用户质押量',
    tokenAmount DECIMAL(36,18) DEFAULT -1 COMMENT '链上实际质押量',
    gasCost DECIMAL(36,18) DEFAULT -1 COMMENT 'gas费用',
    currency VARCHAR(10) NOT NULL COMMENT '质押币种',
    extInfo VARCHAR(2500) DEFAULT NULL COMMENT '扩展字段：质押/解押记录 等',
    tokenVersion SMALLINT DEFAULT 0 COMMENT '更新链上实际质押量 版本号',
    createTime BIGINT(20) NOT NULL COMMENT '创建时间',
    updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(id),
    INDEX idx_pid(prdAddress),
    INDEX idx_address(userAddress)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT '打新用户质押记录';

-- swap

DROP TABLE IF EXISTS swap_user_record;
CREATE TABLE swap_user_record (
        id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
        userAddress VARCHAR(128) NOT NULL COMMENT '用户地址',
        tokenCodeX VARCHAR(64) NOT NULL COMMENT 'x币种',
        tokenCodeY VARCHAR(64) NOT NULL COMMENT 'y币种',
        tokenInX DECIMAL(36,18) DEFAULT 0 COMMENT 'x注入量',
        tokenInY DECIMAL(36,18) DEFAULT 0 COMMENT 'y注入量',
        tokenOutX DECIMAL(36,18) DEFAULT 0 COMMENT 'x提取量',
        tokenOutY DECIMAL(36,18) DEFAULT 0 COMMENT 'y提取量',
        reserveAmountX DECIMAL(36,18) DEFAULT 0 COMMENT 'x储备量',
        reserveAmountY DECIMAL(36,18) DEFAULT 0 COMMENT 'y储备量',
        swapTime BIGINT(20) NOT NULL COMMENT '兑换成功时间',
        createTime BIGINT(20) NOT NULL COMMENT '创建时间',
        PRIMARY KEY(id),
        INDEX idx_address(userAddress)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT 'swap兑换成功记录';

DROP TABLE IF EXISTS liquidity_user_record;
CREATE TABLE liquidity_user_record (
      id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
      userAddress VARCHAR(128) NOT NULL COMMENT '用户地址',
      tokenCodeX VARCHAR(64) NOT NULL COMMENT 'x币种',
      tokenCodeY VARCHAR(64) NOT NULL COMMENT 'y币种',
      direction SMALLINT DEFAULT -1 COMMENT '1注入，0提取',
      amountX DECIMAL(36,18) DEFAULT 0 COMMENT 'x注入/提取量',
      amountY DECIMAL(36,18) DEFAULT 0 COMMENT 'y注入/提取量',
      reserveAmountX DECIMAL(36,18) DEFAULT 0 COMMENT 'x储备量',
      reserveAmountY DECIMAL(36,18) DEFAULT 0 COMMENT 'y储备量',
      liquidityTime BIGINT(20) NOT NULL COMMENT '注入/提取时间',
      createTime BIGINT(20) NOT NULL COMMENT '创建时间',
      PRIMARY KEY(id),
      INDEX idx_address(userAddress)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT 'swap用户注入提取记录';

DROP TABLE IF EXISTS swap_coins;
CREATE TABLE swap_coins (
       id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键id',
       shortName VARCHAR(100) NOT NULL COMMENT '币种简称',
       fullName VARCHAR(100) NOT NULL COMMENT '币种全程',
       icon VARCHAR(512) NOT NULL COMMENT '币种图标',
       address VARCHAR(512) NOT NULL COMMENT '币种地址',
       weight INT(9) DEFAULT 0 COMMENT '权重，用于排序，默认0，则根据id进行排序',
       exchangePrecision SMALLINT DEFAULT 9 COMMENT '交易精度',
       displayPrecision SMALLINT DEFAULT 9 COMMENT '展示精度',
       createTime BIGINT(20) NOT NULL COMMENT '创建时间',
       updateTime BIGINT(20) NOT NULL COMMENT '更新时间',
       PRIMARY KEY(id),
       INDEX idx_sname(shortName),
       INDEX idx_address(address)
) Engine=INNODB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT 'swap币种列表';


