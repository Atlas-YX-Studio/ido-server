package com.bixin.ido.server.scheduler;

import com.alibaba.fastjson.JSON;
import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.dto.SwapSymbolMarketDto;
import com.bixin.ido.server.bean.dto.SwapSymbolTickDto;
import com.bixin.ido.server.bean.dto.SwapTokenMarketDto;
import com.bixin.ido.server.bean.dto.SwapTokenTickDto;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.impl.SwapPathServiceImpl;
import com.bixin.ido.server.utils.BeanCopyUtil;
import com.bixin.ido.server.utils.TypeArgsUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScheduleSwapTokenData {

    private static final long EXPIRE_MINUTES = 5;

    @Resource
    private ISwapCoinsService swapCoinsService;
    @Resource
    private ISwapPathService swapPathService;
    @Resource
    private RedisCache redisCache;

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateSwapTokenMarket() {
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(new SwapCoins());
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            return;
        }
        swapCoinsList.forEach(swapCoins -> {
            String tokenName = swapCoins.getShortName();
            // 淘汰过期数据
            Date yesterday = DateUtils.addDays(new Date(), -1);
            redisCache.zRemoveRangeByScore(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + tokenName, 0, yesterday.getTime());
            // 获取交易数据
            List<Object> tickCaches = redisCache.zRange(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + tokenName, 0, -1);
            if (CollectionUtils.isEmpty(tickCaches)) {
                return;
            }
            List<SwapTokenTickDto> swapTicks = BeanCopyUtil.copyListProperties(tickCaches, tick -> JSON.parseObject((String) tick, SwapTokenTickDto.class));
            // 获取token 24H价格变化率，百分之x
            BigDecimal firstPrice = swapTicks.get(0).getUsdtExRate();
            BigDecimal lastPrice = swapTicks.get(swapTicks.size() - 1).getUsdtExRate();
            BigDecimal priceRate = lastPrice.subtract(firstPrice).divide(firstPrice, 18, RoundingMode.HALF_UP).multiply(new BigDecimal(2));
            // 获取token 24H总交易额，以USDT计价
            BigDecimal amountWithPrecision = swapTicks.stream().map(SwapTokenTickDto::getUsdtAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal usdtSwapAmount = amountWithPrecision.divide(new BigDecimal(swapCoins.getExchangePrecision()), 18, RoundingMode.HALF_UP);
            // 存入redis
            SwapTokenMarketDto swapTokenMarketDto = new SwapTokenMarketDto();
            swapTokenMarketDto.setPriceRate(priceRate);
            swapTokenMarketDto.setSwapAmount(usdtSwapAmount);
            redisCache.setValue(CommonConstant.SWAP_TOKEN_MARKET_PREFIX_KEY + tokenName, swapTokenMarketDto, EXPIRE_MINUTES, TimeUnit.MINUTES);
        });
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateSwapSymbolMarket() {
        List<SwapPathServiceImpl.Pool> poolList = swapPathService.getPoolList();
        if (CollectionUtils.isEmpty(poolList)) {
            return;
        }
        poolList.forEach(pool -> {
            String token0 = TypeArgsUtil.parseTypeObj(pool.tokenA).getName();
            String token1 = TypeArgsUtil.parseTypeObj(pool.tokenB).getName();
            // 淘汰过期数据
            Date yesterday = DateUtils.addDays(new Date(), -1);
            redisCache.zRemoveRangeByScore(CommonConstant.SWAP_SYMBOL_TICKS_PREFIX_KEY + token0 + "_" + token1, 0, yesterday.getTime());
            // 获取交易数据
            List<Object> tickCaches = redisCache.zRange(CommonConstant.SWAP_SYMBOL_TICKS_PREFIX_KEY + token0 + "_" + token1, 0, -1);
            if (CollectionUtils.isEmpty(tickCaches)) {
                return;
            }
            List<SwapSymbolTickDto> swapTicks = BeanCopyUtil.copyListProperties(tickCaches, tick -> JSON.parseObject((String) tick, SwapSymbolTickDto.class));

            List<SwapCoins> swapCoinsList0 = swapCoinsService.selectByDDL(SwapCoins.builder().address(pool.tokenA).build());
            if (CollectionUtils.isEmpty(swapCoinsList0)) {
                log.error("找不到币种:{}", pool.tokenA);
                return;
            }
            SwapCoins swapCoins0 = swapCoinsList0.get(0);
            List<SwapCoins> swapCoinsList1 = swapCoinsService.selectByDDL(SwapCoins.builder().address(pool.tokenA).build());
            if (CollectionUtils.isEmpty(swapCoinsList1)) {
                log.error("找不到币种:{}", pool.tokenA);
                return;
            }
            SwapCoins swapCoins1 = swapCoinsList1.get(0);
            // 24h 交易额
            BigDecimal usdtSwapAmount0 = swapTicks.stream().map(SwapSymbolTickDto::getUsdtAmount0).reduce(BigDecimal.ZERO, BigDecimal::add);
            usdtSwapAmount0 = usdtSwapAmount0.divide(new BigDecimal(swapCoins0.getExchangePrecision()), 18, RoundingMode.HALF_UP);
            BigDecimal usdtSwapAmount1 = swapTicks.stream().map(SwapSymbolTickDto::getUsdtAmount0).reduce(BigDecimal.ZERO, BigDecimal::add);
            usdtSwapAmount1 = usdtSwapAmount1.divide(new BigDecimal(swapCoins1.getExchangePrecision()), 18, RoundingMode.HALF_UP);
            // 最近一笔交易
            SwapSymbolTickDto swapSymbolTickDto = swapTicks.get(swapTicks.size() - 1);
            // 存入redis
            SwapSymbolMarketDto swapSymbolMarketDto = new SwapSymbolMarketDto();
            swapSymbolMarketDto.setToken0(pool.tokenA);
            swapSymbolMarketDto.setToken1(pool.tokenB);
            swapSymbolMarketDto.setReserve0(pool.tokenAmountA);
            swapSymbolMarketDto.setReserve1(pool.tokenAmountB);
            swapSymbolMarketDto.setUsdtExRate0(swapSymbolTickDto.getUsdtExRate0());
            swapSymbolMarketDto.setUsdtExRate1(swapSymbolTickDto.getUsdtExRate1());
            swapSymbolMarketDto.setSwapAmount(usdtSwapAmount0.add(usdtSwapAmount1));
            swapSymbolMarketDto.setLastSwap(swapSymbolTickDto.getSwapTime());
            redisCache.setValue(CommonConstant.SWAP_SYMBOL_MARKET_PREFIX_KEY + token0 + "_" + token1, swapSymbolMarketDto, EXPIRE_MINUTES, TimeUnit.MINUTES);
        });
    }

}
