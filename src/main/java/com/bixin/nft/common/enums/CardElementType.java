package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author zhangcheng
 * create  2021/12/27
 */
@Getter
@AllArgsConstructor
public enum CardElementType {

    ELE_BODY("body"),
    ELE_HAIR("hair"),
    ELE_HEAD_WEAR("headWear"),
    ELE_EYE("eye"),
    ELE_MOUTH("mouth"),
    ELE_CLOTHES("clothes"),
    ELE_PROP("prop"),
    ELE_BACKGROUND("background"),

    ;

    private String desc;

    public static CardElementType of(String desc) {
        if (Objects.isNull(desc)) {
            return null;
        }
        for (CardElementType elementType : CardElementType.values()) {
            if (desc.equalsIgnoreCase(elementType.getDesc())) {
                return elementType;
            }
        }
        return null;
    }


}
