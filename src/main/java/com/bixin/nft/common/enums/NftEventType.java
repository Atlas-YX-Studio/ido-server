package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NftEventType {

    NFT_MINT_EVENT("NFTMintEvent"),
    NFT_RESOLVE_EVENT("NFTResolveEvent"),
    NFT_SELL_EVENT("NFTSellEvent"),
    NFT_SELL_EVENT_V2("NFTSellEventV2"),
    NFT_BID_EVENT("NFTBidEvent"),
    NFT_BID_EVENT_V2("NFTBidEventV2"),
    NFT_BUY_EVENT("NFTBuyEvent"),
    NFT_BUY_EVENT_V2("NFTBuyEventV2"),
    NFT_ACCEPT_BID_EVENT("NFTAcceptBidEvent"),
    NFT_ACCEPT_BID_EVENT_V2("NFTAcceptBidEventV2"),
    NFT_OFFLINE_EVENT("NFTOfflineEvent"),
    NFT_OFFLINE_EVENT_V2("NFTOfflineEventV2"),
    NFT_CHANGE_PRICE_EVENT("NFTChangePriceEvent"),
    NFT_BUY_BACK_SELL_EVENT("NFTBuyBackSellEvent"),
    BOX_OFFERING_SELL_EVENT("BoxOfferingSellEvent"),
    BOX_SELL_EVENT("BoxSellEvent"),
    BOX_SELL_EVENT_V2("BoxSellEventV2"),
    BOX_OPEN_EVENT("BoxOpenEvent"),
    BOX_BID_EVENT("BoxBidEvent"),
    BOX_BID_EVENT_V2("BoxBidEventV2"),
    BOX_BUY_EVENT("BoxBuyEvent"),
    BOX_BUY_EVENT_V2("BoxBuyEventV2"),
    BOX_ACCEPT_BID_EVENT("BoxAcceptBidEvent"),
    BOX_ACCEPT_BID_EVENT_V2("BoxAcceptBidEventV2"),
    BOX_OFFLINE_EVENT("BoxOfflineEvent"),
    BOX_OFFLINE_EVENT_V2("BoxOfflineEventV2"),
    BOX_CHANGE_PRICE_EVENT("BoxChangePriceEvent"),

    ;

    private String desc;

}