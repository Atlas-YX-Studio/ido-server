package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;

import java.math.BigDecimal;

public interface ISwapPathService {
    SwapPathInVO exchangeIn(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    SwapPathOutVO exchangeOut(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode);

    BigDecimal totalAssets();
}
