package com.bixin.ido.server.bean.dto;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
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
public class LiquidityEventDto {


    private String signer;

    private int direction;

    private BigDecimal amount_x;

    private BigDecimal amount_y;

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

    public static LiquidityUserRecord of(LiquidityEventDto dto) {
        LiquidityUserRecord.LiquidityUserRecordBuilder builder = LiquidityUserRecord.builder()
                .userAddress(dto.getSigner())
                .tokenCodeX(dto.getXTokenCodeStr())
                .tokenCodeY(dto.getYTokenCodeStr())
                .amountX(dto.getAmount_x())
                .amountY(dto.getAmount_y())
                .direction((short) dto.getDirection())
                .reserveAmountX(dto.getReserve_x())
                .reserveAmountY(dto.getAmount_y())
                .liquidityTime(dto.getBlock_timestamp_last())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
        return builder.build();
    }

}
