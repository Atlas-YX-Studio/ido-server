package com.bixin.ido.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 挖矿收益提取记录表
 *
 * @author Xiang Feihan
 * @since 2021-11-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("mining_harvest_records")
public class MiningHarvestRecords implements Serializable {

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
     * 提取收益
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 挖矿类型，TRADING：交易挖矿，LP_STAKING：Lp质押挖矿
     */
    @TableField("mining_type")
    private String miningType;

    /**
     * 收益类型，CURRENT：当前收益，FREED：已释放收益
     */
    @TableField("reward_type")
    private String rewardType;

    /**
     * 操作状态，PENDING：待确认，FAILED：失败，SUCCESS：成功
     */
    @TableField("status")
    private String status;

    /**
     * 交易哈希
     */
    @TableField("hash")
    private String hash;

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
