package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.TradingPoolDo;

import java.math.BigDecimal;

public interface ITradingMiningService {

    int addTradingAmount(String userAddress, Long poolId, BigDecimal tradingAmount);

    TradingPoolDo getTradingPoolByTokenCode(String tokenA, String tokenB);

}
