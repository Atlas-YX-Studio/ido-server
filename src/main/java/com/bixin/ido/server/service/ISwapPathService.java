package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.vo.CoinStatsInfoVO;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;

import java.math.BigDecimal;
import java.util.List;

public interface ISwapPathService {
    SwapPathInVO exchangeIn(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    SwapPathOutVO exchangeOut(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    BigDecimal totalAssets();

    List<CoinStatsInfoVO> coinInfos(int pageNum, int pageSize);
}
