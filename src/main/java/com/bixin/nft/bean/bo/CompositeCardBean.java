package com.bixin.nft.bean.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021/12/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompositeCardBean {

    //用户地址
    private String userAddress;
    private String occupation;
    //自定义卡牌名称
    private String customName;
    //0女 1男
    private int sex;
    private long groupId;

    private List<CustomCardElement> elementList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomCardElement {
        //素材 nftId
        private Long id;
        //素材名称
        private String eleName;
    }

}
