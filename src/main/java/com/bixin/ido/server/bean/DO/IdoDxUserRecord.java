package com.bixin.ido.server.bean.DO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zhangcheng
 * @create 2021-08-06 5:34 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdoDxUserRecord {
    private Long id;

    private Long prdId;

    private String address;

    private BigDecimal amount;

    private BigDecimal gasCost;

    private String currency;

    private String state;

    private Integer version;

    private Long createTime;

    private Long updateTime;

}