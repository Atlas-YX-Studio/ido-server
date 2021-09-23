package com.bixin.nft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NftEventType {

    NFTMINTEVENT("NFTMintEvent"),
    NFTSELLEVENT("NFTSellEvent"),
    NFTBIDEVENT("NFTBidEvent"),
    NFTBUYEVENT("NFTBuyEvent"),
    NFTOFFLINEEVENT("NFTOfflineEvent");

    private String desc;

}