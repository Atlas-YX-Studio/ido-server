package com.bixin.nft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * NFT 元素
 *
 * @author Xiang Feihan
 * @since 2021-12-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("nft_composite_element")
public class NftCompositeElement implements Serializable {

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
     * 元素类型
     */
    @TableField("type")
    private String type;

    /**
     * 属性值
     */
    @TableField("property")
    private String property;

    /**
     * 稀有值
     */
    @TableField("score")
    private BigDecimal score;

    @TableField("create_time")
    private Long createTime;

    @TableField("update_time")
    private Long updateTime;


}
