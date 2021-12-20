package com.bixin.ido.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: MiningHarvestRecordDo
* @Description: 挖矿收益提取记录表
* @author: 系统
* @created: 2021-11-09
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MiningHarvestRecordDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 提取收益
     */
    private BigDecimal amount;

    /**
     * 挖矿类型，TRADING：交易挖矿，LP_STAKING：Lp质押挖矿
     */
    private String miningType;

    /**
     * 收益类型，CURRENT：当前收益，FREED：已释放收益
     */
    private String rewardType;

    /**
     * 操作状态，PENDING：待确认，FAILED：失败，SUCCESS：成功
     */
    private String status;

    /**
     * 交易哈希
     */
    private String hash;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    private static final long serialVersionUID = 1L;
}