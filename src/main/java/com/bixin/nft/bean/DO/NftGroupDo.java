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
    private String seriesName;

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
    private Integer seriesQuantity;

    /**
     * 盲盒币种
     */
    private String boxToken;

    /**
     * 盲盒币种精度
     */
    private Integer boxTokenPrecision;

    /**
     * 盲盒图片
     */
    private String boxTokenLogo;

    /**
     * 支付币种
     */
    private String payToken;

    /**
     * 支付币种精度
     */
    private Integer payTokenPrecision;

    /**
     * nft_meta地址
     */
    private String nftMeta;

    /**
     * nft_body地址
     */
    private String nftBody;

    /**
     * nft_type_info地址
     */
    private String nftTypeInfo;

    /**
     * 发售价格
     */
    private Integer sellingPrice;

    /**
     * 开售时间
     */
    private Long sellingTime;

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
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 中文描述
     */
    private String cnDescription;

    /**
     * 英文描述
     */
    private String enDescription;

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

    private static final long serialVersionUID = 1L;
}