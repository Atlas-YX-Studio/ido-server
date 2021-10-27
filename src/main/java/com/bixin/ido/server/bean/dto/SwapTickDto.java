package com.bixin.ido.server.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwapTickDto {

    private String direction;

    private BigDecimal amount;

    private BigDecimal usdtAmount;

    private BigDecimal usdtExRate;

}
