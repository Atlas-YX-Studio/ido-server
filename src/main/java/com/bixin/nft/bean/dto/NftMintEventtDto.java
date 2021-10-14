package com.bixin.nft.bean.dto;

import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.enums.NftEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public static NftEventDo of(NftMintEventtDto dto) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator(dto.getCreator())
                .seller("")
                .sellingPrice(BigDecimal.ZERO)
                .bider("")
                .bidPrice(BigDecimal.ZERO)
                .type(NftEventType.NFT_MINT_EVENT.getDesc())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        builder.payToken("");
        return builder.build();
    }
}
