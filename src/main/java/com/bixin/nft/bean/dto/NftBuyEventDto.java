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
 * nft购买事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftBuyEventDto {

    // nft id
    private Long id;
    // 售卖者
    private String seller;
    // 购买者
    private String buyer;
    // 购买价格
    private BigDecimal final_price;
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

    public static NftEventDo of(NftBuyEventDto dto, String type) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator("")
                .seller(dto.getSeller())
                .sellingPrice(dto.getFinal_price())
                .bider(dto.getBuyer())
                .bidPrice(dto.getFinal_price())
                .type(type)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        String token = dto.getPay_token_code().getName();
        if(StringUtils.isNoneEmpty(token)){
            builder.payTokenName(HexStringUtil.toStringHex(token.replaceAll("0x","")));
        }
        return builder.build();
    }
}
