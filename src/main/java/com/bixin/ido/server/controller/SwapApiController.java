package com.bixin.ido.server.controller;

import com.bixin.ido.server.bean.dto.SwapSymbolMarketDto;
import com.bixin.ido.server.bean.vo.SwapPairMarketVO;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.constants.PathConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.impl.SwapPathServiceImpl;
import com.bixin.ido.server.utils.BeanCopyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX + "/api")
public class SwapApiController {

    @Resource
    private ISwapPathService iSwapPathService;
    @Resource
    private RedisCache redisCache;

    @Value("${ido.star.swap.usdt-address}")
    private String usdtAddress;

    @GetMapping("/meta")
    public R meta() {
        return R.success(iSwapPathService.meta());
    }

    @GetMapping("/market")
    public R market(){
        List<SwapPathServiceImpl.Pool> poolList = iSwapPathService.getPoolList();
        if (CollectionUtils.isEmpty(poolList)) {
            return R.success();
        }
        List<SwapPairMarketVO> swapPairMarketVOList = BeanCopyUtil.copyListProperties(poolList, pool -> {
            SwapSymbolMarketDto swapTokenMarketDto = redisCache.getValue(CommonConstant.SWAP_SYMBOL_MARKET_PREFIX_KEY + pool.tokenA + "_" + pool.tokenB, SwapSymbolMarketDto.class);
            if (swapTokenMarketDto == null) {
                Map<String, BigDecimal> coinPriceInfos = iSwapPathService.getCoinPriceInfos();
                return SwapPairMarketVO.builder()
                        .token0(pool.tokenA)
                        .token1(pool.tokenB)
                        .reserve0(pool.tokenAmountA)
                        .reserve1(pool.tokenAmountB)
                        .usdtPrice0(coinPriceInfos.getOrDefault(pool.tokenA + "_" + usdtAddress, BigDecimal.ZERO))
                        .usdtPrice1(coinPriceInfos.getOrDefault(pool.tokenB + "_" + usdtAddress, BigDecimal.ZERO))
                        .build();
            }
            return SwapPairMarketVO.builder()
                    .token0(swapTokenMarketDto.getToken0())
                    .token1(swapTokenMarketDto.getToken1())
                    .reserve0(swapTokenMarketDto.getReserve0())
                    .reserve1(swapTokenMarketDto.getReserve1())
                    .usdtPrice0(swapTokenMarketDto.getUsdtExRate0())
                    .usdtPrice1(swapTokenMarketDto.getUsdtExRate1())
                    .swapAmount(swapTokenMarketDto.getSwapAmount())
                    .lastSwap(swapTokenMarketDto.getLastSwap())
                    .build();
        });
        return R.success(swapPairMarketVOList);
    }

}
