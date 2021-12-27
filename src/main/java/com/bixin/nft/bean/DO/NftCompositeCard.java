package com.bixin.nft.bean.DO;

import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * NFT 卡牌
 *
 * @author Xiang Feihan
 * @since 2021-12-20
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
     * 主键id
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
     * 背景id
     */
    @TableField("background_id")
    private Long backgroundId;

    /**
     * 背景
     */
    @TableField("background")
    private String background;

    /**
     * 背景分
     */
    @TableField("background_score")
    private BigDecimal backgroundScore;

    /**
     * 皮毛id
     */
    @TableField("fur_id")
    private Long furId;

    /**
     * 皮毛
     */
    @TableField("fur")
    private String fur;

    /**
     * 皮毛分
     */
    @TableField("fur_score")
    private BigDecimal furScore;

    /**
     * 衣服id
     */
    @TableField("clothes_id")
    private Long clothesId;

    /**
     * 衣服
     */
    @TableField("clothes")
    private String clothes;

    /**
     * 衣服分
     */
    @TableField("clothes_score")
    private BigDecimal clothesScore;

    /**
     * 表情id
     */
    @TableField("facial_expression_id")
    private Long facialExpressionId;

    /**
     * 表情
     */
    @TableField("facial_expression")
    private String facialExpression;

    /**
     * 表情分
     */
    @TableField("facial_expression_score")
    private BigDecimal facialExpressionScore;

    /**
     * 头部id
     */
    @TableField("head_expression_id")
    private Long headExpressionId;

    /**
     * 头部
     */
    @TableField("head")
    private String head;

    /**
     * 头部分
     */
    @TableField("head_score")
    private BigDecimal headScore;

    /**
     * 自定义组合卡牌名称
     */
    @TableField("custom_name")
    private  String customName;

    /**
     * 自定义组合卡牌用户地址
     */
    @TableField("user_address")
    private  String userAddress;

    @TableField("create_time")
    private Long createTime;

    @TableField("update_time")
    private Long updateTime;


}
