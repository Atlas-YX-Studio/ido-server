package com.bixin.ido.server.provider.impl;

import com.alibaba.fastjson.JSON;
import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.bean.dto.SwapSymbolTickDto;
import com.bixin.ido.server.bean.dto.SwapTokenTickDto;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.enums.DirectionType;
import com.bixin.ido.server.provider.IStarSwapProvider;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.ISwapUserRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author zhangcheng
 * create  2021-08-25 3:04 下午
 */
@Component
public class SwapUserRecordProviderImpl implements IStarSwapProvider<SwapUserRecord> {

    @Resource
    private ISwapUserRecordService swapUserRecordService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ISwapPathService swapPathService;

    @Value("${ido.star.swap.usdt-address}")
    private String usdtAddress;

    @Override
    public void dispatcher(SwapUserRecord idoSwapUserRecord) {
        swapUserRecordService.insert(idoSwapUserRecord);
        // 保存tick
        String tokenX = idoSwapUserRecord.getTokenCodeX();
        String tokenY = idoSwapUserRecord.getTokenCodeY();
        // tokenX
        if (BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenInX()) && !BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenOutX())) {
            cacheSymbolTick(tokenX, tokenY, DirectionType.LONG.name(), idoSwapUserRecord.getTokenOutX(), idoSwapUserRecord.getTokenInY(), idoSwapUserRecord.getSwapTime());
            cacheTokenTick(tokenX, DirectionType.LONG.name(), idoSwapUserRecord.getTokenOutX(), idoSwapUserRecord.getSwapTime());
        } else {
            cacheSymbolTick(tokenX, tokenY, DirectionType.SHORT.name(), idoSwapUserRecord.getTokenInX(), idoSwapUserRecord.getTokenOutY(), idoSwapUserRecord.getSwapTime());
            cacheTokenTick(tokenX, DirectionType.SHORT.name(), idoSwapUserRecord.getTokenInX(), idoSwapUserRecord.getSwapTime());
        }
        // tokenY
        if (BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenInY()) && !BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenOutY())) {
            cacheTokenTick(tokenY, DirectionType.LONG.name(), idoSwapUserRecord.getTokenOutY(), idoSwapUserRecord.getSwapTime());
        } else {
            cacheTokenTick(tokenY, DirectionType.SHORT.name(), idoSwapUserRecord.getTokenInY(), idoSwapUserRecord.getSwapTime());
        }
    }

    /**
     * 缓存交易对数据
     * @param token0
     * @param token1
     * @param direction0
     * @param amount0
     * @param amount1
     * @param time
     */
    private void cacheSymbolTick(String token0, String token1, String direction0, BigDecimal amount0, BigDecimal amount1, long time) {
        BigDecimal usdtExRate0 = StringUtils.equals(token0, CommonConstant.USDT_NAME) ? BigDecimal.ONE :
                swapPathService.getCoinPriceInfos().getOrDefault(token0 + "_" + usdtAddress, BigDecimal.ZERO);
        BigDecimal usdtExRate1 = StringUtils.equals(token1, CommonConstant.USDT_NAME) ? BigDecimal.ONE :
                swapPathService.getCoinPriceInfos().getOrDefault(token1 + "_" + usdtAddress, BigDecimal.ZERO);

        SwapSymbolTickDto swapSymbolTickDto = new SwapSymbolTickDto();
        swapSymbolTickDto.setToken0(token0);
        swapSymbolTickDto.setToken1(token1);
        swapSymbolTickDto.setDirection0(direction0);
        swapSymbolTickDto.setAmount0(amount0);
        swapSymbolTickDto.setAmount1(amount1);
        swapSymbolTickDto.setUsdtExRate0(usdtExRate0);
        swapSymbolTickDto.setUsdtExRate1(usdtExRate1);
        swapSymbolTickDto.setUsdtAmount0(amount0.multiply(usdtExRate0));
        swapSymbolTickDto.setUsdtAmount1(amount1.multiply(usdtExRate1));
        swapSymbolTickDto.setSwapTime(time);
        redisCache.zAdd(CommonConstant.SWAP_SYMBOL_TICKS_PREFIX_KEY + token0 + "_" + token1, JSON.toJSON(swapSymbolTickDto), time);
    }

    /**
     * 缓存token交易数据
     * @param token
     * @param direction
     * @param amount
     * @param time
     */
    private void cacheTokenTick(String token, String direction, BigDecimal amount, long time) {
        BigDecimal usdtExRate = StringUtils.equals(token, usdtAddress) ? BigDecimal.ONE :
                swapPathService.getCoinPriceInfos().getOrDefault(token + "_" + usdtAddress, BigDecimal.ZERO);

        SwapTokenTickDto swapTokenTickDto = new SwapTokenTickDto();
        swapTokenTickDto.setDirection(direction);
        swapTokenTickDto.setAmount(amount);
        swapTokenTickDto.setUsdtExRate(usdtExRate);
        swapTokenTickDto.setUsdtAmount(amount.multiply(usdtExRate));
        swapTokenTickDto.setSwapTime(time);
        redisCache.zAdd(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + token, JSON.toJSON(swapTokenTickDto), time);
    }

}
