package com.bixin.nft.bean.dto;

import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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

    public static NftEventDo of(NftMintEventtDto dto, String type) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator(dto.getCreator())
                .seller("")
                .sellingPrice(BigDecimal.ZERO)
                .bider("")
                .bidPrice(BigDecimal.ZERO)
                .type(type)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        return builder.build();
    }
}
