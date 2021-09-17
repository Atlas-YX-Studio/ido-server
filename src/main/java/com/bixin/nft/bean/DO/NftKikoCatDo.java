package com.bixin.nft.bean.DO;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: NftKikoCatDo
* @Description: NFT Kiko猫信息表
* @author: 系统
* @created: 2021-09-17
*/
@Data
public class NftKikoCatDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * nft_Info id
     */
    private Long infoId;

    /**
     * 背景
     */
    private String background;

    /**
     * 背景分
     */
    private BigDecimal backgroundScore;

    /**
     * 皮毛
     */
    private String fur;

    /**
     * 皮毛分
     */
    private BigDecimal furScore;

    /**
     * 衣服
     */
    private String clothes;

    /**
     * 衣服分
     */
    private BigDecimal clothesScore;

    /**
     * 表情
     */
    private String facialExpression;

    /**
     * 表情分
     */
    private BigDecimal facialExpressionScore;

    /**
     * 头部
     */
    private String head;

    /**
     * 头部分
     */
    private BigDecimal headScore;

    /**
     * 配饰
     */
    private String accessories;

    /**
     * 配饰分
     */
    private BigDecimal accessoriesScore;

    /**
     * 眼部
     */
    private String eyes;

    /**
     * 眼部分
     */
    private BigDecimal eyesScore;

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