package com.bixin.ido.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
public class LPMingRewardVO {

    /**
     * 待领取收益
     */
    private String currentReward;

    /**
     * 锁仓量
     */
    private String stakingAmount;

}
