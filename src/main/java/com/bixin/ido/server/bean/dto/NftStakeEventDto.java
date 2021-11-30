package com.bixin.ido.server.bean.dto;

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

    private Long nftId;

    private Long order;

}
