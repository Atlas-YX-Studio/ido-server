package com.bixin.nft.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhangcheng
 * create  2022/1/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompositeCardBean {

    private String group_name;
    private long groupId;
    //生成的 图片名称
    private String name;
    private String occupation;
    private String custom_name;
    //0组合卡牌 1原始卡牌
    private int original;
    //0女 1男
    private int sex;

    public static class Layer {
        private String property;
        private String name;
        private BigDecimal score;
        private long nft_id;

    }

}
