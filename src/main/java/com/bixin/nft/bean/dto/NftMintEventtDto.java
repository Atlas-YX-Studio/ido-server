package com.bixin.nft.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 铸造 nft
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftMintEventtDto {

    // nft id
    private Long id;
    // 铸造者
    private String creator;
}
