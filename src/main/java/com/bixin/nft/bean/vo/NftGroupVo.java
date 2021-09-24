package com.bixin.nft.bean.vo;

import com.bixin.nft.bean.dto.TokenDto;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @class: NftGroupVO
 * @Description: NFT分组表 实体类
 * @author: 系统
 * @created: 2021-09-17
 */
@Data
public class NftGroupVo implements Serializable {
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
     * 系列总发售数量
     */
    private Integer seriesQuantity;

    /**
     * 发售数量
     */
    private Integer quantity;

    /**
     * 发售数量
     */
    private Integer offeringQuantity;

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
     * 创作者地址
     */
    private String creator;

    /**
     * 所有者
     */
    private String owner;

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

    /**
     * 是否 售卖
     */
    private Boolean onSell;

    /**
     * 出售价格
     */
    private BigDecimal sellingPrice;

    /**
     * 最高出价
     */
    private BigDecimal topBidPrice;

    /**
     * 支持币种
     */
    private List<TokenDto> supportToken;
}