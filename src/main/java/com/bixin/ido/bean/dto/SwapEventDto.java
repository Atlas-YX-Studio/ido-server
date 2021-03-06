package com.bixin.ido.bean.dto;

import com.bixin.ido.bean.DO.SwapUserRecord;
import com.bixin.common.utils.HexStringUtil;
import com.bixin.common.utils.LocalDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public String getXTokenCodeStr() {
        return this.x_token_code.getAddr() + "::" + HexStringUtil.toStringHex(this.x_token_code.getModule_name().replaceAll("0x",""))
                + "::"+ HexStringUtil.toStringHex(this.x_token_code.getName().replaceAll("0x",""));
    }

    public String getYTokenCodeStr() {
        return this.y_token_code.getAddr() + "::" + HexStringUtil.toStringHex(this.y_token_code.getModule_name().replaceAll("0x",""))
                + "::"+ HexStringUtil.toStringHex(this.y_token_code.getName().replaceAll("0x",""));
    }

    public static SwapUserRecord of(SwapEventDto dto) {
        SwapUserRecord.SwapUserRecordBuilder builder = SwapUserRecord.builder()
                .userAddress(dto.getSigner())
                .tokenCodeX(dto.getXTokenCodeStr())
                .tokenCodeY(dto.getYTokenCodeStr())
                .tokenInX(dto.getAmount_x_in())
                .tokenInY(dto.getAmount_y_in())
                .tokenOutX(dto.getAmount_x_out())
                .tokenOutY(dto.getAmount_y_out())
                .reserveAmountX(dto.getReserve_x())
                .reserveAmountY(dto.reserve_y)
                .swapTime(dto.getBlock_timestamp_last())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));

        return builder.build();
    }

}
