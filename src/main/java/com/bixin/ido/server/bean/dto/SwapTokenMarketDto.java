package com.bixin.ido.server.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwapTokenMarketDto {

    private BigDecimal priceRate;

    private BigDecimal swapAmount;

}
