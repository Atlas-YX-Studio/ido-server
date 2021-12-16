package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
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

    private String pledgeCurrency;

    private Short pledgePrecision;

    private String pledgeAddress;

    private String payCurrency;

    private Short payPrecision;

    private String payAddress;

    private String assignCurrency;

    private Short assignPrecision;

    private String assignAddress;

    private BigDecimal rate;

    private BigDecimal raiseTotal;

    private BigDecimal currencyTotal;

    private BigDecimal saleTotal;

    private String icon;

    private String prdImg;

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

    private Integer weight;
}