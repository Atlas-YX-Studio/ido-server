package com.bixin.nft.bean.DO;

import lombok.Data;

import java.io.Serializable;

/**
* @Class: NftKikoCatDo
* @Description: NFT Kiko猫信息表
* @author: 系统
* @created: 2021-09-15
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
    private Integer backgroundScore;

    /**
     * 品种
     */
    private String breed;

    /**
     * 品种分
     */
    private Integer breedScore;

    /**
     * 装饰
     */
    private String decorate;

    /**
     * 装饰分
     */
    private Integer decorateScore;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 排名
     */
    private Integer order;

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