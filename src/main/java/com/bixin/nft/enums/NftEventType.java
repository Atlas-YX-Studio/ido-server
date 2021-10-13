package com.bixin.nft.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NftEventType {

    NFT_MINT_EVENT("NFTMintEvent"),
    NFT_SELL_EVENT("NFTSellEvent"),
    NFT_BID_EVENT("NFTBidEvent"),
    NFT_BUY_EVENT("NFTBuyEvent"),
    NFT_ACCEPT_BID_EVENT("NFTAcceptBidEvent"),
    NFT_OFFLINE_EVENT("NFTOfflineEvent"),
    BOX_OPEN_EVENT("BoxOpenEvent"),
    BOX_BID_EVENT("BoxBidEvent"),
    BOX_BUY_EVENT("BoxBuyEvent"),
    BOX_ACCEPT_BID_EVENT("BoxAcceptBidEvent");

    private String desc;

}