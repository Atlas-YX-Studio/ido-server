package com.bixin.ido.server.bean.dto;

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
 * @author zhangcheng
 * create   2021/9/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwapEventDto {

    private String signer;

    private BigDecimal amount_x_in;

    private BigDecimal amount_y_in;

    private BigDecimal amount_x_out;

    private BigDecimal amount_y_out;

    private BigDecimal reserve_x;

    private BigDecimal reserve_y;

    private long block_timestamp_last;

    private X_token_code x_token_code;
    private Y_token_code y_token_code;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class X_token_code {
        private String addr;
        private String module_name;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Y_token_code {
        private String addr;
        private String module_name;
        private String name;
    }

    public static SwapUserRecord of(SwapEventDto dto) {
        SwapUserRecord.SwapUserRecordBuilder builder = SwapUserRecord.builder()
                .userAddress(dto.getSigner())
//                .tokenCodeX("")
//                .tokenCodeY("")
                .tokenInX(dto.getAmount_x_in())
                .tokenInY(dto.getAmount_y_in())
                .tokenOutX(dto.getAmount_x_out())
                .tokenOutY(dto.getAmount_y_out())
                .reserveAmountX(dto.getReserve_x())
                .reserveAmountY(dto.reserve_y)
                .swapTime(dto.getBlock_timestamp_last())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));

        String tokenX = dto.getX_token_code().getName();
        String tokenY = dto.getY_token_code().getName();
        if(StringUtils.isNoneEmpty(tokenX)){
            builder.tokenCodeX(HexStringUtil.toStringHex(tokenX.replaceAll("0x","")));
        }
        if(StringUtils.isNoneEmpty(tokenY)){
            builder.tokenCodeY(HexStringUtil.toStringHex(tokenY.replaceAll("0x","")));
        }

        return builder.build();
    }

}
