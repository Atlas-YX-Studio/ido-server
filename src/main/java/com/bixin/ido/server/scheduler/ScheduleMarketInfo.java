package com.bixin.ido.server.scheduler;

import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.bean.dto.SwapSymbolMarketDto;
import com.bixin.ido.server.bean.dto.SwapSymbolTickDto;
import com.bixin.ido.server.bean.dto.SwapTokenMarketDto;
import com.bixin.ido.server.bean.dto.SwapTokenTickDto;
import com.bixin.ido.server.bean.vo.VolumeInfoVO;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ILiquidityUserRecordService;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.ISwapUserRecordService;
import com.bixin.ido.server.service.impl.SwapPathServiceImpl;
import com.bixin.ido.server.utils.BigDecimalUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.core.service.NftEventService;
import com.bixin.nft.enums.NftEventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ScheduleMarketInfo {

    private static final long EXPIRE_MINUTES = 5;
    private static final long PROCESSING_EXPIRE_TIME = 30 * 1000L;
    private static final long LOCK_EXPIRE_TIME = 0L;
    private static final String UPDATE_SWAP_TOKEN_MARKET_LOCK = "update_swap_token_market_lock";
    private static final String UPDATE_SWAP_SYMBOL_MARKET_LOCK = "update_swap_symbol_market_lock";
    private static final String UPDATE_VOLUME_INFO_LOCK = "update_volume_info_lock";

    @Resource
    private ISwapCoinsService swapCoinsService;
    @Resource
    private ISwapPathService swapPathService;
    @Resource
    private ISwapUserRecordService swapUserRecordService;
    @Resource
    private ILiquidityUserRecordService liquidityUserRecordService;
    @Resource
    private NftEventService nftEventService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private StarConfig starConfig;

    @Value("${ido.star.swap.usdt-address}")
    private String usdtAddress;

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateSwapTokenMarket() {
        redisCache.tryGetDistributedLock(
                UPDATE_SWAP_TOKEN_MARKET_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(new SwapCoins());
                    if (CollectionUtils.isEmpty(swapCoinsList)) {
                        return;
                    }
                    swapCoinsList.forEach(swapCoins -> {
                        String tokenAddress = swapCoins.getAddress();
                        // 淘汰过期数据
                        Date yesterday = DateUtils.addDays(new Date(), -1);
                        redisCache.zRemoveRangeByScore(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + tokenAddress, 0, yesterday.getTime());
                        // 获取交易数据
                        List<SwapTokenTickDto> swapTicks = redisCache.zRange(CommonConstant.SWAP_TOKEN_TICKS_PREFIX_KEY + tokenAddress, 0, -1, SwapTokenTickDto.class);
                        if (CollectionUtils.isEmpty(swapTicks)) {
                            return;
                        }
                        // 获取token 24H价格变化率，百分之x
                        BigDecimal firstPrice = swapTicks.get(0).getUsdtExRate();
                        BigDecimal lastPrice = swapTicks.get(swapTicks.size() - 1).getUsdtExRate();
                        BigDecimal priceRate = BigDecimal.ZERO.equals(firstPrice) ? BigDecimal.ZERO :
                                lastPrice.subtract(firstPrice).divide(firstPrice, 18, RoundingMode.HALF_UP).multiply(new BigDecimal(2));
                        // 获取token 24H总交易额，以USDT计价
                        BigDecimal usdtSwapAmount = swapTicks.stream().map(SwapTokenTickDto::getUsdtAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        usdtSwapAmount = BigDecimalUtil.removePrecision(usdtSwapAmount, swapPathService.getCoinPrecision(usdtAddress));

                        // 存入redis
                        SwapTokenMarketDto swapTokenMarketDto = new SwapTokenMarketDto();
                        swapTokenMarketDto.setPriceRate(priceRate);
                        swapTokenMarketDto.setSwapAmount(usdtSwapAmount);
                        log.info("updateSwapTokenMarket success: {}", swapTokenMarketDto);
                        redisCache.setValue(CommonConstant.SWAP_TOKEN_MARKET_PREFIX_KEY + tokenAddress, swapTokenMarketDto, EXPIRE_MINUTES, TimeUnit.MINUTES);
                    });
                });
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateSwapSymbolMarket() {
        redisCache.tryGetDistributedLock(
                UPDATE_SWAP_SYMBOL_MARKET_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    List<SwapPathServiceImpl.Pool> poolList = swapPathService.getPoolList();
                    if (CollectionUtils.isEmpty(poolList)) {
                        return;
                    }
                    poolList.forEach(pool -> {
                        // 淘汰过期数据
                        Date yesterday = DateUtils.addDays(new Date(), -1);
                        redisCache.zRemoveRangeByScore(CommonConstant.SWAP_SYMBOL_TICKS_PREFIX_KEY + pool.tokenA + "_" + pool.tokenB, 0, yesterday.getTime());
                        // 获取交易数据
                        List<SwapSymbolTickDto> swapTicks = redisCache.zRange(CommonConstant.SWAP_SYMBOL_TICKS_PREFIX_KEY + pool.tokenA + "_" + pool.tokenB, 0, -1, SwapSymbolTickDto.class);
                        if (CollectionUtils.isEmpty(swapTicks)) {
                            return;
                        }
                        // 24h 交易额
                        BigDecimal usdtSwapAmount0 = swapTicks.stream().map(SwapSymbolTickDto::getUsdtAmount0).reduce(BigDecimal.ZERO, BigDecimal::add);
                        usdtSwapAmount0 = BigDecimalUtil.removePrecision(usdtSwapAmount0, swapPathService.getCoinPrecision(usdtAddress));
                        BigDecimal usdtSwapAmount1 = swapTicks.stream().map(SwapSymbolTickDto::getUsdtAmount1).reduce(BigDecimal.ZERO, BigDecimal::add);
                        usdtSwapAmount1 = BigDecimalUtil.removePrecision(usdtSwapAmount1, swapPathService.getCoinPrecision(usdtAddress));
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
                        log.info("updateSwapSymbolMarket success: {}", swapSymbolMarketDto);
                        redisCache.setValue(CommonConstant.SWAP_SYMBOL_MARKET_PREFIX_KEY + pool.tokenA + "_" + pool.tokenB, swapSymbolMarketDto, EXPIRE_MINUTES, TimeUnit.MINUTES);
                    });
                });
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void updateVolumeInfo() {
        redisCache.tryGetDistributedLock(
                UPDATE_VOLUME_INFO_LOCK,
                UUID.randomUUID().toString(),
                PROCESSING_EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    Set<String> addressSet = new HashSet<>();
                    // swap user
                    List<String> swapUserAddress = swapUserRecordService.selectAllAddress();
                    if (!CollectionUtils.isEmpty(swapUserAddress)) {
                        swapUserAddress.stream().filter(StringUtils::isNotBlank).forEach(addressSet::add);
                    }
                    // liquidity user
                    List<String> liquidityUserAddress = liquidityUserRecordService.selectAllAddress();
                    if (!CollectionUtils.isEmpty(liquidityUserAddress)) {
                        liquidityUserAddress.stream().filter(StringUtils::isNotBlank).forEach(addressSet::add);
                    }
                    // nft user
                    List<String> nftEventAddress = nftEventService.selectAllAddress();
                    if (!CollectionUtils.isEmpty(nftEventAddress)) {
                        nftEventAddress.stream().filter(StringUtils::isNotBlank).forEach(addressSet::add);
                    }

                    // volume
                    // swap
                    Map<String, BigDecimal> coinPriceInfos = swapPathService.getCoinPriceInfos();
                    long pageSize = 1000;
                    long nextId = 0;
                    BigDecimal swapTotalVolume = BigDecimal.ZERO;
                    while (true) {
                        List<SwapUserRecord> swapUserRecords = swapUserRecordService.getALlByPage(null, pageSize, nextId);
                        if (CollectionUtils.isEmpty(swapUserRecords)) {
                            break;
                        }
                        swapTotalVolume = swapUserRecords.stream().map(swapUserRecord -> {
                            BigDecimal swapVolume = BigDecimal.ZERO;
                            BigDecimal priceX = coinPriceInfos.getOrDefault(swapUserRecord.getTokenCodeX() + "_" + usdtAddress, BigDecimal.ZERO);
                            BigDecimal priceY = coinPriceInfos.getOrDefault(swapUserRecord.getTokenCodeY() + "_" + usdtAddress, BigDecimal.ZERO);
                            if (!BigDecimal.ZERO.equals(swapUserRecord.getTokenInX())) {
                                swapVolume = swapVolume.add(swapUserRecord.getTokenInX().multiply(priceX));
                                swapVolume = swapVolume.add(swapUserRecord.getTokenOutY().multiply(priceY));
                            } else {
                                swapVolume = swapVolume.add(swapUserRecord.getTokenOutX().multiply(priceX));
                                swapVolume = swapVolume.add(swapUserRecord.getTokenInY().multiply(priceY));
                            }
                            return swapVolume;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).add(swapTotalVolume);
                        if (swapUserRecords.size() < pageSize) {
                            break;
                        }
                        nextId = swapUserRecords.get(swapUserRecords.size() - 1).getId();
                    }
                    swapTotalVolume = BigDecimalUtil.removePrecision(swapTotalVolume, swapPathService.getCoinPrecision(usdtAddress));
                    log.info("updateVolumeInfo swapTotalVolume: {}", swapTotalVolume);

                    // nft
                    nextId = 0;
                    BigDecimal nftTotalVolume = BigDecimal.ZERO;
                    while (true) {
                        List<NftEventDo> nftEventDos = nftEventService.getALlByPage(null,null, pageSize, nextId);
                        if (CollectionUtils.isEmpty(nftEventDos)) {
                            break;
                        }
                        nftTotalVolume = nftEventDos.stream().map(swapUserRecord -> {
                            BigDecimal nftVolume = BigDecimal.ZERO;
                            if (StringUtils.equalsAny(swapUserRecord.getType(), NftEventType.BOX_BUY_EVENT.getDesc(),
                                    NftEventType.NFT_BUY_EVENT.getDesc(), NftEventType.BOX_OFFERING_SELL_EVENT.getDesc(), NftEventType.NFT_BUY_BACK_SELL_EVENT.getDesc())) {
                                BigDecimal sellingPrice = swapUserRecord.getSellingPrice() == null ? BigDecimal.ZERO : swapUserRecord.getSellingPrice();
                                nftVolume = coinPriceInfos.getOrDefault(swapUserRecord.getPayToken() + "_" + usdtAddress, BigDecimal.ZERO).multiply(sellingPrice);
                            } else if (StringUtils.equalsAny(swapUserRecord.getType(), NftEventType.BOX_ACCEPT_BID_EVENT.getDesc(), NftEventType.NFT_ACCEPT_BID_EVENT.getDesc())) {
                                BigDecimal sellingPrice = swapUserRecord.getBidPrice() == null ? BigDecimal.ZERO : swapUserRecord.getBidPrice();
                                nftVolume = coinPriceInfos.getOrDefault(swapUserRecord.getPayToken() + "_" + usdtAddress, BigDecimal.ZERO).multiply(sellingPrice);
                            }
                            return nftVolume;
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).add(nftTotalVolume);
                        if (nftEventDos.size() < pageSize) {
                            break;
                        }
                        nextId = nftEventDos.get(nftEventDos.size() - 1).getId();
                    }
                    nftTotalVolume = BigDecimalUtil.removePrecision(nftTotalVolume, swapPathService.getCoinPrecision(usdtAddress));
                    log.info("updateVolumeInfo nftTotalVolume: {}", nftTotalVolume);

                    VolumeInfoVO volumeInfoVO = new VolumeInfoVO();
                    volumeInfoVO.setTvl(swapPathService.totalAssets());
                    volumeInfoVO.setVolume(swapTotalVolume.add(nftTotalVolume));
                    volumeInfoVO.setUserNumber(addressSet.size());
                    log.info("updateVolumeInfo success: {}", volumeInfoVO);
                    redisCache.setValue(CommonConstant.VOLUME_INFO_KEY, volumeInfoVO, EXPIRE_MINUTES, TimeUnit.MINUTES);
                });
    }

    /**
     * 更新stc价格
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateStcFeePrice() {
        Calendar instance = Calendar.getInstance();
        if (instance.get(Calendar.HOUR_OF_DAY) != 0) {
            BigDecimal price = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
            if (price != null && !price.equals(BigDecimal.ZERO)) {
                return;
            }
        }
        BigDecimal price = swapPathService.getCoinPriceInfos().getOrDefault(CommonConstant.STC_ADDRESS + "_" + starConfig.getMining().getKikoAddress(), BigDecimal.ZERO);
        redisCache.setValue(CommonConstant.STC_FEE_PRICE_KEY, price, 25, TimeUnit.HOURS);
    }

}
