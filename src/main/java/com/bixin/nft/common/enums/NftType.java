package com.bixin.nft.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NftType {

    NORMAL("normal"),
    COMPOSITE_CARD("composite_card"),
    COMPOSITE_ELEMENT("composite_element");

    private String type;

}
