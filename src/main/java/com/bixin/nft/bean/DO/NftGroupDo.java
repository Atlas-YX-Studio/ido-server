package com.bixin.nft.bean.DO;

import lombok.Data;

import java.io.Serializable;

/**
* @Class: NftGroupDo
* @Description: NFT分组表
* @author: 系统
* @created: 2021-09-15
*/
@Data
public class NftGroupDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 系列
     */
    private String series;

    /**
     * 系列名
     */
    private String series_name;

    /**
     * 组名
     */
    private String name;

    /**
     * 发售数量
     */
    private Integer quantity;

    /**
     * 系列总发售数量
     */
    private Integer series_quantity;

    /**
     * 盲盒币种
     */
    private String box_token;

    /**
     * 盲盒币种精度
     */
    private Integer box_token_precision;

    /**
     * 盲盒图片
     */
    private String box_token_logo;

    /**
     * 支付币种
     */
    private String pay_token;

    /**
     * 支付币种精度
     */
    private Integer pay_token_precision;

    /**
     * nft_meta地址
     */
    private String nft_meta;

    /**
     * nft_body地址
     */
    private String nft_body;

    /**
     * nft_type_info地址
     */
    private String nft_type_info;

    /**
     * 发售价格
     */
    private Integer selling_price;

    /**
     * 开售时间
     */
    private Long selling_time;

    /**
     * 是否激活
     */
    private Boolean enabled;

    /**
     * 状态：APPENDING/INITIALIZED/CREATED
     */
    private String status;

    /**
     * 创建时间
     */
    private Long create_time;

    /**
     * 更新时间
     */
    private Long update_time;

    /**
     * 中文描述
     */
    private String cn_description;

    /**
     * 英文描述
     */
    private String en_description;

    /**
     * 中文规则
     */
    private String cn_rule_desc;

    /**
     * 英文规则
     */
    private String en_rule_desc;

    /**
     * 创作者中文描述
     */
    private String cn_creator_desc;

    /**
     * 创作者英文描述
     */
    private String en_creator_desc;

    private static final long serialVersionUID = 1L;
}