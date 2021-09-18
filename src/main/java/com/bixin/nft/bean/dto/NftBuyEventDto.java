package com.bixin.nft.bean.dto;

import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * nft购买事件 = 获得
 *
 * 铸造 = met出来 没有
 * 上架 = 售卖 没有
 * 报价 = bid
 * 下架 = 取消 （还没有做）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NftBuyEventDto {
    private String seller;
    private String bider;
    private Long id;
    private BigDecimal selling_price;
    private BigDecimal bid_price;
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
//    //将 dto 转为do
//    public static SwapUserRecord of(NftBuyEventDto dto) {
//       // SwapUserRecord.SwapUserRecordBuilder builder = SwapUserRecord.builder()
////                .userAddress(dto.getSigner())
////                .tokenCodeX("")
////                .tokenCodeY("")
////                .tokenInX(dto.getAmount_x_in())
////                .tokenInY(dto.getAmount_y_in())
////                .tokenOutX(dto.getAmount_x_out())
////                .tokenOutY(dto.getAmount_y_out())
////                .reserveAmountX(dto.getReserve_x())
////                .reserveAmountY(dto.reserve_y)
////                .swapTime(dto.getBlock_timestamp_last())
////                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
//
////        String tokenX = dto.getX_token_code().getName();
////        String tokenY = dto.getY_token_code().getName();
////        if(StringUtils.isNoneEmpty(tokenX)){
////            builder.tokenCodeX(HexStringUtil.toStringHex(tokenX.replaceAll("0x","")));
////        }
////        if(StringUtils.isNoneEmpty(tokenY)){
////            builder.tokenCodeY(HexStringUtil.toStringHex(tokenY.replaceAll("0x","")));
////        }
//
//        return builder.build();
//    }

}
