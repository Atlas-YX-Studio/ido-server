package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OccupationType {

    ADVENTURER("adventurer"),
    EXTREME_PLAYER("extreme player"),
    ATHLETES("athletes"),
    NONE("none"),

    ;

    private String desc;

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
