package com.bixin.nft.bean.dto;

import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.enums.NftEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 取消事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoxOffLineEventDto {
    // 售卖者
    private String seller;
    // nft id
    private Long id;
    // box token
    private PayTokenCode box_token_code;
    // 支付token
    private PayTokenCode pay_token_code;
    // 数量
    private BigDecimal quantity;
    // 售价
    private BigDecimal selling_price;
    // 出价
    private BigDecimal bid_price;
    // 出价者
    private String bidder;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class PayTokenCode {
        private String addr;
        private String module_name;
        private String name;
    }

    public String getBoxTokenCodeStr() {
        return this.box_token_code.getAddr() + "::" + HexStringUtil.toStringHex(this.box_token_code.getModule_name().replaceAll("0x",""))
                + "::"+ HexStringUtil.toStringHex(this.box_token_code.getName().replaceAll("0x",""));
    }

    public String getPayTokenCodeStr() {
        return this.pay_token_code.getAddr() + "::" + HexStringUtil.toStringHex(this.pay_token_code.getModule_name().replaceAll("0x",""))
                + "::"+ HexStringUtil.toStringHex(this.pay_token_code.getName().replaceAll("0x",""));
    }

    public static NftEventDo of(BoxOffLineEventDto dto) {
        NftEventDo.NftEventDoBuilder builder = NftEventDo.builder()
                .nftId(dto.getId())
                .creator("")
                .seller(dto.getSeller())
                .sellingPrice(dto.getSelling_price())
                .bider(dto.getBidder())
                .bidPrice(dto.getBid_price())
                .type(NftEventType.BOX_OFFLINE_EVENT.getDesc())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        PayTokenCode payTokenCode = dto.getPay_token_code();
        String tokenCode = "";
        if(!ObjectUtils.isEmpty(payTokenCode)){
            tokenCode = payTokenCode.getAddr() + "::" + HexStringUtil.toStringHex(payTokenCode.getModule_name().replaceAll("0x",""))
                    + "::"+ HexStringUtil.toStringHex(payTokenCode.getName().replaceAll("0x",""));
        }
        builder.payToken(tokenCode);
        return builder.build();
    }

}