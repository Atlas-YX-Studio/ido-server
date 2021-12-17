package com.bixin.ido.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* @Class: LPStakingRecordDo
* @Description: 用户lp质押/解押记录表
* @author: 系统
* @created: 2021-11-22
*/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LPStakingRecordDo implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 交易对名
     */
    private String pairName;

    /**
     * 币种A
     */
    private String tokenA;

    /**
     * 币种B
     */
    private String tokenB;

    /**
     * 操作类型: stake/unstake
     */
    private String type;

    /**
     * 额度
     */
    private BigDecimal amount;

    /**
     * 序列id
     */
    private Long seqId;

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