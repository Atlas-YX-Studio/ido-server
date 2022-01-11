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

    private Map<String, Set<ElementVo>> elementMap;
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
        private long groupId;
        private List<Long> nftIds;
        private String name;
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
        private long groupId;
        private long nftId;
        private String name;
    }

}

