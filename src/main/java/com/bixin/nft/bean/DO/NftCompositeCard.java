package com.bixin.nft.bean.DO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;

/**
 * NFT 卡牌
 *
 * @author cheng.zhang
 * @since 2021-12-28
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
    private Long infoId;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 自定义组合卡牌名称
     */
    private String customName;

    /**
     * 背景id
     */
    private Long backgroundId;

    /**
     * 皮毛id
     */
    private Long furId;

    /**
     * 表情id
     */
    private Long facialId;

    /**
     * 头部id
     */
    private Long headId;

    /**
     * 配饰id
     */
    private Long accessoriesId;

    /**
     * 眼部id
     */
    private Long eyesId;

    /**
     * 衣服id
     */
    private Long clothesId;

    /**
     * 帽子id
     */
    private Long hatId;

    /**
     * 服装id
     */
    private Long costumeId;

    /**
     * 妆容id
     */
    private Long makeupId;

    /**
     * 鞋子id
     */
    private Long shoesId;

    /**
     * 嘴id
     */
    private Long mouthId;

    /**
     * 耳环id
     */
    private Long earringId;

    /**
     * 项链id
     */
    private Long necklaceId;

    /**
     * 颈部id
     */
    private Long neckId;

    /**
     * 头发id
     */
    private Long hairId;

    /**
     * 角id
     */
    private Long hornId;

    /**
     * 手id
     */
    private Long handsId;

    /**
     * 身体id
     */
    private Long bodyId;

    /**
     * 皮肤id
     */
    private Long skinId;

    /**
     * 纹身id
     */
    private Long tattooId;

    /**
     * 人物id
     */
    private Long peopleId;

    /**
     * 性格id
     */
    private Long characteristicId;

    /**
     * 爱好id
     */
    private Long hobbyId;

    /**
     * 星座id
     */
    private Long zodiacId;

    /**
     * 动作id
     */
    private Long actionId;

    /**
     * 玩具id
     */
    private Long toysId;

    /**
     * 水果id
     */
    private Long fruitsId;

    /**
     * 蔬菜id
     */
    private Long vegetablesId;

    /**
     * 肉类id
     */
    private Long meatId;

    /**
     * 饮料id
     */
    private Long beveragesId;

    /**
     * 食物id
     */
    private Long foodId;

    /**
     * 交通工具id
     */
    private Long vehicleId;

    /**
     * 天气id
     */
    private Long weatherId;

    /**
     * 月份id
     */
    private Long monthId;

    /**
     * 运动id
     */
    private Long sportsId;

    /**
     * 音乐id
     */
    private Long musicId;

    /**
     * 电影id
     */
    private Long moviesId;

    /**
     * 季节id
     */
    private Long seasonId;

    /**
     * 搭配id
     */
    private Long outfitId;

    /**
     * 五官id
     */
    private Long faceId;

    /**
     * 手臂id
     */
    private Long armId;

    /**
     * 腿部id
     */
    private Long legId;

    /**
     * 脚id
     */
    private Long footId;

    /**
     * 武器id
     */
    private Long weaponId;

    /**
     * 头盔id
     */
    private Long helmetId;

    /**
     * 盔甲id
     */
    private Long armorId;

    /**
     * 机甲id
     */
    private Long mechaId;

    /**
     * 裤子id
     */
    private Long pantsId;

    /**
     * 裙子id
     */
    private Long skirtId;

    /**
     * 背景
     */
    private Long background;

    /**
     * 皮毛
     */
    private Long fur;

    /**
     * 表情
     */
    private Long facial;

    /**
     * 头部
     */
    private Long head;

    /**
     * 配饰
     */
    private Long accessories;

    /**
     * 眼部
     */
    private Long eyes;

    /**
     * 衣服
     */
    private Long clothes;

    /**
     * 帽子
     */
    private Long hat;

    /**
     * 服装
     */
    private Long costume;

    /**
     * 妆容
     */
    private Long makeup;

    /**
     * 鞋子
     */
    private Long shoes;

    /**
     * 嘴
     */
    private Long mouth;

    /**
     * 耳环
     */
    private Long earring;

    /**
     * 项链
     */
    private Long necklace;

    /**
     * 颈部
     */
    private Long neck;

    /**
     * 头发
     */
    private Long hair;

    /**
     * 角
     */
    private Long horn;

    /**
     * 手
     */
    private Long hands;

    /**
     * 身体
     */
    private Long body;

    /**
     * 皮肤
     */
    private Long skin;

    /**
     * 纹身
     */
    private Long tattoo;

    /**
     * 人物
     */
    private Long people;

    /**
     * 性格
     */
    private Long characteristic;

    /**
     * 爱好
     */
    private Long hobby;

    /**
     * 星座
     */
    private Long zodiac;

    /**
     * 动作
     */
    private Long action;

    /**
     * 玩具
     */
    private Long toys;

    /**
     * 水果
     */
    private Long fruits;

    /**
     * 蔬菜
     */
    private Long vegetables;

    /**
     * 肉类
     */
    private Long meat;

    /**
     * 饮料
     */
    private Long beverages;

    /**
     * 食物
     */
    private Long food;

    /**
     * 交通工具
     */
    private Long vehicle;

    /**
     * 天气
     */
    private Long weather;

    /**
     * 月份
     */
    private Long month;

    /**
     * 运动
     */
    private Long sports;

    /**
     * 音乐
     */
    private Long music;

    /**
     * 电影
     */
    private Long movies;

    /**
     * 季节
     */
    private Long season;

    /**
     * 搭配
     */
    private Long outfit;

    /**
     * 五官
     */
    private Long face;

    /**
     * 手臂
     */
    private Long arm;

    /**
     * 腿部
     */
    private Long leg;

    /**
     * 脚
     */
    private Long foot;

    /**
     * 武器
     */
    private Long weapon;

    /**
     * 头盔
     */
    private Long helmet;

    /**
     * 盔甲
     */
    private Long armor;

    /**
     * 机甲
     */
    private Long mecha;

    /**
     * 裤子
     */
    private Long pants;

    /**
     * 裙子
     */
    private Long skirt;

    /**
     * 背景score
     */
    private Long backgroundScore;

    /**
     * 皮毛score
     */
    private Long furScore;

    /**
     * 表情score
     */
    private Long facialScore;

    /**
     * 头部score
     */
    private Long headScore;

    /**
     * 配饰score
     */
    private Long accessoriesScore;

    /**
     * 眼部score
     */
    private Long eyesScore;

    /**
     * 衣服score
     */
    private Long clothesScore;

    /**
     * 帽子score
     */
    private Long hatScore;

    /**
     * 服装score
     */
    private Long costumeScore;

    /**
     * 妆容score
     */
    private Long makeupScore;

    /**
     * 鞋子score
     */
    private Long shoesScore;

    /**
     * 嘴score
     */
    private Long mouthScore;

    /**
     * 耳环score
     */
    private Long earringScore;

    /**
     * 项链score
     */
    private Long necklaceScore;

    /**
     * 颈部score
     */
    private Long neckScore;

    /**
     * 头发score
     */
    private Long hairScore;

    /**
     * 角score
     */
    private Long hornScore;

    /**
     * 手score
     */
    private Long handsScore;

    /**
     * 身体score
     */
    private Long bodyScore;

    /**
     * 皮肤score
     */
    private Long skinScore;

    /**
     * 纹身score
     */
    private Long tattooScore;

    /**
     * 人物score
     */
    private Long peopleScore;

    /**
     * 性格score
     */
    private Long characteristicScore;

    /**
     * 爱好score
     */
    private Long hobbyScore;

    /**
     * 星座score
     */
    private Long zodiacScore;

    /**
     * 动作score
     */
    private Long actionScore;

    /**
     * 玩具score
     */
    private Long toysScore;

    /**
     * 水果score
     */
    private Long fruitsScore;

    /**
     * 蔬菜score
     */
    private Long vegetablesScore;

    /**
     * 肉类score
     */
    private Long meatScore;

    /**
     * 饮料score
     */
    private Long beveragesScore;

    /**
     * 食物score
     */
    private Long foodScore;

    /**
     * 交通工具score
     */
    private Long vehicleScore;

    /**
     * 天气score
     */
    private Long weatherScore;

    /**
     * 月份score
     */
    private Long monthScore;

    /**
     * 运动score
     */
    private Long sportsScore;

    /**
     * 音乐score
     */
    private Long musicScore;

    /**
     * 电影score
     */
    private Long moviesScore;

    /**
     * 季节score
     */
    private Long seasonScore;

    /**
     * 搭配score
     */
    private Long outfitScore;

    /**
     * 五官score
     */
    private Long faceScore;

    /**
     * 手臂score
     */
    private Long armScore;

    /**
     * 腿部score
     */
    private Long legScore;

    /**
     * 脚score
     */
    private Long footScore;

    /**
     * 武器score
     */
    private Long weaponScore;

    /**
     * 头盔score
     */
    private Long helmetScore;

    /**
     * 盔甲score
     */
    private Long armorScore;

    /**
     * 机甲score
     */
    private Long mechaScore;

    /**
     * 裤子score
     */
    private Long pantsScore;

    /**
     * 裙子score
     */
    private Long skirtScore;

    /**
     * 状态 0有效，1无效
     */
    private Boolean state;

    /**
     * 是否初始卡牌
     */
    private Boolean original;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;


}
