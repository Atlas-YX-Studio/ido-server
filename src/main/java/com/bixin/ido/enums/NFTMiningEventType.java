package com.bixin.ido.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NFTMiningEventType {
    STAKE_EVENT("NFTStakeEvent"),
    UNSTAKE_EVENT("NFTUnstakeEvent");

    private String desc;
}
