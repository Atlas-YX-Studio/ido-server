package com.bixin.ido.server.scheduler;

import com.alibaba.fastjson.JSON;
import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.dto.SwapTickDto;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.utils.BeanCopyUtil;
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

@Component
public class ScheduleSwapTokenData {

    private static final long EXPIRE_MINUTES = 5;

    @Resource
    private ISwapCoinsService swapCoinsService;
    @Resource
    private RedisCache redisCache;

    @Scheduled(cron = "0 */5 * * * ?")
    public void calculateSwapData() {
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(new SwapCoins());
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            return;
        }
        swapCoinsList.forEach(swapCoins -> {
            String tokenName = swapCoins.getShortName();
            List<Object> tickCaches = redisCache.zRange(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + tokenName, 0, -1);
            if (CollectionUtils.isEmpty(tickCaches)) {
                return;
            }
            updateSwapTick(tokenName);
            List<SwapTickDto> swapTicks = BeanCopyUtil.copyListProperties(tickCaches, tick -> JSON.parseObject((String) tick, SwapTickDto.class));
            updatePriceRate(tokenName, swapTicks);
            updateUsdtSwapAmount(tokenName, swapTicks, new BigDecimal(swapCoins.getExchangePrecision()));
        });
    }

    /**
     * 更新交易记录
     * @param tokenName
     */
    private void updateSwapTick(String tokenName) {
        Date yesterday = DateUtils.addDays(new Date(), -1);
        redisCache.zRemoveRangeByScore(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + tokenName, 0, yesterday.getTime());
    }

    /**
     * 获取token 24H价格变化率
     * @param tokenName
     * @param swapTicks
     * @return
     */
    private void updatePriceRate(String tokenName, List<SwapTickDto> swapTicks) {
        if (CollectionUtils.isEmpty(swapTicks)) {
            return;
        }
        BigDecimal firstPrice = swapTicks.get(0).getUsdtExRate();
        BigDecimal lastPrice = swapTicks.get(swapTicks.size() - 1).getUsdtExRate();
        BigDecimal priceRate = lastPrice.subtract(firstPrice).divide(firstPrice, 18, RoundingMode.HALF_UP).multiply(new BigDecimal(2));
        redisCache.setValue(CommonConstant.SWAP_TOKEN_PRICE_RATE_PREFIX_KEY + tokenName, priceRate.toPlainString(), EXPIRE_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 获取token 24H总交易额，以USDT计价
     * @param tokenName
     * @param swapTicks
     * @return
     */
    private void updateUsdtSwapAmount(String tokenName, List<SwapTickDto> swapTicks, BigDecimal precision) {
        BigDecimal amountWithPrecision = swapTicks.stream().map(SwapTickDto::getUsdtAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal amount = amountWithPrecision.divide(precision, 18, RoundingMode.HALF_UP);
        redisCache.setValue(CommonConstant.SWAP_TOKEN_SWAP_AMOUNT_PREFIX_KEY + tokenName, amount.toPlainString(), EXPIRE_MINUTES, TimeUnit.MINUTES);
    }
}
