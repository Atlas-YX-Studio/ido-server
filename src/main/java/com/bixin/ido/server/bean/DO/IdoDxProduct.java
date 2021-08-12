package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IdoDxProduct {
    private Long id;

    private String prdName;

    private String currency;

    private String baseCurrency;

    private BigDecimal rate;

    private BigDecimal raiseTotal;

    private BigDecimal currencyTotal;

    private Short tokenPrecision;

    private String address;

    private String icon;

    private String state;

    private String prdDesc;

    private String prdDescEn;

    private String ruleDesc;

    private String ruleDescEn;

    private Long createTime;

    private Long updateTime;

    private Long startTime;

    private Long endTime;

    private Long pledgeStartTime;

    private Long pledgeEndTime;

    private Long lockStartTime;

    private Long lockEndTime;

    private Long payStartTime;

    private Long payEndTime;

    private Long assignmentStartTime;

    private Long assignmentEndTime;

}