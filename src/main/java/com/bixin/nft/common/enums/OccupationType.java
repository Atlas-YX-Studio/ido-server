package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OccupationType {

    ADVENTURER("Adventurer", "冒险家"),
    EXTREME_PLAYER("Extreme Player", "极限玩家"),
    ATHLETES("Athletes", "运动员"),
//    NONE("None", "无"),

    ;

    private String desc;
    private String cnDesc;

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
