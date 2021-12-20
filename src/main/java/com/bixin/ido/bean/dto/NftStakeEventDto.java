package com.bixin.ido.bean.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftStakeEventDto {

    private String sender;

    @JsonProperty("nft_id")
    private Long nftId;

    private Long order;

}
