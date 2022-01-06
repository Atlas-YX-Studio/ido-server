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

    BACKGROUND("background", 1, true),
    FUR("fur", 2, true),
    CLOTHES("clothes", 3, true),
    EXPRESSION("expression", 4, true),
    HEAD("head", 5, true),
    ACCESSORIES("accessories", 6, false),
    EYES("eyes", 7, false),
    HAT("hat", 8, false),
    COSTUME("costume", 9, false),
    MAKEUP("makeup", 10, false),
    SHOES("shoes", 11, false),
    MOUTH("mouth", 12, false),
    EARRING("earring", 13, false),
    NECKLACE("necklace", 14, false),
    NECK("neck", 15, false),
    HAIR("hair", 16, false),
    HORN("horn", 17, false),
    HANDS("hands", 18, false),
    BODY("body", 19, false),
    SKIN("skin", 20, false),
    TATTOO("tattoo", 21, false),
    PEOPLE("people", 22, false),
    CHARACTERISTIC("characteristic", 23, false),
    HOBBY("hobby", 24, false),
    ZODIAC("zodiac", 25, false),
    ACTION("action", 26, false),
    TOYS("toys", 27, false),
    FRUITS("fruits", 28, false),
    VEGETABLES("vegetables", 29, false),
    MEAT("meat", 30, false),
    BEVERAGES("beverages", 31, false),
    FOOD("food", 32, false),
    VEHICLE("vehicle", 33, false),
    WEATHER("weather", 34, false),
    MONTH("month", 35, false),
    SPORTS("sports", 36, false),
    MUSIC("music", 37, false),
    MOVIES("movies", 38, false),
    SEASON("season", 39, false),
    OUTFIT("outfit", 40, false),
    FACE("face", 41, false),
    ARM("arm", 42, false),
    LEG("leg", 43, false),
    FOOT("foot", 44, false),
    WEAPON("weapon", 45, false),
    HELMET("helmet", 46, false),
    ARMOR("ARMOR", 47, false),
    MECHA("mecha", 48, false),
    PANTS("pants", 49, false),
    SKIRT("skirt", 50, false),
    LEFT_HAND("left hand", 51, false),
    RIGHT_HAND("right hand", 52, false),
    PETS("pets", 53, false),
    GIFTS("gifts", 54, false),
    TAIL("tail", 55, false),
    ;

    private String desc;
    private long id;
    private boolean enable;

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
