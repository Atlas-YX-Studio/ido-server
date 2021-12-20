package com.bixin.ido.service.provider;

import com.bixin.ido.bean.DO.SwapUserRecord;
import com.bixin.ido.bean.DO.TradingPoolDo;
import com.bixin.ido.bean.dto.SwapSymbolTickDto;
import com.bixin.ido.bean.dto.SwapTokenTickDto;
import com.bixin.ido.common.enums.DirectionType;
import com.bixin.common.constants.CommonConstant;
import com.bixin.core.redis.RedisCache;
import com.bixin.core.provider.IStarSwapProvider;
import com.bixin.ido.service.ISwapPathService;
import com.bixin.ido.service.ISwapUserRecordService;
import com.bixin.ido.service.ITradingMiningService;
import com.bixin.common.utils.BigDecimalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author zhangcheng
 * create  2021-08-25 3:04 下午
 */
@Slf4j
@Component
public class SwapUserRecordProviderImpl implements IStarSwapProvider<SwapUserRecord> {

    @Resource
    private ISwapUserRecordService swapUserRecordService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private ISwapPathService swapPathService;
    @Resource
    private ITradingMiningService tradingMiningService;

    @Value("${ido.star.swap.usdt-address}")
    private String usdtAddress;

    @Override
    public void dispatcher(SwapUserRecord idoSwapUserRecord) {
        swapUserRecordService.insert(idoSwapUserRecord);
        // 保存tick
        String tokenX = idoSwapUserRecord.getTokenCodeX();
        String tokenY = idoSwapUserRecord.getTokenCodeY();
        if (BigDecimal.ZERO.equals(idoSwapUserRecord.getTokenInX())) {
            // x <- y
            cacheTokenTick(tokenX, DirectionType.LONG.name(), idoSwapUserRecord.getTokenOutX(), idoSwapUserRecord.getSwapTime());
            cacheTokenTick(tokenY, DirectionType.SHORT.name(), idoSwapUserRecord.getTokenInY(), idoSwapUserRecord.getSwapTime());
            cacheSymbolTick(tokenX, tokenY, DirectionType.LONG.name(), idoSwapUserRecord.getTokenOutX(), idoSwapUserRecord.getTokenInY(), idoSwapUserRecord.getSwapTime());
            addTradingAmount(idoSwapUserRecord.getUserAddress(), tokenX, tokenY, idoSwapUserRecord.getTokenOutX(), idoSwapUserRecord.getTokenInY());
        } else {
            // x -> y
            cacheTokenTick(tokenX, DirectionType.SHORT.name(), idoSwapUserRecord.getTokenInX(), idoSwapUserRecord.getSwapTime());
            cacheTokenTick(tokenY, DirectionType.LONG.name(), idoSwapUserRecord.getTokenOutY(), idoSwapUserRecord.getSwapTime());
            cacheSymbolTick(tokenX, tokenY, DirectionType.SHORT.name(), idoSwapUserRecord.getTokenInX(), idoSwapUserRecord.getTokenOutY(), idoSwapUserRecord.getSwapTime());
            addTradingAmount(idoSwapUserRecord.getUserAddress(), tokenX, tokenY, idoSwapUserRecord.getTokenInX(), idoSwapUserRecord.getTokenOutY());
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
        BigDecimal usdtExRate0 = swapPathService.getCoinPriceInfos().getOrDefault(token0 + "_" + usdtAddress, BigDecimal.ZERO);
        BigDecimal usdtExRate1 = swapPathService.getCoinPriceInfos().getOrDefault(token1 + "_" + usdtAddress, BigDecimal.ZERO);

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
        redisCache.zAdd(CommonConstant.SWAP_SYMBOL_TICKS_PREFIX_KEY + token0 + "_" + token1, swapSymbolTickDto, time);
    }

    /**
     * 缓存token交易数据
     * @param token
     * @param direction
     * @param amount
     * @param time
     */
    private void cacheTokenTick(String token, String direction, BigDecimal amount, long time) {
        BigDecimal usdtExRate = swapPathService.getCoinPriceInfos().getOrDefault(token + "_" + usdtAddress, BigDecimal.ZERO);

        SwapTokenTickDto swapTokenTickDto = new SwapTokenTickDto();
        swapTokenTickDto.setDirection(direction);
        swapTokenTickDto.setAmount(amount);
        swapTokenTickDto.setUsdtExRate(usdtExRate);
        swapTokenTickDto.setUsdtAmount(amount.multiply(usdtExRate));
        swapTokenTickDto.setSwapTime(time);
        redisCache.zAdd(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + token, swapTokenTickDto, time);
    }

    /**
     * 累加交易额
     * @param userAddress
     * @param tokenX
     * @param tokenY
     * @param amountX
     * @param amountY
     */
    private void addTradingAmount(String userAddress, String tokenX, String tokenY, BigDecimal amountX, BigDecimal amountY) {
        TradingPoolDo tradingPoolDo = tradingMiningService.getTradingPoolByTokenCode(tokenX, tokenY);
        if (tradingPoolDo == null) {
            log.info("TradingPool not exists, tokenX:{}, tokenY:{}", tokenX, tokenY);
            return;
        }
        BigDecimal tradingAmount;
        if (StringUtils.equalsIgnoreCase(tokenX, usdtAddress)) {
            tradingAmount = BigDecimalUtil.removePrecision(amountX, swapPathService.getCoinPrecision(tokenX));
        } else if (StringUtils.equalsIgnoreCase(tokenY, usdtAddress)) {
            tradingAmount = BigDecimalUtil.removePrecision(amountY, swapPathService.getCoinPrecision(tokenY));
        } else {
            BigDecimal usdtExRate = swapPathService.getCoinPriceInfos().getOrDefault(tokenX + "_" + usdtAddress, BigDecimal.ZERO);
            tradingAmount = BigDecimalUtil.removePrecision(amountX.subtract(usdtExRate), swapPathService.getCoinPrecision(tokenY));
        }
        tradingMiningService.addTradingAmount(userAddress, tradingPoolDo.getId(), tradingAmount);
    }

}
