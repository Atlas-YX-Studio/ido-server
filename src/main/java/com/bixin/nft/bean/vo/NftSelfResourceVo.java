package com.bixin.nft.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2022/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftSelfResourceVo {

    //是否是卡牌，如果为 false，则original，customName，sex 可忽略
    private boolean isCard;
    private boolean original;
    private String customName;
    //0女 1男
    private int sex;

    private Map<String, ElementVo> eleMap;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ElementVo {
        private String property;
        private String image;
        private BigDecimal score;
        private long sum;
    }

}
//
//
//              "isCard": true,         //是否是卡牌，如果为 false，则original，customName，sex 可忽略
//               "original": true,       //是否是 原始卡牌
//               "customName":"test1",  //自定义名称
//               "sex"：1,              //0女 1男
//               "clothes":[{
//               "id": 2,
//               "image":"http://link_xxxx",
//               "score":98.8,
//               "property":"blue",
//               "sum":12
//               },
//               .....
//               ],
//               "eyes": [{
//               "id": 22,
//               "image":"http://link_xxxx",
//               "score":98.8,
//               "property":"blue",
//               "sum":12
//               },
//               .....
//               ],
