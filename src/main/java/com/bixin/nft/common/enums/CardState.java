package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create  2021/12/29
 */
@Getter
@AllArgsConstructor
public enum CardState {

    //初始化
    card_init(0),
    card_combining(1),
    card_split(2),
    card_combining_invalid(3),
    card_split_invalid(4),

    ;

    private int code;

    public static CardState of(int code) {
        if (code < 0) {
            return null;
        }
        for (CardState cardState : CardState.values()) {
            if (code == cardState.getCode()) {
                return cardState;
            }
        }
        return null;
    }


}
