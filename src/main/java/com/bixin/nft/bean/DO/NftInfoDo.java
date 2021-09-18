package com.bixin.nft.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: NftInfoDo
* @Description: NFT信息记录表
* @author: 系统
* @created: 2021-09-17
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftInfoDo implements Serializable {
    /**
     * 主键id
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
     * 名称
     */
    private String name;

    /**
     * 图片链接
     */
    private String imageLink;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 已创建
     */
    private Boolean created;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 图片数据
     */
    private String imageData;

    private static final long serialVersionUID = 1L;
}