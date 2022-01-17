package com.bixin.nft.bean.vo;

import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftKikoCatDo;
import com.bixin.nft.bean.dto.TokenDto;
import com.bixin.nft.common.enums.NftType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @class: NftInfoVO
 * @Description: NFT信息记录表 实体类
 * @author: 系统
 * @created: 2021-09-17
 */
@Data
public class NftInfoVo {

    /**
     * NFT info id (中心化)
     */
    private Long id;
    /**
     * NFT id (链上)
     */
    private Long nftId;
    /**
     * 所属分组
     */
    private Long groupId;
    /**
     * NFT名
     */
    private String name;
    /**
     * 图片链接
     */
    private String imageLink;
    /**
     * 所属者
     */
    private String owner;
    /**
     * 分数
     */
    private BigDecimal score;
    /**
     * 排名
     */
    private Integer rank;

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
     * 配置属性
     */
    private NftKikoCatDo properties;

    private NftCompositeCard compositeCard;

    private NftCompositeElement compositeElement;

    private NftType nftType;

    /**
     * 支持币种
     */
    private List<TokenDto> supportToken;

}