DROP TABLE IF EXISTS trading_reward_users;
CREATE TABLE `trading_reward_users` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `address` varchar(64) NOT NULL COMMENT '用户地址',
    `locked_reward` DECIMAL(38, 9) DEFAULT 0 COMMENT '当前收益',
    `freed_reward` DECIMAL(38, 9) DEFAULT 0 COMMENT '累计收益',
    `create_time` bigint(20) NOT NULL COMMENT '创建时间',
    `update_time` bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='用户交易挖矿收益表';

DROP TABLE IF EXISTS trading_pool_users;
CREATE TABLE `trading_pool_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `address` varchar(64) NOT NULL COMMENT '用户地址',
  `pool_id` bigint(20) NOT NULL COMMENT '矿池id',
  `current_trading_amount` decimal(38,9) DEFAULT 0 COMMENT '当前交易额',
  `total_trading_amount` decimal(38,9) DEFAULT 0 COMMENT '累计交易额',
  `current_reward` decimal(38,9) DEFAULT 0 COMMENT '当前收益',
  `total_reward` decimal(38,9) DEFAULT 0 COMMENT '累计收益',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_address_pool_id` (`address`,`pool_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='用户交易挖矿表';

DROP TABLE IF EXISTS trading_pools;
CREATE TABLE `trading_pools` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `pair_name` varchar(32) NOT NULL COMMENT '交易对名',
    `token_a` varchar(128) NOT NULL COMMENT '币种A',
    `token_b` varchar(128) NOT NULL COMMENT '币种B',
    `allocation_multiple` int(11) NOT NULL DEFAULT 0 COMMENT '奖励分配倍数',
    `current_trading_amount` DECIMAL(38, 9) DEFAULT 0 COMMENT '当前交易额',
    `total_trading_amount` DECIMAL(38, 9) DEFAULT 0 COMMENT '累计交易额',
    `allocated_reward_amount` DECIMAL(38, 9) DEFAULT 0 COMMENT '已分配奖励',
    `weight` int(11) NOT NULL DEFAULT 0 COMMENT '权重',
    `create_time` bigint(20) NOT NULL COMMENT '创建时间',
    `update_time` bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='交易挖矿矿池表';

DROP TABLE IF EXISTS trading_reward_users;
CREATE TABLE trading_reward_users (
id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
address varchar(64) NOT NULL COMMENT '用户地址',
locked_reward decimal(38,9) DEFAULT '0.000000000' COMMENT '锁定收益',
freed_reward decimal(38,9) DEFAULT '0.000000000' COMMENT '已释放收益',
unlock_reward_per_day decimal(38,9) DEFAULT NULL COMMENT '每日解锁收益',
pending_reward decimal(38,9) DEFAULT '0.000000000' COMMENT '待结算收益',
next_unlock_time bigint(20) NOT NULL COMMENT '下次解锁时间',
create_time bigint(20) NOT NULL COMMENT '创建时间',
update_time bigint(20) NOT NULL COMMENT '更新时间',
PRIMARY KEY (id) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='用户交易挖矿收益表';

DROP TABLE IF EXISTS lp_mining_pools;
CREATE TABLE `lp_mining_pools` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `pair_name` varchar(32) NOT NULL COMMENT '交易对名',
    `token_a` varchar(128) NOT NULL COMMENT '币种A',
    `token_b` varchar(128) NOT NULL COMMENT '币种B',
    `allocation_multiple` int(11) NOT NULL DEFAULT 0 COMMENT '奖励分配倍数',
    `total_staking_amount` DECIMAL(38, 9) DEFAULT 0 COMMENT '总质押量',
    `weight` int(11) NOT NULL DEFAULT 0 COMMENT '权重',
    `create_time` bigint(20) NOT NULL COMMENT '创建时间',
    `update_time` bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='lp挖矿矿池表';

DROP TABLE IF EXISTS lp_mining_pool_users;
CREATE TABLE `lp_mining_pool_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `address` varchar(64) NOT NULL COMMENT '用户地址',
  `pool_id` bigint(20) NOT NULL COMMENT '矿池id',
  `staking_amount` decimal(38,9) DEFAULT 0 COMMENT '质押量',
  `reward` decimal(38,9) DEFAULT 0 COMMENT '收益',
  `pending_reward` decimal(38,9) DEFAULT 0 COMMENT '收益',
  `create_time` bigint(20) NOT NULL COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_address_pool_id` (`address`,`pool_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='用户lp挖矿表';

DROP TABLE IF EXISTS lp_staking_records;
CREATE TABLE `lp_staking_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `pair_name` varchar(32) NOT NULL COMMENT '交易对名',
  `token_a` varchar(128) NOT NULL COMMENT '币种A',
  `token_b` varchar(128) NOT NULL COMMENT '币种B',
  `type` varchar(32) NOT NULL COMMENT '操作类型: stake/unstake',
  `amount` decimal(38,9) DEFAULT '0.000000000' COMMENT '额度',
  `seq_id` bigint NOT NULL COMMENT '序列id',
  `create_time` bigint NOT NULL COMMENT '创建时间',
  `update_time` bigint NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='用户lp质押/解押记录表';