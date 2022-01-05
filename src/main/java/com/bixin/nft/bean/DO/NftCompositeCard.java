package com.bixin.nft.bean.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

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
    private Boolean state;

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
     * 背景
     */
    @TableField("background")
    private String background;

    /**
     * 皮毛
     */
    @TableField("fur")
    private String fur;

    /**
     * 表情
     */
    @TableField("expression")
    private String expression;

    /**
     * 头部
     */
    @TableField("head")
    private String head;

    /**
     * 配饰
     */
    @TableField("accessories")
    private String accessories;

    /**
     * 眼部
     */
    @TableField("eyes")
    private String eyes;

    /**
     * 衣服
     */
    @TableField("clothes")
    private String clothes;

    /**
     * 帽子
     */
    @TableField("hat")
    private String hat;

    /**
     * 服装
     */
    @TableField("costume")
    private String costume;

    /**
     * 妆容
     */
    @TableField("makeup")
    private String makeup;

    /**
     * 鞋子
     */
    @TableField("shoes")
    private String shoes;

    /**
     * 嘴
     */
    @TableField("mouth")
    private String mouth;

    /**
     * 耳环
     */
    @TableField("earring")
    private String earring;

    /**
     * 项链
     */
    @TableField("necklace")
    private String necklace;

    /**
     * 颈部
     */
    @TableField("neck")
    private String neck;

    /**
     * 头发
     */
    @TableField("hair")
    private String hair;

    /**
     * 角
     */
    @TableField("horn")
    private String horn;

    /**
     * 手
     */
    @TableField("hands")
    private String hands;

    /**
     * 身体
     */
    @TableField("body")
    private String body;

    /**
     * 皮肤
     */
    @TableField("skin")
    private String skin;

    /**
     * 纹身
     */
    @TableField("tattoo")
    private String tattoo;

    /**
     * 人物
     */
    @TableField("people")
    private String people;

    /**
     * 性格
     */
    @TableField("characteristic")
    private String characteristic;

    /**
     * 爱好
     */
    @TableField("hobby")
    private String hobby;

    /**
     * 星座
     */
    @TableField("zodiac")
    private String zodiac;

    /**
     * 动作
     */
    @TableField("action")
    private String action;

    /**
     * 玩具
     */
    @TableField("toys")
    private String toys;

    /**
     * 水果
     */
    @TableField("fruits")
    private String fruits;

    /**
     * 蔬菜
     */
    @TableField("vegetables")
    private String vegetables;

    /**
     * 肉类
     */
    @TableField("meat")
    private String meat;

    /**
     * 饮料
     */
    @TableField("beverages")
    private String beverages;

    /**
     * 食物
     */
    @TableField("food")
    private String food;

    /**
     * 交通工具
     */
    @TableField("vehicle")
    private String vehicle;

    /**
     * 天气
     */
    @TableField("weather")
    private String weather;

    /**
     * 月份
     */
    @TableField("month")
    private String month;

    /**
     * 运动
     */
    @TableField("sports")
    private String sports;

    /**
     * 音乐
     */
    @TableField("music")
    private String music;

    /**
     * 电影
     */
    @TableField("movies")
    private String movies;

    /**
     * 季节
     */
    @TableField("season")
    private String season;

    /**
     * 搭配
     */
    @TableField("outfit")
    private String outfit;

    /**
     * 五官
     */
    @TableField("face")
    private String face;

    /**
     * 手臂
     */
    @TableField("arm")
    private String arm;

    /**
     * 腿部
     */
    @TableField("leg")
    private String leg;

    /**
     * 脚
     */
    @TableField("foot")
    private String foot;

    /**
     * 武器
     */
    @TableField("weapon")
    private String weapon;

    /**
     * 头盔
     */
    @TableField("helmet")
    private String helmet;

    /**
     * 盔甲
     */
    @TableField("armor")
    private String armor;

    /**
     * 机甲
     */
    @TableField("mecha")
    private String mecha;

    /**
     * 裤子
     */
    @TableField("pants")
    private String pants;

    /**
     * 裙子
     */
    @TableField("skirt")
    private String skirt;

    /**
     * 背景score
     */
    @TableField("background_score")
    private BigDecimal backgroundScore;

    /**
     * 皮毛score
     */
    @TableField("fur_score")
    private BigDecimal furScore;

    /**
     * 表情score
     */
    @TableField("expression_score")
    private BigDecimal expressionScore;

    /**
     * 头部score
     */
    @TableField("head_score")
    private BigDecimal headScore;

    /**
     * 配饰score
     */
    @TableField("accessories_score")
    private BigDecimal accessoriesScore;

    /**
     * 眼部score
     */
    @TableField("eyes_score")
    private BigDecimal eyesScore;

    /**
     * 衣服score
     */
    @TableField("clothes_score")
    private BigDecimal clothesScore;

    /**
     * 帽子score
     */
    @TableField("hat_score")
    private BigDecimal hatScore;

    /**
     * 服装score
     */
    @TableField("costume_score")
    private BigDecimal costumeScore;

    /**
     * 妆容score
     */
    @TableField("makeup_score")
    private BigDecimal makeupScore;

    /**
     * 鞋子score
     */
    @TableField("shoes_score")
    private BigDecimal shoesScore;

    /**
     * 嘴score
     */
    @TableField("mouth_score")
    private BigDecimal mouthScore;

    /**
     * 耳环score
     */
    @TableField("earring_score")
    private BigDecimal earringScore;

    /**
     * 项链score
     */
    @TableField("necklace_score")
    private BigDecimal necklaceScore;

    /**
     * 颈部score
     */
    @TableField("neck_score")
    private BigDecimal neckScore;

    /**
     * 头发score
     */
    @TableField("hair_score")
    private BigDecimal hairScore;

    /**
     * 角score
     */
    @TableField("horn_score")
    private BigDecimal hornScore;

    /**
     * 手score
     */
    @TableField("hands_score")
    private BigDecimal handsScore;

    /**
     * 身体score
     */
    @TableField("body_score")
    private BigDecimal bodyScore;

    /**
     * 皮肤score
     */
    @TableField("skin_score")
    private BigDecimal skinScore;

    /**
     * 纹身score
     */
    @TableField("tattoo_score")
    private BigDecimal tattooScore;

    /**
     * 人物score
     */
    @TableField("people_score")
    private BigDecimal peopleScore;

    /**
     * 性格score
     */
    @TableField("characteristic_score")
    private BigDecimal characteristicScore;

    /**
     * 爱好score
     */
    @TableField("hobby_score")
    private BigDecimal hobbyScore;

    /**
     * 星座score
     */
    @TableField("zodiac_score")
    private BigDecimal zodiacScore;

    /**
     * 动作score
     */
    @TableField("action_score")
    private BigDecimal actionScore;

    /**
     * 玩具score
     */
    @TableField("toys_score")
    private BigDecimal toysScore;

    /**
     * 水果score
     */
    @TableField("fruits_score")
    private BigDecimal fruitsScore;

    /**
     * 蔬菜score
     */
    @TableField("vegetables_score")
    private BigDecimal vegetablesScore;

    /**
     * 肉类score
     */
    @TableField("meat_score")
    private BigDecimal meatScore;

    /**
     * 饮料score
     */
    @TableField("beverages_score")
    private BigDecimal beveragesScore;

    /**
     * 食物score
     */
    @TableField("food_score")
    private BigDecimal foodScore;

    /**
     * 交通工具score
     */
    @TableField("vehicle_score")
    private BigDecimal vehicleScore;

    /**
     * 天气score
     */
    @TableField("weather_score")
    private BigDecimal weatherScore;

    /**
     * 月份score
     */
    @TableField("month_score")
    private BigDecimal monthScore;

    /**
     * 运动score
     */
    @TableField("sports_score")
    private BigDecimal sportsScore;

    /**
     * 音乐score
     */
    @TableField("music_score")
    private BigDecimal musicScore;

    /**
     * 电影score
     */
    @TableField("movies_score")
    private BigDecimal moviesScore;

    /**
     * 季节score
     */
    @TableField("season_score")
    private BigDecimal seasonScore;

    /**
     * 搭配score
     */
    @TableField("outfit_score")
    private BigDecimal outfitScore;

    /**
     * 五官score
     */
    @TableField("face_score")
    private BigDecimal faceScore;

    /**
     * 手臂score
     */
    @TableField("arm_score")
    private BigDecimal armScore;

    /**
     * 腿部score
     */
    @TableField("leg_score")
    private BigDecimal legScore;

    /**
     * 脚score
     */
    @TableField("foot_score")
    private BigDecimal footScore;

    /**
     * 武器score
     */
    @TableField("weapon_score")
    private BigDecimal weaponScore;

    /**
     * 头盔score
     */
    @TableField("helmet_score")
    private BigDecimal helmetScore;

    /**
     * 盔甲score
     */
    @TableField("armor_score")
    private BigDecimal armorScore;

    /**
     * 机甲score
     */
    @TableField("mecha_score")
    private BigDecimal mechaScore;

    /**
     * 裤子score
     */
    @TableField("pants_score")
    private BigDecimal pantsScore;

    /**
     * 裙子score
     */
    @TableField("skirt_score")
    private BigDecimal skirtScore;

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


}
