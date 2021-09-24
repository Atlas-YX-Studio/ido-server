package com.bixin.nft.bean.dto;

import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * nft出售事件 =  上架
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftSellEventtDto {
    // nft id
    private Long id;
    // 出售者
    private String seller;
    // 出售价格
    private BigDecimal selling_price;
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

    public static NftEventDo of(NftSellEventtDto dto,String type) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator("")
                .seller(dto.getSeller())
                .sellingPrice(dto.getSelling_price())
                .bider("")
                .bidPrice(BigDecimal.ZERO)
                .type(type)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        Pay_token_code payTokenCode = dto.getPay_token_code();
        String tokenCode = "";
        if(!ObjectUtils.isEmpty(payTokenCode)){
            tokenCode = payTokenCode.getAddr() + "::" + HexStringUtil.toStringHex(payTokenCode.getName().replaceAll("0x",""))
                    + "::"+ HexStringUtil.toStringHex(payTokenCode.getModule_name().replaceAll("0x",""));
        }
        builder.payToken(tokenCode);
        return builder.build();
    }

}
