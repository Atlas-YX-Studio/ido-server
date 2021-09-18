CREATE TABLE `nft_group`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `series`              varchar(32)  NOT NULL COMMENT '系列',
    `series_name`         varchar(128) NOT NULL COMMENT '系列名',
    `name`                varchar(32)  NOT NULL COMMENT '组名',
    `quantity`            int(11) NOT NULL COMMENT '发售数量',
    `series_quantity`     int(11) NOT NULL COMMENT '系列总发售数量',
    `box_token`           varchar(128) NOT NULL COMMENT '盲盒币种',
    `box_token_precision` int(11) NOT NULL COMMENT '盲盒币种精度',
    `box_token_logo`      varchar(128) NOT NULL COMMENT '盲盒图片',
    `pay_token`           varchar(128) NOT NULL COMMENT '支付币种',
    `pay_token_precision` int(11) NOT NULL COMMENT '支付币种精度',
    `nft_meta`            varchar(128) NOT NULL COMMENT 'nft_meta地址',
    `nft_body`            varchar(128) NOT NULL COMMENT 'nft_body地址',
    `nft_type_info`       varchar(128) NOT NULL COMMENT 'nft_type_info地址',
    `selling_price`       int(11) NOT NULL COMMENT '发售价格',
    `selling_time`        bigint(20) NOT NULL COMMENT '开售时间',
    `cn_description`      text COMMENT '中文描述',
    `en_description`      text COMMENT '英文描述',
    `cn_rule_desc`        text COMMENT '中文规则',
    `en_rule_desc`        text COMMENT '英文规则',
    `cn_creator_desc`     text COMMENT '创作者中文描述',
    `en_creator_desc`     text COMMENT '创作者英文描述',
    `enabled`             tinyint(1) NOT NULL COMMENT '是否展示',
    `offering`            tinyint(1) NOT NULL COMMENT '是否激活',
    `initialized`         tinyint(1) NOT NULL COMMENT '是否初始化',
    `create_time`         bigint(20) NOT NULL COMMENT '创建时间',
    `update_time`         bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='NFT分组表';

CREATE TABLE `nft_info`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `nft_id`      bigint(20) NOT NULL COMMENT 'NFT id',
    `group_id`    bigint(20) NOT NULL COMMENT '所属分组',
    `name`        varchar(128) NOT NULL COMMENT '名称',
    `image_link`  varchar(256) NOT NULL COMMENT '图片链接',
    `image_data`  text COMMENT '图片数据',
    `creator`     varchar(64)  NOT NULL COMMENT '创作者',
    `created`     tinyint(1) NOT NULL COMMENT '已创建',
    `create_time` bigint(20) NOT NULL COMMENT '创建时间',
    `update_time` bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY           `idx_nft_id` (`nft_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='NFT信息记录表';

CREATE TABLE `nft_kiko_cat`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `nft_id`           bigint(20) NOT NULL COMMENT 'NFT id',
    `background`       varchar(128) NOT NULL COMMENT '背景',
    `background_score` int(11) NOT NULL COMMENT '背景分',
    `breed`            varchar(128) NOT NULL COMMENT '品种',
    `breed_score`      int(11) NOT NULL COMMENT '品种分',
    `decorate`         varchar(128) NOT NULL COMMENT '装饰',
    `decorate_score`   int(11) NOT NULL COMMENT '装饰分',
    `score`            int(11) NOT NULL COMMENT '分数',
    `order`            int(11) NOT NULL COMMENT '排名',
    `create_time`      bigint(20) NOT NULL COMMENT '创建时间',
    `update_time`      bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY                `idx_nft_id` (`nft_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='NFT Kiko猫信息表';


DROP TABLE IF EXISTS nft_market;
CREATE TABLE nft_market
(
    id               bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    chain_id         bigint(20) NOT NULL COMMENT '链上 id',
    nft_box_id       bigint(20) NOT NULL COMMENT 'nft_info表的id',
    group_id         bigint(20) NOT NULL COMMENT 'nft_group表的id',
    type             varchar(64) DEFAULT NULL COMMENT '类型：nft/box',
    name             varchar(128) DEFAULT NULL COMMENT 'ndf/box 全称',
    owner            varchar(128) DEFAULT NULL COMMENT '当前持有者',
    address          varchar(128) DEFAULT NULL COMMENT '合约地址',
    sell_price       DECIMAL(36,18) DEFAULT 0 COMMENT '售价',
    offer_price      DECIMAL(36,18) DEFAULT 0 COMMENT '报价，0暂无报价，大于0为当前最高出价',
    icon             varchar(256) DEFAULT NULL COMMENT '图片地址',
    create_time      bigint(20) NOT NULL COMMENT '创建时间',
    update_time      bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX idx_nb_id(nft_box_id),
    INDEX idx_address_type(address, type)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8mb4 COMMENT='NFT/box市场销售列表';


