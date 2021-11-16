package com.bixin.ido.server.bean.vo;

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
public class RewardVO {

    /**
     * 待领取收益
     */
    private String currentReward;

    /**
     * 锁仓收益
     */
    private String lockedReward;

    /**
     * 已释放收益
     */
    private String freedReward;

}
