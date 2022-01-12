package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create   2021/9/18
 */
@Getter
@AllArgsConstructor
public enum NftBoxType {


    NFT("nft"),
    BOX("box"),
    COMPOSITE_CARD("composite_card"),
    COMPOSITE_ELEMENT("composite_element");

    private String desc;

    public static NftBoxType of(String desc) {
        switch (desc) {
            case "nft":
                return NFT;
            case "box":
                return BOX;
            case "composite_card":
                return COMPOSITE_CARD;
            case "composite_element":
                return COMPOSITE_ELEMENT;
            default:
                return null;
        }
    }

}
