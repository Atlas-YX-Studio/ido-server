package com.bixin.ido.server.bean.dto;

import com.bixin.ido.server.bean.DO.LiquidityPool;
import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @author zhangcheng
 * create   2021/9/8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiquidityPoolEventDto {

    private String signer;

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

    public static LiquidityPool of(LiquidityPoolEventDto dto) {
        LiquidityPool.LiquidityPoolBuilder builder = LiquidityPool.builder()
                .userAddress(dto.getSigner())
//                .tokenCodeX("")
//                .tokenCodeY("")
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
