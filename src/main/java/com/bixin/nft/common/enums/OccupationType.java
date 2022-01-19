package com.bixin.nft.common.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OccupationType {

    ADVENTURER("Adventurer", "冒险家",
            "https://imagedelivery.net/3mRLd_IbBrrQFSP57PNsVw/05862ea5-e6ee-4cca-6e6d-5b3af0914e00/public"),
    EXTREME_PLAYER("Extreme Player", "极限玩家",
            "https://imagedelivery.net/3mRLd_IbBrrQFSP57PNsVw/d0f29214-d56b-42d3-868d-c17b13082900/public"),
    ATHLETES("Athletes", "运动员",
            "https://imagedelivery.net/3mRLd_IbBrrQFSP57PNsVw/a581477d-318a-4300-8989-f23587d63600/public"),
//    NONE("None", "无"),

    ;

    private String desc;
    private String cnDesc;
    private String image;

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
