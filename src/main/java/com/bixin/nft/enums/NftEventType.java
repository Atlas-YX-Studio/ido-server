package com.bixin.nft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NftEventType {

    SWAP_EVENT("SwapEvent"),
    LIQUIDITY_EVENT("LiquidityEvent");

    private String desc;

    public static NftEventType of(String desc) {
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