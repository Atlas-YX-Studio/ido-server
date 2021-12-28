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

    BACKGROUND("background"),
    FUR("fur"),
    EXPRESSION("expression"),
    HEAD("head"),
    ACCESSORIES("accessories"),
    EYES("eyes"),
    CLOTHES("clothes"),
    HAT("hat"),
    COSTUME("costume"),
    MAKEUP("makeup"),
    SHOES("shoes"),
    MOUTH("mouth"),
    EARRING("earring"),
    NECKLACE("necklace"),
    NECK("neck"),
    HAIR("hair"),
    HORN("horn"),
    HANDS("hands"),
    BODY("body"),
    SKIN("skin"),
    TATTOO("tattoo"),
    PEOPLE("people"),
    CHARACTERISTIC("characteristic"),
    HOBBY("hobby"),
    ZODIAC("zodiac"),
    ACTION("action"),
    TOYS("TOYS"),
    FRUITS("fruits"),
    VEGETABLES("vegetables"),
    MEAT("meat"),
    BEVERAGES("beverages"),
    FOOD("food"),
    VEHICLE("vehicle"),
    WEATHER("weather"),
    MONTH("month"),
    SPORTS("sports"),
    MUSIC("music"),
    MOVIES("movies"),
    SEASON("season"),
    OUTFIT("outfit"),
    FACE("face"),
    ARM("arm"),
    LEG("leg"),
    FOOT("foot"),
    WEAPON("weapon"),
    HELMET("helmet"),
    ARMOR("ARMOR"),
    MECHA("mecha"),
    PANTS("pants"),
    SKIRT("skirt"),

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
