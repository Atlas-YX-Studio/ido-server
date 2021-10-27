package com.bixin.ido.server.provider.impl;

import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.bean.dto.SwapTickDto;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.enums.DirectionType;
import com.bixin.ido.server.provider.IStarSwapProvider;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.ISwapUserRecordService;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public void dispatcher(SwapUserRecord idoSwapUserRecord) {
        swapUserRecordService.insert(idoSwapUserRecord);
        // 保存tick
        String tokenX = idoSwapUserRecord.getTokenCodeX();
        String tokenY = idoSwapUserRecord.getTokenCodeY();
        // tokenX
        if (BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenInX()) && !BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenOutX())) {
            BigDecimal amount = idoSwapUserRecord.getTokenOutX();
            BigDecimal usdtExRate = StringUtils.equals(tokenX, CommonConstant.USDT_NAME) ? BigDecimal.ONE : swapPathService.getCoinPriceInfos().get(tokenX);
            addSwapTick(tokenX, DirectionType.SHORT.name(), amount, usdtExRate, idoSwapUserRecord.getSwapTime());
        } else {
            BigDecimal amount = idoSwapUserRecord.getTokenInX();
            BigDecimal usdtExRate = StringUtils.equals(tokenX, CommonConstant.USDT_NAME) ? BigDecimal.ONE : swapPathService.getCoinPriceInfos().get(tokenX);
            addSwapTick(tokenX, DirectionType.LONG.name(), amount, usdtExRate, idoSwapUserRecord.getSwapTime());
        }
        // tokenY
        if (BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenInY()) && !BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenOutY())) {
            BigDecimal amount = idoSwapUserRecord.getTokenOutY();
            BigDecimal usdtExRate = StringUtils.equals(tokenY, CommonConstant.USDT_NAME) ? BigDecimal.ONE : swapPathService.getCoinPriceInfos().get(tokenY);
            addSwapTick(tokenY, DirectionType.SHORT.name(), amount, usdtExRate, idoSwapUserRecord.getSwapTime());
        } else {
            BigDecimal amount = idoSwapUserRecord.getTokenInY();
            BigDecimal usdtExRate = StringUtils.equals(tokenY, CommonConstant.USDT_NAME) ? BigDecimal.ONE : swapPathService.getCoinPriceInfos().get(tokenY);
            addSwapTick(tokenY, DirectionType.LONG.name(), amount, usdtExRate, idoSwapUserRecord.getSwapTime());
        }
    }

    private void addSwapTick(String token, String direction, BigDecimal amount, BigDecimal usdtExRate, long time) {
        SwapTickDto swapTickDto = new SwapTickDto();
        swapTickDto.setDirection(direction);
        swapTickDto.setAmount(amount);
        swapTickDto.setUsdtExRate(usdtExRate);
        swapTickDto.setUsdtAmount(amount.multiply(usdtExRate));
        redisCache.zAdd(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + token, swapTickDto, time);
    }

}
