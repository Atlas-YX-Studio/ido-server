package com.bixin.nft.bean.DO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: NftMarketDo
* @Description: NFT/box市场销售列表
* @author: 系统
* @created: 2021-09-17
*/
@Data
public class NftMarketDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * NFT/box id
     */
    private Long nftBoxId;

    /**
     * 类型：nft/box
     */
    private String type;

    /**
     * ndf/box 全称
     */
    private String name;

    /**
     * 所属分组 id
     */
    private Long groupId;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 当前持有者
     */
    private String owner;

    /**
     * 合约地址
     */
    private String address;

    /**
     * 售价
     */
    private BigDecimal sellPrice;

    /**
     * 报价，0暂无报价，大于0为当前最高出价
     */
    private BigDecimal offerPrice;

    /**
     * 图片地址
     */
    private String icon;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}