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
 * 出价nft
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftBidEventtDto {

    // nft id
    private Long id;
    // 售卖者
    private String seller;
    // 售价
    private BigDecimal selling_price;
    // 出价者
    private String bider;
    // 出价
    private BigDecimal bid_price;
    // 支付token
    private Pay_token_code pay_token_code;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Pay_token_code {
        private String addr;
        private String module_name;
        private String name;
    }

    public static NftEventDo of(NftBidEventtDto dto) {
        NftEventDo.NftEventDoBuilder builder= NftEventDo.builder()
                .nftId(dto.getId())
                .creator("")
                .seller(dto.getSeller())
                .sellingPrice(dto.getSelling_price())
                .bider(dto.getBider())
                .bidPrice(dto.getBid_price())
                .type("出价")
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        String token = dto.getPay_token_code().getName();
        if(StringUtils.isNoneEmpty(token)){
            builder.payTokenAddr(HexStringUtil.toStringHex(token.replaceAll("0x","")));
        }
        return builder.build();
    }


}
