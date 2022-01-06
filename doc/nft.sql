DROP TABLE IF EXISTS nft_group;
CREATE TABLE `nft_group`
(
    `id`                  bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `series`              varchar(32)  NOT NULL COMMENT '系列',
    `series_name`         varchar(128) NOT NULL COMMENT '系列名',
    `name`                varchar(32)  NOT NULL COMMENT '组名',
    `type`                varchar(32)  NOT NULL COMMENT '类型',
    `series_quantity`     int(11)      NOT NULL COMMENT '系列总发售数量',
    `quantity`            int(11)      NOT NULL COMMENT '发售数量',
    `offering_quantity`   int(11)      NOT NULL COMMENT '发售数量',
    `box_token`           varchar(128) NOT NULL COMMENT '盲盒币种',
    `box_token_precision` int(11)      NOT NULL COMMENT '盲盒币种精度',
    `box_token_logo`      varchar(256) NOT NULL COMMENT '盲盒图片',
    `pay_token`           varchar(128) NOT NULL COMMENT '支付币种',
    `pay_token_precision` int(11)      NOT NULL COMMENT '支付币种精度',
    `support_token`       text COMMENT '支持币种',
    `nft_meta`            varchar(128) NOT NULL COMMENT 'nft_meta地址',
    `nft_body`            varchar(128) NOT NULL COMMENT 'nft_body地址',
    `nft_type_info`       varchar(128) NOT NULL COMMENT 'nft_type_info地址',
    `nft_type_image_link` varchar(256) NOT NULL COMMENT 'NFT分组图片',
    `nft_type_image_data` text COMMENT 'NFT分组图片',
    `creator`             varchar(128) NOT NULL COMMENT '创作者地址',
    `owner`               varchar(128) NOT NULL COMMENT '所有者',
    `selling_price`       int(11)      NOT NULL COMMENT '发售价格',
    `selling_time`        bigint(20)   NOT NULL COMMENT '开售时间',
    `cn_description`      text COMMENT '中文描述',
    `en_description`      text COMMENT '英文描述',
    `cn_rule_desc`        text COMMENT '中文规则',
    `en_rule_desc`        text COMMENT '英文规则',
    `cn_creator_desc`     text COMMENT '创作者中文描述',
    `en_creator_desc`     text COMMENT '创作者英文描述',
    `enabled`             tinyint(1)   NOT NULL COMMENT '是否激活',
    `offering`            tinyint(1)   NOT NULL COMMENT '是否展示',
    `status`              varchar(32)  NOT NULL COMMENT '状态：APPENDING/INITIALIZED/CREATED',
    `element_id`          bigint(20)   DEFAULT 0 COMMENT '元素id',
    `create_time`         bigint(20)   NOT NULL COMMENT '创建时间',
    `update_time`         bigint(20)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_meta_body` (`nft_meta`, `nft_body`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='NFT分组表';

DROP TABLE IF EXISTS nft_info;
CREATE TABLE `nft_info`
(
    `id`          bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `nft_id`      bigint(20)     NOT NULL COMMENT 'NFT id',
    `group_id`    bigint(20)     NOT NULL COMMENT '所属分组',
    `type`        varchar(32)    NOT NULL COMMENT '类型',
    `name`        varchar(128)   NOT NULL COMMENT '名称',
    `owner`       varchar(128)   NOT NULL COMMENT '所有者',
    `image_link`  varchar(256)   NOT NULL COMMENT '图片链接',
    `image_data`  mediumtext COMMENT '图片数据',
    `score`       decimal(18, 9) NOT NULL COMMENT '分数',
    `rank`        int(11)        NOT NULL COMMENT '排名',
    `created`     tinyint(1)     NOT NULL COMMENT '已创建',
    `state`       varchar(32) DEFAULT NULL COMMENT '状态',
    `create_time` bigint(20)     NOT NULL COMMENT '创建时间',
    `update_time` bigint(20)     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_nft_id` (`nft_id`),
    KEY `idx_group_nft` (`group_id`, `nft_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='NFT信息记录表';

DROP TABLE IF EXISTS nft_market;
CREATE TABLE nft_market
(
    id          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    chain_id    bigint(20) NOT NULL COMMENT '链上 id',
    nft_box_id  bigint(20) NOT NULL COMMENT 'nft_info表的id',
    group_id    bigint(20) NOT NULL COMMENT 'nft_group表的id',
    type        varchar(64)    DEFAULT NULL COMMENT '类型：nft/box',
    name        varchar(128)   DEFAULT NULL COMMENT 'ndf/box 分组全称',
    nft_name    varchar(128)   DEFAULT NULL COMMENT ' nft名称',
    nft_type    varchar(32)    DEFAULT NULL COMMENT 'nft类型：qshow卡牌,qshow元素',
    owner       varchar(128)   DEFAULT NULL COMMENT '当前持有者',
    pay_token   varchar(255)   DEFAULT NULL COMMENT '链上支付币种',
    address     varchar(128)   DEFAULT NULL COMMENT '合约地址',
    sell_price  DECIMAL(38, 0) DEFAULT 0 COMMENT '售价',
    offer_price DECIMAL(38, 0) DEFAULT 0 COMMENT '报价，0暂无报价，大于0为当前最高出价',
    icon        varchar(256)   DEFAULT NULL COMMENT '图片地址',
    create_time bigint(20) NOT NULL COMMENT '创建时间',
    update_time bigint(20) NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_nb_id` (`nft_box_id`),
    KEY `idx_address_type` (`address`, `type`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='NFT/box市场销售列表';

DROP TABLE IF EXISTS nft_event;
CREATE TABLE `nft_event`
(
    `id`            bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `nft_id`        bigint(20)      DEFAULT NULL COMMENT 'NFT id',
    `info_id`       bigint(20)      DEFAULT NULL COMMENT 'info 表 id',
    `group_id`      bigint(20)      DEFAULT NULL COMMENT 'group 表 id',
    `pay_token`     varchar(64)     DEFAULT NULL COMMENT 'pay token',
    `creator`       varchar(128)    DEFAULT NULL COMMENT '创建者',
    `seller`        varchar(128)    DEFAULT NULL COMMENT '出售者',
    `selling_price` decimal(36, 18) DEFAULT '0.000000000000000000' COMMENT '报价',
    `bider`         varchar(128)    DEFAULT NULL COMMENT '出价者',
    `bid_price`     decimal(36, 18) DEFAULT '0.000000000000000000' COMMENT '出价',
    `type`          varchar(20) NOT NULL COMMENT '类型：获得，上架，铸造',
    `create_time`   bigint(20)  NOT NULL COMMENT '创建时间',
    `update_time`   bigint(20)  NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_nft_id` (`nft_id`),
    KEY `idx_nft_type` (`type`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='nft事件表';

DROP TABLE IF EXISTS trading_records;
CREATE TABLE `trading_records`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `address`     varchar(64)  NOT NULL COMMENT '用户地址',
    `type`        varchar(32)  NOT NULL COMMENT '类型nft、box',
    `ref_id`      bigint(20)   NOT NULL COMMENT '关联id nft id 或 box id',
    `nft_box_id`  bigint(20)   NOT NULL COMMENT 'nft_info表的id',
    `group_id`    bigint(20)     DEFAULT NULL COMMENT 'group 表 id',
    `direction`   varchar(32)  NOT NULL COMMENT '方向buy、sell',
    `icon`        varchar(256) NOT NULL COMMENT '图片链接',
    `name`        varchar(32)  NOT NULL COMMENT '组名',
    `box_token`   varchar(128) NOT NULL COMMENT '盲盒币种',
    `nft_meta`    varchar(128) NOT NULL COMMENT 'nft_meta地址',
    `nft_body`    varchar(128) NOT NULL COMMENT 'nft_body地址',
    `pay_token`   varchar(128) NOT NULL COMMENT '支付币种',
    `state`       varchar(32)  NOT NULL COMMENT '状态',
    `nft_type`    varchar(32)    DEFAULT NULL COMMENT 'nft类型：qshow卡牌,qshow元素',
    `price`       decimal(38, 0) DEFAULT '0' COMMENT '成交价、出价',
    `fee`         decimal(38, 0) DEFAULT '0' COMMENT '手续费',
    `finish`      tinyint(1)   NOT NULL COMMENT '是否完结',
    `create_time` bigint(20)   NOT NULL COMMENT '创建时间',
    `update_time` bigint(20)   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='交易记录表';

DROP TABLE IF EXISTS nft_kiko_cat;
CREATE TABLE `nft_kiko_cat`
(
    `id`                      bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `info_id`                 bigint(20)     NOT NULL COMMENT 'nft_Info id',
    `background`              varchar(64)    NOT NULL COMMENT '背景',
    `background_score`        decimal(18, 9) NOT NULL COMMENT '背景分',
    `fur`                     varchar(64)    NOT NULL COMMENT '皮毛',
    `fur_score`               decimal(18, 9) NOT NULL COMMENT '皮毛分',
    `clothes`                 varchar(64)    NOT NULL COMMENT '衣服',
    `clothes_score`           decimal(18, 9) NOT NULL COMMENT '衣服分',
    `facial_expression`       varchar(64)    NOT NULL COMMENT '表情',
    `facial_expression_score` decimal(18, 9) NOT NULL COMMENT '表情分',
    `head`                    varchar(64)    NOT NULL COMMENT '头部',
    `head_score`              decimal(18, 9) NOT NULL COMMENT '头部分',
    `accessories`             varchar(64)    NOT NULL COMMENT '配饰',
    `accessories_score`       decimal(18, 9) NOT NULL COMMENT '配饰分',
    `eyes`                    varchar(64)    NOT NULL COMMENT '眼部',
    `eyes_score`              decimal(18, 9) NOT NULL COMMENT '眼部分',
    `create_time`             bigint(20)     NOT NULL COMMENT '创建时间',
    `update_time`             bigint(20)     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_info_id` (`info_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='NFT Kiko猫信息表';

DROP TABLE IF EXISTS nft_composite_card;
CREATE TABLE `nft_composite_card`
(
    `id`                   bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主  键id',
    `info_id`              bigint(20)  NOT NULL COMMENT 'nft_Info id',
    `occupation`           varchar(64) NOT NULL DEFAULT '' COMMENT '职业',
    `custom_name`          varchar(64) NOT NULL DEFAULT '' COMMENT '自定义组合卡牌名称',
    `state`                tinyint(1)  NOT NULL DEFAULT 0 COMMENT '状态 0有效，1无效',
    `original`             tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否初始卡牌',
    `sex`                  tinyint(8)  NOT NULL DEFAULT 0 COMMENT '性别：0女 1男',
    `background_id`        bigint(20)           DEFAULT 0 COMMENT '背景id',
    `fur_id`               bigint(20)           DEFAULT 0 COMMENT '皮毛id',
    `expression_id`        bigint(20)           DEFAULT 0 COMMENT '表情id',
    `head_id`              bigint(20)           DEFAULT 0 COMMENT '头部id',
    `accessories_id`       bigint(20)           DEFAULT 0 COMMENT '配饰id',
    `eyes_id`              bigint(20)           DEFAULT 0 COMMENT '眼部id',
    `clothes_id`           bigint(20)           DEFAULT 0 COMMENT '衣服id',
    `hat_id`               bigint(20)           DEFAULT 0 COMMENT '帽子id',
    `costume_id`           bigint(20)           DEFAULT 0 COMMENT '服装id',
    `makeup_id`            bigint(20)           DEFAULT 0 COMMENT '妆容id',
    `shoes_id`             bigint(20)           DEFAULT 0 COMMENT '鞋子id',
    `mouth_id`             bigint(20)           DEFAULT 0 COMMENT '嘴id',
    `earring_id`           bigint(20)           DEFAULT 0 COMMENT '耳环id',
    `necklace_id`          bigint(20)           DEFAULT 0 COMMENT '项链id',
    `neck_id`              bigint(20)           DEFAULT 0 COMMENT '颈部id',
    `hair_id`              bigint(20)           DEFAULT 0 COMMENT '头发id',
    `horn_id`              bigint(20)           DEFAULT 0 COMMENT '角id',
    `hands_id`             bigint(20)           DEFAULT 0 COMMENT '手id',
    `body_id`              bigint(20)           DEFAULT 0 COMMENT '身体id',
    `skin_id`              bigint(20)           DEFAULT 0 COMMENT '皮肤id',
    `tattoo_id`            bigint(20)           DEFAULT 0 COMMENT '纹身id',
    `people_id`            bigint(20)           DEFAULT 0 COMMENT '人物id',
    `characteristic_id`    bigint(20)           DEFAULT 0 COMMENT '性格id',
    `hobby_id`             bigint(20)           DEFAULT 0 COMMENT '爱好id',
    `zodiac_id`            bigint(20)           DEFAULT 0 COMMENT '星座id',
    `action_id`            bigint(20)           DEFAULT 0 COMMENT '动作id',
    `toys_id`              bigint(20)           DEFAULT 0 COMMENT '玩具id',
    `fruits_id`            bigint(20)           DEFAULT 0 COMMENT '水果id',
    `vegetables_id`        bigint(20)           DEFAULT 0 COMMENT '蔬菜id',
    `meat_id`              bigint(20)           DEFAULT 0 COMMENT '肉类id',
    `beverages_id`         bigint(20)           DEFAULT 0 COMMENT '饮料id',
    `food_id`              bigint(20)           DEFAULT 0 COMMENT '食物id',
    `vehicle_id`           bigint(20)           DEFAULT 0 COMMENT '交通工具id',
    `weather_id`           bigint(20)           DEFAULT 0 COMMENT '天气id',
    `month_id`             bigint(20)           DEFAULT 0 COMMENT '月份id',
    `sports_id`            bigint(20)           DEFAULT 0 COMMENT '运动id',
    `music_id`             bigint(20)           DEFAULT 0 COMMENT '音乐id',
    `movies_id`            bigint(20)           DEFAULT 0 COMMENT '电影id',
    `season_id`            bigint(20)           DEFAULT 0 COMMENT '季节id',
    `outfit_id`            bigint(20)           DEFAULT 0 COMMENT '搭配id',
    `face_id`              bigint(20)           DEFAULT 0 COMMENT '五官id',
    `arm_id`               bigint(20)           DEFAULT 0 COMMENT '手臂id',
    `leg_id`               bigint(20)           DEFAULT 0 COMMENT '腿部id',
    `foot_id`              bigint(20)           DEFAULT 0 COMMENT '脚id',
    `weapon_id`            bigint(20)           DEFAULT 0 COMMENT '武器id',
    `helmet_id`            bigint(20)           DEFAULT 0 COMMENT '头盔id',
    `armor_id`             bigint(20)           DEFAULT 0 COMMENT '盔甲id',
    `mecha_id`             bigint(20)           DEFAULT 0 COMMENT '机甲id',
    `pants_id`             bigint(20)           DEFAULT 0 COMMENT '裤子id',
    `skirt_id`             bigint(20)           DEFAULT 0 COMMENT '裙子id',
    `left_hand_id`         bigint(20)           DEFAULT 0 COMMENT '左手id',
    `right_hand_id`        bigint(20)           DEFAULT 0 COMMENT '左手id',
    `pets_id`              bigint(20)           DEFAULT 0 COMMENT '宠物id',
    `gifts_id`             bigint(20)           DEFAULT 0 COMMENT '礼物id',
    `tail_id`              bigint(20)           DEFAULT 0 COMMENT '尾巴id',
    `create_time`          bigint(20)           DEFAULT 0 COMMENT '创建时间',
    `update_time`          bigint(20)           DEFAULT 0 COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_info_id` (`info_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='NFT 卡牌';

DROP TABLE IF EXISTS nft_composite_element;
CREATE TABLE `nft_composite_element`
(
    `id`          bigint(20)              NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `info_id`     bigint(20)              NOT NULL COMMENT 'nft_Info id',
    `type`        varchar(64)             NOT NULL DEFAULT '' COMMENT '元素类型',
    `property`    varchar(64)             NOT NULL DEFAULT '' COMMENT '属性值',
    `score`       decimal(18, 9) unsigned NOT NULL DEFAULT '0.000000000' COMMENT '稀有值',
    `create_time` bigint(20)                       DEFAULT 0,
    `update_time` bigint(20)                       DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_info_id` (`info_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  DEFAULT CHARSET = utf8mb4 COMMENT ='NFT 元素';

