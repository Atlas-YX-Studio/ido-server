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

    BACKGROUND("background", 1),
    FUR("fur", 2),
    CLOTHES("clothes", 3),
    EXPRESSION("expression", 4),
    HEAD("head", 5),
    ACCESSORIES("accessories", 6),
    EYES("eyes", 7),
    HAT("hat", 8),
    COSTUME("costume", 9),
    MAKEUP("makeup", 10),
    SHOES("shoes", 11),
    MOUTH("mouth", 12),
    EARRING("earring", 13),
    NECKLACE("necklace", 14),
    NECK("neck", 15),
    HAIR("hair", 16),
    HORN("horn", 17),
    HANDS("hands", 18),
    BODY("body", 19),
    SKIN("skin", 20),
    TATTOO("tattoo", 21),
    PEOPLE("people", 22),
    CHARACTERISTIC("characteristic", 23),
    HOBBY("hobby", 24),
    ZODIAC("zodiac", 25),
    ACTION("action", 26),
    TOYS("toys", 27),
    FRUITS("fruits", 28),
    VEGETABLES("vegetables", 29),
    MEAT("meat", 30),
    BEVERAGES("beverages", 31),
    FOOD("food", 32),
    VEHICLE("vehicle", 33),
    WEATHER("weather", 34),
    MONTH("month", 35),
    SPORTS("sports", 36),
    MUSIC("music", 37),
    MOVIES("movies", 38),
    SEASON("season", 39),
    OUTFIT("outfit", 40),
    FACE("face", 41),
    ARM("arm", 42),
    LEG("leg", 43),
    FOOT("foot", 44),
    WEAPON("weapon", 45),
    HELMET("helmet", 46),
    ARMOR("ARMOR", 47),
    MECHA("mecha", 48),
    PANTS("pants", 49),
    SKIRT("skirt", 50),
    LEFT_HAND("left_hand", 51),
    RIGHT_HAND("right_hand", 52),
    PETS("pets", 53),
    GIFTS("gifts", 54),
    TAIL("tail", 55),
    ;

    private String desc;
    private long id;

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
