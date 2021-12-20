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
    BOX("box");

    private String desc;

    public static NftBoxType of(String desc) {
        switch (desc) {
            case "nft":
                return NFT;
            case "box":
                return BOX;
            default:
                return null;
        }
    }

}
