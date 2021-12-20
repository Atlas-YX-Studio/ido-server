package com.bixin.ido.bean.dto;

import com.bixin.ido.bean.DO.LPStakingRecordDo;
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
public class LPStakeEventEventDto {

    private String sender;

    private TokenCode token_code;

    private BigDecimal amount;

    private Long seq_id;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class TokenCode {
        private String addr;
        private String module_name;
        private String name;
    }

    public String getTokenCodeStr() {
        return this.token_code.getAddr() + "::" + HexStringUtil.toStringHex(this.token_code.getModule_name().replaceAll("0x",""))
                + "::"+ HexStringUtil.toStringHex(this.token_code.getName().replaceAll("0x",""));
    }

    public static LPStakingRecordDo of(LPStakeEventEventDto dto) {
        LPStakingRecordDo.LPStakingRecordDoBuilder builder = LPStakingRecordDo.builder()
                .pairName(dto.getTokenCodeStr())
                .tokenA(dto.getTokenCodeStr())
                .tokenB(dto.getTokenCodeStr())
                .type("stake")
                .amount(dto.getAmount())
                .seqId(dto.getSeq_id())
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));

        return builder.build();
    }

}
