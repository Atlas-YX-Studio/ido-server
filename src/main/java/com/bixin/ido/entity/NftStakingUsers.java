package com.bixin.ido.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * NFT质押表
 *
 * @author Xiang Feihan
 * @since 2021-11-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("nft_staking_users")
public class NftStakingUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户地址
     */
    @TableField("address")
    private String address;

    /**
     * NFT info id
     */
    @TableField("info_id")
    private Long infoId;

    /**
     * 序号
     */
    @TableField("`order`")
    private Long order;

    /**
     * 分数
     */
    @TableField("score")
    private BigDecimal score;

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
