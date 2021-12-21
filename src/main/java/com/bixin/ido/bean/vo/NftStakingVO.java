package com.bixin.ido.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NftStakingVO {

    private Long id;

    /**
     * NFT info id
     */
    private Long infoId;

    /**
     * NFT名
     */
    private String name;

    /**
     * 分数
     */
    private BigDecimal score;

    /**
     * 图片链接
     */
    private String imageLink;

    /**
     * 序号
     */
    private Long order;

    /**
     * 用户地址
     */
    private String address;

    /**
     * nft_meta地址
     */
    private String nftMeta;

    /**
     * nft_body地址
     */
    private String nftBody;

    /**
     * 创建时间
     */
    private Long createTime;
}
