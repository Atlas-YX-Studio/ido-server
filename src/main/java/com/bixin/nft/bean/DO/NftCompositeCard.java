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
    @TableField("info_id")
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
    private String background;

    /**
     * 皮毛
     */
    private String fur;

    /**
     * 表情
     */
    private String facial;

    /**
     * 头部
     */
    private String head;

    /**
     * 配饰
     */
    private String accessories;

    /**
     * 眼部
     */
    private String eyes;

    /**
     * 衣服
     */
    private String clothes;

    /**
     * 帽子
     */
    private String hat;

    /**
     * 服装
     */
    private String costume;

    /**
     * 妆容
     */
    private String makeup;

    /**
     * 鞋子
     */
    private String shoes;

    /**
     * 嘴
     */
    private String mouth;

    /**
     * 耳环
     */
    private String earring;

    /**
     * 项链
     */
    private String necklace;

    /**
     * 颈部
     */
    private String neck;

    /**
     * 头发
     */
    private String hair;

    /**
     * 角
     */
    private String horn;

    /**
     * 手
     */
    private String hands;

    /**
     * 身体
     */
    private String body;

    /**
     * 皮肤
     */
    private String skin;

    /**
     * 纹身
     */
    private String tattoo;

    /**
     * 人物
     */
    private String people;

    /**
     * 性格
     */
    private String characteristic;

    /**
     * 爱好
     */
    private String hobby;

    /**
     * 星座
     */
    private String zodiac;

    /**
     * 动作
     */
    private String action;

    /**
     * 玩具
     */
    private String toys;

    /**
     * 水果
     */
    private String fruits;

    /**
     * 蔬菜
     */
    private String vegetables;

    /**
     * 肉类
     */
    private String meat;

    /**
     * 饮料
     */
    private String beverages;

    /**
     * 食物
     */
    private String food;

    /**
     * 交通工具
     */
    private String vehicle;

    /**
     * 天气
     */
    private String weather;

    /**
     * 月份
     */
    private String month;

    /**
     * 运动
     */
    private String sports;

    /**
     * 音乐
     */
    private String music;

    /**
     * 电影
     */
    private String movies;

    /**
     * 季节
     */
    private String season;

    /**
     * 搭配
     */
    private String outfit;

    /**
     * 五官
     */
    private String face;

    /**
     * 手臂
     */
    private String arm;

    /**
     * 腿部
     */
    private String leg;

    /**
     * 脚
     */
    private String foot;

    /**
     * 武器
     */
    private String weapon;

    /**
     * 头盔
     */
    private String helmet;

    /**
     * 盔甲
     */
    private String armor;

    /**
     * 机甲
     */
    private String mecha;

    /**
     * 裤子
     */
    private String pants;

    /**
     * 裙子
     */
    private String skirt;

    /**
     * 背景score
     */
    private BigDecimal backgroundScore;

    /**
     * 皮毛score
     */
    private BigDecimal furScore;

    /**
     * 表情score
     */
    private BigDecimal facialScore;

    /**
     * 头部score
     */
    private BigDecimal headScore;

    /**
     * 配饰score
     */
    private BigDecimal accessoriesScore;

    /**
     * 眼部score
     */
    private BigDecimal eyesScore;

    /**
     * 衣服score
     */
    private BigDecimal clothesScore;

    /**
     * 帽子score
     */
    private BigDecimal hatScore;

    /**
     * 服装score
     */
    private BigDecimal costumeScore;

    /**
     * 妆容score
     */
    private BigDecimal makeupScore;

    /**
     * 鞋子score
     */
    private BigDecimal shoesScore;

    /**
     * 嘴score
     */
    private BigDecimal mouthScore;

    /**
     * 耳环score
     */
    private BigDecimal earringScore;

    /**
     * 项链score
     */
    private BigDecimal necklaceScore;

    /**
     * 颈部score
     */
    private BigDecimal neckScore;

    /**
     * 头发score
     */
    private BigDecimal hairScore;

    /**
     * 角score
     */
    private BigDecimal hornScore;

    /**
     * 手score
     */
    private BigDecimal handsScore;

    /**
     * 身体score
     */
    private BigDecimal bodyScore;

    /**
     * 皮肤score
     */
    private BigDecimal skinScore;

    /**
     * 纹身score
     */
    private BigDecimal tattooScore;

    /**
     * 人物score
     */
    private BigDecimal peopleScore;

    /**
     * 性格score
     */
    private BigDecimal characteristicScore;

    /**
     * 爱好score
     */
    private BigDecimal hobbyScore;

    /**
     * 星座score
     */
    private BigDecimal zodiacScore;

    /**
     * 动作score
     */
    private BigDecimal actionScore;

    /**
     * 玩具score
     */
    private BigDecimal toysScore;

    /**
     * 水果score
     */
    private BigDecimal fruitsScore;

    /**
     * 蔬菜score
     */
    private BigDecimal vegetablesScore;

    /**
     * 肉类score
     */
    private BigDecimal meatScore;

    /**
     * 饮料score
     */
    private BigDecimal beveragesScore;

    /**
     * 食物score
     */
    private BigDecimal foodScore;

    /**
     * 交通工具score
     */
    private BigDecimal vehicleScore;

    /**
     * 天气score
     */
    private BigDecimal weatherScore;

    /**
     * 月份score
     */
    private BigDecimal monthScore;

    /**
     * 运动score
     */
    private BigDecimal sportsScore;

    /**
     * 音乐score
     */
    private BigDecimal musicScore;

    /**
     * 电影score
     */
    private BigDecimal moviesScore;

    /**
     * 季节score
     */
    private BigDecimal seasonScore;

    /**
     * 搭配score
     */
    private BigDecimal outfitScore;

    /**
     * 五官score
     */
    private BigDecimal faceScore;

    /**
     * 手臂score
     */
    private BigDecimal armScore;

    /**
     * 腿部score
     */
    private BigDecimal legScore;

    /**
     * 脚score
     */
    private BigDecimal footScore;

    /**
     * 武器score
     */
    private BigDecimal weaponScore;

    /**
     * 头盔score
     */
    private BigDecimal helmetScore;

    /**
     * 盔甲score
     */
    private BigDecimal armorScore;

    /**
     * 机甲score
     */
    private BigDecimal mechaScore;

    /**
     * 裤子score
     */
    private BigDecimal pantsScore;

    /**
     * 裙子score
     */
    private BigDecimal skirtScore;

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
