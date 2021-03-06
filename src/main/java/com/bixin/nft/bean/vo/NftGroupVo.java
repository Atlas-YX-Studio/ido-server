package com.bixin.nft.bean.vo;

import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.dto.TokenDto;
import com.bixin.nft.common.enums.NftType;
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
     * 类型
     */
    private String type;

    /**
     * 系列发售图片
     */
    private String seriesLogo;

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
     * 售卖方式 1: fixed price, 2: auction
     */
    private Integer sellType;

    /**
     * 拍卖结束时间
     */
    private Long endTime;

    /**
     * 出售价格
     */
    private BigDecimal sellingPrice;

    /**
     * 出售时间
     */
    private Long sellingTime;

    /**
     * 最高出价
     */
    private BigDecimal topBidPrice;

    /**
     * 支持币种
     */
    private List<TokenDto> supportToken;

    /**
     * 元素id
     */
    private Long elementId;

    /**
     * 元素
     */
    private NftGroupVo element;

    private NftType nftType;

    private String imageLink;

}