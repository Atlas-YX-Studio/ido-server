package com.bixin.nft.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2022/1/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompositeCardBeanV2 {

    private String group_name;
    private long group_id;
    //生成的 图片名称
    private String name;
    private String occupation;
    private String custom_name;
    //0组合卡牌 1原始卡牌
    private int original;
    //0女 1男
    private int sex;

    private Map<Integer, Layer> layers;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Layer {
        private String property;
        private String name;
        private BigDecimal score;
        private long element_group_id;
    }

}