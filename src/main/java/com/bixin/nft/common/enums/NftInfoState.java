package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create  2022/1/13
 */
@Getter
@AllArgsConstructor
public enum NftInfoState {

    INIT("init"),
    SUCCESS("success"),
    INVALID("invalid"),

    ;


    private String desc;

}
