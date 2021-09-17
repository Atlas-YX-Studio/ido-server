package com.bixin.nft.bean.vo;

import lombok.Data;

/**
 * @class: NftInfoVO
 * @Description: NFT信息记录表 实体类
 * @author: 系统
 * @created: 2021-09-17
 */
@Data
public class NftInfoVo {

    /**
     * NFT id
     */
    private Long nftId;

    /**
     * 系列名
     */
    private String seriesName;

    /**
     * 组名
     */
    private String groupName;

    /**
     * 发售数量
     */
    private Integer quantity;

    /**
     * 系列总发售数量
     */
    private Integer seriesQuantity;

    /**
     * nft_meta地址
     */
    private String nftMeta;

    /**
     * nft_body地址
     */
    private String nftBody;

    /**
     * 中文描述
     */
    private String cnDescription;

    /**
     * 英文描述
     */
    private String enDescription;

    /**
     * 创作者
     */
    private String creator;

    /**
     * 所属者
     */
    private String owner;

    /**
     * 中文规则
     */
    private String cnRuleDesc;

    /**
     * 英文规则
     */
    private String enRuleDesc;

    /**
     * 创作者中文描述
     */
    private String cnCreatorDesc;

    /**
     * 创作者英文描述
     */
    private String enCreatorDesc;

    /**
     * 支付币种
     */
    private String payToken;

    /**
     * 支付币种精度
     */
    private Integer payTokenPrecision;

    /**
     * 发售价格
     */
    private Integer sellingPrice;

//    topBidPrice	string	最高出价
//    onSell	boolean	是否出售中

     // todo info 表
//    score	number	总分
//    rank	number	排名

    // // TODO:  cat 表中
//    properties	list	nft属性
//    properties.background	string	背景
//    properties.backgroundScore	string	背景分
//    properties.fur	string	皮肤
//    properties.furScore	string	皮肤分
//    properties.clothes	string	衣服
//    properties.clothesScore	string	衣服分
//    properties.facialExpression	string	表情
//    properties.facialExpressionScore	string	表情分
//    properties.head	string	头部
//    properties.headScore	string	头部分
//    properties.accessories	string	配饰
//    properties.accessoriesScore	string	配饰分
//    properties.eyes	string	眼部
//    properties.eyesScore	string	眼部分

}