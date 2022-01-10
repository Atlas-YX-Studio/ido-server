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

    BACKGROUND("Background", "背景", 1, 10, true),
    FUR("Fur", "皮毛", 2, 0, false),
    CLOTHES("Clothes", "衣服", 3, 80, true),
    EXPRESSION("Facial Expression", "表情", 4, 50, true),
    HEAD("Head", "头部", 5, 90, true),
    ACCESSORIES("Accessories", "配饰", 6, 0, false),
    EYES("Eyes", "眼部", 7, 0, false),
    HAT("HAT", "帽子", 8, 0, false),
    COSTUME("Costume", "服装", 9, 0, false),
    MAKEUP("Makeup", "妆容", 10, 0, false),
    SHOES("Shoes", "鞋子", 11, 70, true),
    MOUTH("Mouth", "嘴", 12, 0, false),
    EARRING("Earring", "耳环", 13, 0, false),
    NECKLACE("Necklace", "项链", 14, 0, false),
    NECK("Neck", "颈部", 15, 0, false),
    HAIR("Hair", "头发", 16, 0, false),
    HORN("Horn", "角", 17, 0, false),
    HANDS("Hands", "手", 18, 0, false),
    BODY("Body", "身体", 19, 40, true),
    SKIN("Skin", "皮肤", 20, 0, false),
    TATTOO("Tattoo", "纹身", 21, 0, false),
    PEOPLE("People", "人物", 22, 0, false),
    CHARACTERISTIC("Characteristic", "性格", 23, 0, false),
    HOBBY("Hobby", "爱好", 24, 0, false),
    ZODIAC("Zodiac", "星座", 25, 0, false),
    ACTION("Action", "动作", 26, 0, false),
    TOYS("Toys", "玩具", 27, 0, false),
    FRUITS("Fruits", "水果", 28, 0, false),
    VEGETABLES("Vegetables", "蔬菜", 29, 0, false),
    MEAT("Meat", "肉类", 30, 0, false),
    BEVERAGES("Beverages", "饮料", 31, 0, false),
    FOOD("Food", "食物", 32, 0, false),
    VEHICLE("Vehicle", "交通工具", 33, 0, false),
    WEATHER("Weather", "天气", 34, 0, false),
    MONTH("Month", "月份", 35, 0, false),
    SPORTS("Sports", "运动", 36, 0, false),
    MUSIC("Music", "音乐", 37, 0, false),
    MOVIES("Movies", "电影", 38, 0, false),
    SEASON("Season", "季节", 39, 0, false),
    OUTFIT("Outfit", "搭配", 40, 0, false),
    FACE("Face", "五官", 41, 0, false),
    ARM("Arm", "手臂", 42, 0, false),
    LEG("Leg", "腿部", 43, 0, false),
    FOOT("Foot", "脚", 44, 0, false),
    WEAPON("Weapon", "武器", 45, 0, false),
    HELMET("Helmet", "头盔", 46, 0, false),
    ARMOR("Armor", "盔甲", 47, 0, false),
    MECHA("Mecha", "机甲", 48, 0, false),
    PANTS("Pants", "裤子", 49, 60, true),
    SKIRT("Skirt", "裙子", 50, 0, false),
    LEFT_HAND("Left Hand", "左手", 51, 30, true),
    RIGHT_HAND("Right Hand", "右手", 52, 100, true),
    PETS("Pets", "宠物", 53, 0, false),
    GIFTS("Gifts", "礼物", 54, 0, false),
    TAIL("Tail", "尾巴", 55, 20, true),
    ;

    private String desc;
    private String cnDesc;
    private long id;
    private long maskOrder;
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
