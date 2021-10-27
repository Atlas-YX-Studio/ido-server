package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.vo.CoinStatsInfoVO;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ISwapPathService {
    SwapPathInVO exchangeIn(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    SwapPathOutVO exchangeOut(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    BigDecimal totalAssets();

    Map<String, BigDecimal> getCoinPriceInfos();

    List<CoinStatsInfoVO> coinInfos(int pageNum, int pageSize);
}
