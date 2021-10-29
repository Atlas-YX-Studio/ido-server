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
import com.bixin.ido.server.utils.TypeArgsUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(PathConstant.SWAP_REQUEST_PATH_PREFIX + "/api")
public class SwapApiController {

    @Resource
    private ISwapPathService iSwapPathService;
    @Resource
    private RedisCache redisCache;

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
            String token0 = TypeArgsUtil.parseTypeObj(pool.tokenA).getName();
            String token1 = TypeArgsUtil.parseTypeObj(pool.tokenB).getName();
            SwapSymbolMarketDto swapTokenMarketDto = redisCache.getValue(CommonConstant.SWAP_SYMBOL_MARKET_PREFIX_KEY + token0 + "_" + token1, SwapSymbolMarketDto.class);

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
