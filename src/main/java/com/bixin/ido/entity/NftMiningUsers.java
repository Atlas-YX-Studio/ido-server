package com.bixin.ido.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户NFT挖矿表
 *
 * @author Xiang Feihan
 * @since 2021-11-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("nft_mining_users")
public class NftMiningUsers implements Serializable {

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
     * 总算力
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 收益
     */
    @TableField("reward")
    private BigDecimal reward;

    /**
     * 待结算收益
     */
    @TableField("pending_reward")
    private BigDecimal pendingReward;

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
