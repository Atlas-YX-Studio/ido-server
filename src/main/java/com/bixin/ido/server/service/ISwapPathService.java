package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.vo.CoinStatsInfoVO;
import com.bixin.ido.server.bean.vo.SwapMetaVO;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;
import com.bixin.ido.server.service.impl.SwapPathServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ISwapPathService {
    SwapPathInVO exchangeIn(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    SwapPathOutVO exchangeOut(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    BigDecimal totalAssets();

    List<SwapPathServiceImpl.Pool> getPoolList();

    Map<String, BigDecimal> getCoinPriceInfos();

    List<CoinStatsInfoVO> coinInfos(int pageNum, int pageSize);

    Integer getCoinPrecision(String coinAddress);

    SwapMetaVO meta();
}
