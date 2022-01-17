package com.bixin.nft.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangcheng
 * create  2022/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftSelfResourceVo {

    private Map<String, List<ElementVo>> elementMap;
    private List<CardVo> cardList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ElementVo {
        private String type;
        private String property;
        private String image;
        private BigDecimal score;
        private long sum;
        private Map<Long, Long> chainNftIds;
        private String name;
        private String description;
        private String eleName;
        private long groupId;
        private String nftMeta;
        private String nftBody;
        private String payToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardVo {
        private boolean original;
        private String customName;
        //0女 1男
        private int sex;
        private String image;
        private String name;
        private String description;
        private long groupId;
        private long chainId;
        private long nftId;
        private String nftMeta;
        private String nftBody;
        private String payToken;
    }

}

