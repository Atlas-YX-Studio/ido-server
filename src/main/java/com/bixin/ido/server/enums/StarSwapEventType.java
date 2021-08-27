package com.bixin.ido.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create  2021-08-25 3:18 下午
 */
@Getter
@AllArgsConstructor
public enum StarSwapEventType {

    CREATE_PAIR_EVENT("CreatePairEvent"),
    SWAP_EVENT("SwapEvent"),
    LIQUIDITY_EVENT("LiquidityEvent");

    private String desc;

    public static StarSwapEventType of(String desc) {
        switch (desc) {
            case "CreatePairEvent":
                return CREATE_PAIR_EVENT;
            case "SwapEvent":
                return SWAP_EVENT;
            case "LiquidityEvent":
                return LIQUIDITY_EVENT;
            default:
                return null;
        }
    }

}
