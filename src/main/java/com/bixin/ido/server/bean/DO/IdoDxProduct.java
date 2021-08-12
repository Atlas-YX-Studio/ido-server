package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author zhangcheng
 * @create 2021-08-06 5:34 下午
 */
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

    private BigDecimal pledgeTotal;

    private BigDecimal raiseTotal;

    private BigDecimal currencyTotal;

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