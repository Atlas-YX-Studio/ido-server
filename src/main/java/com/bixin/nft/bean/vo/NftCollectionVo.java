package com.bixin.nft.bean.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NftCollectionVo {


    /**
     * 藏品类型
     * @see com.bixin.nft.common.enums.NftBoxType
     */
    private String collectionType;
    /**
     * NFT id (链上)
     */
    private Long nftId;
    /**
     * 名称
     */
    private String name;
    /**
     * nft_meta地址
     */
    private String nftMeta;
    /**
     * nft_body地址
     */
    private String nftBody;
    /**
     * 分数
     */
    private BigDecimal score;
    /**
     * 盲盒代币地址
     */
    private String boxToken;
    /**
     * 图片地址
     */
    private String imageLink;
}
