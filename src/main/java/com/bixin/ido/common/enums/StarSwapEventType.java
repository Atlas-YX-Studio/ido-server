package com.bixin.ido.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create  2021-08-25 3:18 下午
 */
@Getter
@AllArgsConstructor
public enum StarSwapEventType {

    SWAP_EVENT("SwapEvent"),
    LIQUIDITY_EVENT("LiquidityEvent");

    private String desc;

    public static StarSwapEventType of(String desc) {
        switch (desc) {
            case "SwapEvent":
                return SWAP_EVENT;
            case "LiquidityEvent":
                return LIQUIDITY_EVENT;
            default:
                return null;
        }
    }

}
