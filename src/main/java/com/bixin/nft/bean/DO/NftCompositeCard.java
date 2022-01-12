package com.bixin.nft.bean.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bixin.common.utils.BeanReflectUtil;
import com.bixin.nft.bean.bo.CompositeCardBean;
import com.google.common.base.CaseFormat;
import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * NFT 卡牌
 *
 * @since 2022-01-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("nft_composite_card")
public class NftCompositeCard implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主  键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * nft_Info id
     */
    @TableField("info_id")
    private Long infoId;

    /**
     * 职业
     */
    @TableField("occupation")
    private String occupation;

    /**
     * 自定义组合卡牌名称
     */
    @TableField("custom_name")
    private String customName;

    /**
     * 状态 0有效，1无效
     */
    @TableField("state")
    private Integer state;

    /**
     * 是否初始卡牌
     */
    @TableField("original")
    private Boolean original;

    /**
     * 性别 0女 1男
     */
    @TableField("sex")
    private Byte sex;

    /**
     * 背景id
     */
    @TableField("background_id")
    private Long backgroundId;

    /**
     * 皮毛id
     */
    @TableField("fur_id")
    private Long furId;

    /**
     * 表情id
     */
    @TableField("expression_id")
    private Long expressionId;

    /**
     * 头部id
     */
    @TableField("head_id")
    private Long headId;

    /**
     * 配饰id
     */
    @TableField("accessories_id")
    private Long accessoriesId;

    /**
     * 眼部id
     */
    @TableField("eyes_id")
    private Long eyesId;

    /**
     * 衣服id
     */
    @TableField("clothes_id")
    private Long clothesId;

    /**
     * 帽子id
     */
    @TableField("hat_id")
    private Long hatId;

    /**
     * 服装id
     */
    @TableField("costume_id")
    private Long costumeId;

    /**
     * 妆容id
     */
    @TableField("makeup_id")
    private Long makeupId;

    /**
     * 鞋子id
     */
    @TableField("shoes_id")
    private Long shoesId;

    /**
     * 嘴id
     */
    @TableField("mouth_id")
    private Long mouthId;

    /**
     * 耳环id
     */
    @TableField("earring_id")
    private Long earringId;

    /**
     * 项链id
     */
    @TableField("necklace_id")
    private Long necklaceId;

    /**
     * 颈部id
     */
    @TableField("neck_id")
    private Long neckId;

    /**
     * 头发id
     */
    @TableField("hair_id")
    private Long hairId;

    /**
     * 角id
     */
    @TableField("horn_id")
    private Long hornId;

    /**
     * 手id
     */
    @TableField("hands_id")
    private Long handsId;

    /**
     * 身体id
     */
    @TableField("body_id")
    private Long bodyId;

    /**
     * 皮肤id
     */
    @TableField("skin_id")
    private Long skinId;

    /**
     * 纹身id
     */
    @TableField("tattoo_id")
    private Long tattooId;

    /**
     * 人物id
     */
    @TableField("people_id")
    private Long peopleId;

    /**
     * 性格id
     */
    @TableField("characteristic_id")
    private Long characteristicId;

    /**
     * 爱好id
     */
    @TableField("hobby_id")
    private Long hobbyId;

    /**
     * 星座id
     */
    @TableField("zodiac_id")
    private Long zodiacId;

    /**
     * 动作id
     */
    @TableField("action_id")
    private Long actionId;

    /**
     * 玩具id
     */
    @TableField("toys_id")
    private Long toysId;

    /**
     * 水果id
     */
    @TableField("fruits_id")
    private Long fruitsId;

    /**
     * 蔬菜id
     */
    @TableField("vegetables_id")
    private Long vegetablesId;

    /**
     * 肉类id
     */
    @TableField("meat_id")
    private Long meatId;

    /**
     * 饮料id
     */
    @TableField("beverages_id")
    private Long beveragesId;

    /**
     * 食物id
     */
    @TableField("food_id")
    private Long foodId;

    /**
     * 交通工具id
     */
    @TableField("vehicle_id")
    private Long vehicleId;

    /**
     * 天气id
     */
    @TableField("weather_id")
    private Long weatherId;

    /**
     * 月份id
     */
    @TableField("month_id")
    private Long monthId;

    /**
     * 运动id
     */
    @TableField("sports_id")
    private Long sportsId;

    /**
     * 音乐id
     */
    @TableField("music_id")
    private Long musicId;

    /**
     * 电影id
     */
    @TableField("movies_id")
    private Long moviesId;

    /**
     * 季节id
     */
    @TableField("season_id")
    private Long seasonId;

    /**
     * 搭配id
     */
    @TableField("outfit_id")
    private Long outfitId;

    /**
     * 五官id
     */
    @TableField("face_id")
    private Long faceId;

    /**
     * 手臂id
     */
    @TableField("arm_id")
    private Long armId;

    /**
     * 腿部id
     */
    @TableField("leg_id")
    private Long legId;

    /**
     * 脚id
     */
    @TableField("foot_id")
    private Long footId;

    /**
     * 武器id
     */
    @TableField("weapon_id")
    private Long weaponId;

    /**
     * 头盔id
     */
    @TableField("helmet_id")
    private Long helmetId;

    /**
     * 盔甲id
     */
    @TableField("armor_id")
    private Long armorId;

    /**
     * 机甲id
     */
    @TableField("mecha_id")
    private Long mechaId;

    /**
     * 裤子id
     */
    @TableField("pants_id")
    private Long pantsId;

    /**
     * 裙子id
     */
    @TableField("skirt_id")
    private Long skirtId;

    /**
     * 左手id
     */
    @TableField("left_hand_id")
    private Long leftHandId;

    /**
     * 右手id
     */
    @TableField("right_hand_id")
    private Long rightHandId;

    /**
     * 宠物id
     */
    @TableField("pets_id")
    private Long petsId;

    /**
     * 礼物id
     */
    @TableField("gifts_id")
    private Long giftsId;

    /**
     * 尾巴id
     */
    @TableField("tail_id")
    private Long tailId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Long updateTime;


    public static List<Long> getElementIds(NftCompositeCard card) {
        List<Long> list = Arrays.asList(
                card.getBackgroundId(),
                card.getFurId(),
                card.getExpressionId(),
                card.getHeadId(),
                card.getAccessoriesId(),
                card.getEyesId(),
                card.getClothesId(),
                card.getHatId(),
                card.getCostumeId(),
                card.getMakeupId(),
                card.getShoesId(),
                card.getMouthId(),
                card.getEarringId(),
                card.getNecklaceId(),
                card.getNeckId(),
                card.getHairId(),
                card.getHornId(),
                card.getHandsId(),
                card.getBodyId(),
                card.getSkinId(),
                card.getTattooId(),
                card.getPeopleId(),
                card.getCharacteristicId(),
                card.getHobbyId(),
                card.getZodiacId(),
                card.getActionId(),
                card.getToysId(),
                card.getFruitsId(),
                card.getVegetablesId(),
                card.getMeatId(),
                card.getBeveragesId(),
                card.getMeatId(),
                card.getBeveragesId(),
                card.getFoodId(),
                card.getVehicleId(),
                card.getWeatherId(),
                card.getMonthId(),
                card.getSportsId(),
                card.getMusicId(),
                card.getMoviesId(),
                card.getSeasonId(),
                card.getOutfitId(),
                card.getFaceId(),
                card.getArmId(),
                card.getLegId(),
                card.getFootId(),
                card.getWeaponId(),
                card.getHelmetId(),
                card.getArmorId(),
                card.getMechaId(),
                card.getPantsId(),
                card.getSkirtId(),
                card.getLeftHandId(),
                card.getRightHandId(),
                card.getPetsId(),
                card.getGiftsId(),
                card.getTailId()
        );
        return list;
    }

    public static NftCompositeCard of(List<CompositeCardBean.CustomCardElement> elements) {
        NftCompositeCard card = new NftCompositeCard();
        for (CompositeCardBean.CustomCardElement element : elements) {
            String eleName = element.getEleName();
            if (Objects.nonNull(map.get(eleName))) {
                eleName = map.get(eleName);
            }
            String name = eleName.replaceAll("\\s*", "");
            String fieldName = name.substring(0, 1).toLowerCase()
                    + name.substring(1, name.length())
                    + "Id";
            BeanReflectUtil.setFieldValue(card, fieldName, element.getId());
        }
        return card;
    }


    static final Map<String, String> map = new HashMap<>() {{
        put("Facial Expression", "expression");
    }};

}
