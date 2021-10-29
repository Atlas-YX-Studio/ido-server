package com.bixin.ido.server.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwapTokenTickDto {

    private String direction;

    private BigDecimal amount;

    private BigDecimal usdtAmount;

    private BigDecimal usdtExRate;

    private long swapTime;

}
