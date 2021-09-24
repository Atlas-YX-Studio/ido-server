package com.bixin.nft.bean.DO;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * @Class: NftEventDo
 * @Description: nft事件表
 * @author: 系统
 * @created: 2021-09-23
 */
@Data
@Builder
public class NftEventDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * NFT id
     */
    private Long nftId;

    /**
     * info 表 id
     */
    private Long infoId;

    /**
     * group 表 id
     */
    private Long groupId;

    /**
     * pay token
     */
    private String payToken;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 出售者
     */
    private String seller;

    /**
     * 报价
     */
    private BigDecimal sellingPrice;

    /**
     * 出价者
     */
    private String bider;

    /**
     * 出价
     */
    private BigDecimal bidPrice;

    /**
     * 类型：获得，上架，铸造
     */
    private String type;

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