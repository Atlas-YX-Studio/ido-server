package com.bixin.ido.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdoDxUserRecord {
    private Long id;

    private String prdAddress;

    private String userAddress;

    private BigDecimal amount;

    private BigDecimal gasCost;

    private BigDecimal tokenAmount;

    private String currency;

    private String extInfo;

    private Short tokenVersion;

    private Long createTime;

    private Long updateTime;

}