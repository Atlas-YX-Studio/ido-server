package com.bixin.ido.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.beust.jcommander.internal.Lists;
import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.dto.SwapTokenMarketDto;
import com.bixin.ido.server.bean.vo.CoinStatsInfoVO;
import com.bixin.ido.server.bean.vo.SwapMetaVO;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.client.ChainClientHelper;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.ISwapUserRecordService;
import com.bixin.ido.server.utils.GrfAllEdge;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SwapPathServiceImpl implements ISwapPathService {

    private static final int DEFAULT_SCALE = 18;
    private static final int DEFAULT_PRECISION = 8;

    private static final BigDecimal FEE_RATE = new BigDecimal("0.003");

    private static final BigDecimal REMAIN_RATE = BigDecimal.ONE.subtract(FEE_RATE);

    @Value("${ido.star.swap.usdt-address}")
    private String USDT_CODE;

    private Map<String, Pool> liquidityPoolMap;

    private Map<String, BigDecimal> priceMap;

    Map<String, Short> coinPrecisionMap;

    private GrfAllEdge grf;

    private BigDecimal totalAssets;

    @Resource
    private ChainClientHelper chainClientHelper;

    @Resource
    private StarConfig idoStarConfig;

    @Resource
    private ISwapCoinsService swapCoinsService;

    @Resource
    private ISwapUserRecordService swapUserRecordService;

    @Resource
    private RedisCache redisCache;

//    @PostConstruct
    public void init() {
//        this.fillTestData();
        this.refreshPools();
        this.allAssets();
    }

    @Override
    public SwapPathInVO exchangeIn(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode) {
        List<List<String>> paths = getPaths(tokenA, tokenB, multiMode);
        log.info("paths: {}", paths);
        // 选中的路径
        List<String> path = null;
        // 路径上各层可兑换到的数量
        List<BigDecimal> amounts = null;
        for (List<String> tempPath : paths) {
            List<BigDecimal> tempAmounts = getAmountsOut(tempPath, tokenAmount);
            // 当前路径兑换到的数量大于选中的路径兑换到的数量
            if (Objects.isNull(amounts) || amounts.get(amounts.size() - 1).compareTo(tempAmounts.get(tempAmounts.size() - 1)) < 0) {
                path = tempPath;
                amounts = tempAmounts;
            }
        }

        if (Objects.isNull(amounts)) {
            return null;
        }

        // 兑换数量
        BigDecimal exchangeAmount = amounts.get(amounts.size() - 1);

        // 按照A计算成交均价
        BigDecimal avgPriceA = exchangeAmount.divide(tokenAmount, DEFAULT_SCALE, RoundingMode.DOWN);

        // 按照B计算成交均价
        BigDecimal avgPriceB = tokenAmount.divide(exchangeAmount, DEFAULT_SCALE, RoundingMode.DOWN);

        // 最小接收量
        BigDecimal minReceived = exchangeAmount.multiply(BigDecimal.ONE.subtract(slippageTolerance));

        // 价格影响
        BigDecimal priceImpact = getPriceImpact(path, amounts);

        // 手续费
        BigDecimal feeAmount = tokenAmount.subtract(tokenAmount.multiply(REMAIN_RATE.pow(path.size() - 1)));
        // 路径

        log.info("兑换数量: {}, 按照A计算成交均价: {}, 按照B计算成交均价: {}, 最小接收量: {}, 价格影响: {}, 手续费: {}, 路径: {}", exchangeAmount, avgPriceA, avgPriceB, minReceived, priceImpact, feeAmount, path);
        return SwapPathInVO.convertToVO(exchangeAmount, avgPriceA, avgPriceB, minReceived, priceImpact, feeAmount, path);
    }

    @Override
    public SwapPathOutVO exchangeOut(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance, boolean multiMode) {
        List<List<String>> paths = getPaths(tokenA, tokenB, multiMode);
        log.info("paths: {}", paths);
        // 选中的路径
        List<String> path = null;
        // 路径上各层需要的数量
        List<BigDecimal> amounts = null;
        for (List<String> tempPath : paths) {
            List<BigDecimal> tempAmounts = getAmountsIn(tempPath, tokenAmount);
            // 当前路径需要的数量小于选中的路径需要的数量
            if (Objects.isNull(amounts) || amounts.get(0).compareTo(tempAmounts.get(0)) > 0) {
                if (tempAmounts.get(0).compareTo(BigDecimal.ZERO) < 0) {
                    continue;
                }
                path = tempPath;
                amounts = tempAmounts;
            }
        }

        if (Objects.isNull(amounts)) {
            return null;
        }

        // 支付数量
        BigDecimal payAmount = amounts.get(0);

        // 按照A计算成交均价
        BigDecimal avgPriceA = tokenAmount.divide(payAmount, DEFAULT_SCALE, RoundingMode.DOWN);
        ;

        // 按照B计算成交均价
        BigDecimal avgPriceB = payAmount.divide(tokenAmount, DEFAULT_SCALE, RoundingMode.DOWN);

        // 最大发送量
        BigDecimal maxSold = payAmount.divide(BigDecimal.ONE.subtract(slippageTolerance), DEFAULT_SCALE, RoundingMode.DOWN);

        // 价格影响
        BigDecimal priceImpact = getPriceImpact(path, amounts);

        // 手续费
        BigDecimal feeAmount = payAmount.subtract(payAmount.multiply(REMAIN_RATE.pow(path.size() - 1)));

        // 路径

        log.info("支付数量: {}, 按照A计算成交均价: {}, 按照B计算成交均价: {}, 最大发送量: {}, 价格影响: {}, 手续费: {}, 路径: {}", payAmount, avgPriceA, avgPriceB, maxSold, priceImpact, feeAmount, path);
        return SwapPathOutVO.convertToVO(payAmount, avgPriceA, avgPriceB, maxSold, priceImpact, feeAmount, path);
    }

    @Override
    public BigDecimal totalAssets() {
        return Objects.isNull(this.totalAssets) ? BigDecimal.ZERO : this.totalAssets;
    }

    private List<List<String>> getPaths(String tokenA, String tokenB, boolean multiMode) {
        if (multiMode) {
            return grf.getAllPath(tokenA, tokenB);
        } else  {
            List<List<String>> paths = Lists.newArrayList();
            if (Objects.nonNull(getPool(tokenA, tokenB))) {
                paths.add(Lists.newArrayList(tokenA, tokenB));
            }
            return paths;
        }

    }

    private List<BigDecimal> getAmountsOut(List<String> path, BigDecimal tokenAmount) {
        if (path.size() < 2) {
            throw new RuntimeException("币种无法转换");
        }
        BigDecimal[] amounts = new BigDecimal[path.size()];
        amounts[0] = tokenAmount;
        for (int i = 0; i < path.size() - 1; i++) {
            Pool pool = getPool(path.get(i), path.get(i + 1));
            if (path.get(i).equals(pool.tokenA)) {
                amounts[i + 1] = getAmountOut(amounts[i], pool.tokenAmountA, pool.tokenAmountB);
            } else {
                amounts[i + 1] = getAmountOut(amounts[i], pool.tokenAmountB, pool.tokenAmountA);
            }
        }
        return Arrays.asList(amounts);
    }

    // b = Y - XY/(X+a) = aY / (X+a)
    private BigDecimal getAmountOut(BigDecimal tokenAmount, BigDecimal tokenAmountA, BigDecimal tokenAmountB) {
        if (tokenAmount.compareTo(BigDecimal.ZERO) <= 0 || tokenAmountA.compareTo(BigDecimal.ZERO) <= 0 || tokenAmountB.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("兑换参数异常");
        }
        BigDecimal tokenAmountWithFee = tokenAmount.multiply(REMAIN_RATE);
        return tokenAmountWithFee.multiply(tokenAmountB).divide(tokenAmountA.add(tokenAmountWithFee), DEFAULT_SCALE, RoundingMode.DOWN);
    }

    private List<BigDecimal> getAmountsIn(List<String> path, BigDecimal tokenAmount) {
        if (path.size() < 2) {
            throw new RuntimeException("币种无法转换");
        }
        BigDecimal[] amounts = new BigDecimal[path.size()];
        amounts[path.size() - 1] = tokenAmount;
        for (int i = path.size() - 1; i > 0; i--) {
            Pool pool = getPool(path.get(i - 1), path.get(i));
            if (path.get(i - 1).equals(pool.tokenA)) {
                amounts[i - 1] = getAmountIn(amounts[i], pool.tokenAmountA, pool.tokenAmountB);
            } else {
                amounts[i - 1] = getAmountIn(amounts[i], pool.tokenAmountB, pool.tokenAmountA);
            }
        }
        return Arrays.asList(amounts);
    }

    // a = XY/Y-b - X = bX / (Y-b)
    private BigDecimal getAmountIn(BigDecimal tokenAmount, BigDecimal tokenAmountA, BigDecimal tokenAmountB) {
        if (tokenAmount.compareTo(BigDecimal.ZERO) <= 0 || tokenAmountA.compareTo(BigDecimal.ZERO) <= 0 || tokenAmountB.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE.negate();
        }
        return tokenAmount.multiply(tokenAmountA).divide(tokenAmountB.subtract(tokenAmount).multiply(REMAIN_RATE), DEFAULT_SCALE, RoundingMode.DOWN);
    }

    private BigDecimal getPriceImpact(List<String> path, List<BigDecimal> amounts) {
        BigDecimal priceA = BigDecimal.ONE;
        BigDecimal finalPriceA = BigDecimal.ONE;
        for (int i = 0; i < path.size() - 1; i++) {
            Pool pool = getPool(path.get(i), path.get(i + 1));
            if (path.get(i).equals(pool.tokenA)) {
                priceA = priceA.multiply(pool.tokenAmountB.divide(pool.tokenAmountA, DEFAULT_SCALE, RoundingMode.DOWN));
                finalPriceA = finalPriceA.multiply(pool.tokenAmountB.subtract(amounts.get(i + 1)).divide(pool.tokenAmountA.add(amounts.get(i)), DEFAULT_SCALE, RoundingMode.DOWN));
            } else {
                priceA = priceA.multiply(pool.tokenAmountA.divide(pool.tokenAmountB, DEFAULT_SCALE, RoundingMode.DOWN));
                finalPriceA = finalPriceA.multiply(pool.tokenAmountA.subtract(amounts.get(i + 1)).divide(pool.tokenAmountB.add(amounts.get(i)), DEFAULT_SCALE, RoundingMode.DOWN));
            }
        }
        return priceA.subtract(finalPriceA).divide(priceA, DEFAULT_SCALE, RoundingMode.DOWN);
    }

    @Override
    public List<Pool> getPoolList() {
        return List.of(liquidityPoolMap.values().toArray(new Pool[0]));
    }

    @Override
    public Map<String, Pool> getLiquidityPoolMap() {
        return this.liquidityPoolMap;
    }

    private Pool getPool(String tokenA, String tokenB) {
        return liquidityPoolMap.containsKey(toPair(tokenA, tokenB)) ? liquidityPoolMap.get(toPair(tokenA, tokenB)) : liquidityPoolMap.get(toPair(tokenB, tokenA));
    }

    private String toPair(String tokenA, String tokenB) {
        return tokenA + "_" + tokenB;
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void refreshPools() {
        // 获取区块高度

        // 有新块产生，更新数据
        Map<String, Pool> allChainPoolMap = getAllChainPools();
        liquidityPoolMap = allChainPoolMap.values().stream().filter(x -> x.tokenAmountA.compareTo(BigDecimal.ZERO) > 0 || x.tokenAmountB.compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toMap(x -> toPair(x.tokenA, x.tokenB), y -> y));
        Set<String> nodes = new HashSet<>();
        liquidityPoolMap.values().forEach(x -> {
            nodes.add(x.tokenA);
            nodes.add(x.tokenB);
        });
        GrfAllEdge tempGrf = new GrfAllEdge(nodes.size(), new ArrayList<>(nodes));
        liquidityPoolMap.values().forEach(x -> tempGrf.addPath(x.tokenA, x.tokenB));
        this.grf = tempGrf;

        // 同步更新价格信息
        Map<String, BigDecimal> tempPrice = new HashMap<>();
        tempPrice.put(toPair(USDT_CODE, USDT_CODE), BigDecimal.ONE);
        liquidityPoolMap.forEach((x, y) -> {
            if (Objects.equals(y.tokenA, USDT_CODE)) {
                tempPrice.put(toPair(y.tokenB, y.tokenA), y.tokenAmountA.divide(y.tokenAmountB, DEFAULT_SCALE, RoundingMode.DOWN));
            } else if (Objects.equals(y.tokenB, USDT_CODE)) {
                tempPrice.put(toPair(y.tokenA, y.tokenB), y.tokenAmountB.divide(y.tokenAmountA, DEFAULT_SCALE, RoundingMode.DOWN));
            }
        });
        this.priceMap = tempPrice;
    }

    private Map<String, Pool> getAllChainPools() {
        List<SwapCoins> swapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());
        Map<String, Short> coinMap = swapCoins.stream().collect(Collectors.toMap(SwapCoins::getAddress, SwapCoins::getExchangePrecision));
        Map<String, Pool> poolMap = Maps.newHashMap();
        try {
            MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple = chainClientHelper.getAllLPResp();
            ResponseEntity<String> resp = triple.getLeft();
            String url = triple.getMiddle();
            HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

            if (resp.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> respMap = JSON.parseObject(resp.getBody(), new TypeReference<>() {
                });
                if (!respMap.containsKey("result")) {
                    log.error("getChainPool result is empty {}, {}, {}",
                            JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                    return poolMap;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) respMap.get("result");
                if (!result.containsKey("resources")) {
                    log.error("getChainPool result value is empty {}, {}, {}",
                            JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                    return poolMap;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> resourceMap = (Map<String, Object>) result.get("resources");
                resourceMap.forEach((key, rs) -> {
                    if (key.startsWith(idoStarConfig.getSwap().getLpPoolResourceName())) {
                        String[] tokenArr = key.substring(key.indexOf("<") + 1, key.length() - 1).split(",");
                        String pairName = toPair(tokenArr[0].trim(), tokenArr[1].trim());
                        Pool pool = poolMap.getOrDefault(pairName, new Pool(tokenArr[0].trim(), tokenArr[1].trim()));
                        if (!coinMap.containsKey(pool.tokenA) || !coinMap.containsKey(pool.tokenB)) {
                            return;
                        }
                        @SuppressWarnings("unchecked")
                        Map<String, Object> rsMap = (Map<String, Object>) ((Map<String, Object>) rs).get("json");

                        rsMap.forEach((x, y) -> {
                            if ("reserve_x".equalsIgnoreCase(x)) {
                                pool.tokenAmountA = new BigDecimal(y.toString()).movePointLeft(coinMap.get(pool.tokenA));
                            } else if ("reserve_y".equalsIgnoreCase(x)) {
                                pool.tokenAmountB = new BigDecimal(y.toString()).movePointLeft(coinMap.get(pool.tokenB));
                            }
                        });
                        poolMap.put(pairName, pool);
                    } else if (key.startsWith("0x00000000000000000000000000000001::Token::TokenInfo<" + idoStarConfig.getSwap().getLpTokenResourceName())) {
                        String lpTokenStr = key.substring(key.indexOf("<") + 1, key.length() - 1);
                        String[] tokenArr = lpTokenStr.substring(lpTokenStr.indexOf("<") + 1, lpTokenStr.length() - 1).split(",");
                        String pairName = toPair(tokenArr[0].trim(), tokenArr[1].trim());
                        Pool pool = poolMap.getOrDefault(pairName, new Pool(tokenArr[0].trim(), tokenArr[1].trim()));
                        if (!coinMap.containsKey(pool.tokenA) || !coinMap.containsKey(pool.tokenB)) {
                            return;
                        }
                        @SuppressWarnings("unchecked")
                        Map<String, Object> rsMap = (Map<String, Object>) ((Map<String, Object>) rs).get("json");

                        rsMap.forEach((x, y) -> {
                            if ("total_value".equalsIgnoreCase(x)) {
                                // todo lptoken没有存在coin表里，先写死9位
                                pool.lpTokenAmount = new BigDecimal(y.toString()).movePointLeft(9);
                            }
                        });
                        poolMap.put(pairName, pool);
                    }
                });
            } else {
                log.error("getChainPool get remote result {}", JSON.toJSONString(resp));
            }
        } catch (Exception e) {
            log.error("getChainPool get remote chain exception", e);
        }
        if (!CollectionUtils.isEmpty(coinMap)) {
            coinPrecisionMap = coinMap;
        }

        return poolMap;
    }

    private Pool getChainPool(String tokenA, String tokenB) {
        Pool resPool = new Pool(tokenA, tokenB);
        try {
            MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple = chainClientHelper.getLPResp(tokenA, tokenB);
            ResponseEntity<String> resp = triple.getLeft();
            String url = triple.getMiddle();
            HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

            if (resp.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> respMap = JSON.parseObject(resp.getBody(), new TypeReference<>() {
                });
                if (!respMap.containsKey("result")) {
                    log.error("getChainPool result is empty {}, {}, {}",
                            JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                    return resPool;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) respMap.get("result");
                if (!result.containsKey("value")) {
                    log.error("getChainPool result value is empty {}, {}, {}",
                            JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                    return resPool;
                }
                @SuppressWarnings("unchecked")
                List<JSONArray> values = (List<JSONArray>) result.get("value");
                values.forEach(rs -> {
                    Object[] stcResult = rs.toArray();
                    if ("reserve_x".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> pledgeMap = (Map<String, Object>) stcResult[1];
                        String tokenAmount = (String) pledgeMap.get("U128");

                        resPool.tokenAmountA = new BigDecimal(tokenAmount);
                    } else if ("reserve_y".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> pledgeMap = (Map<String, Object>) stcResult[1];
                        String tokenAmount = (String) pledgeMap.get("U128");

                        resPool.tokenAmountB = new BigDecimal(tokenAmount);
                    }
                });
            } else {
                log.error("getChainPool get remote result {}", JSON.toJSONString(resp));
            }
        } catch (Exception e) {
            log.error("getChainPool get remote chain exception {}, {}", tokenA, tokenB, e);
        }

        return resPool;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void allAssets() {
        totalAssets = liquidityPoolMap.values().stream().map(x -> {
            if (Objects.equals(x.tokenA, USDT_CODE)) {
                return x.tokenAmountA.multiply(new BigDecimal(2));
            } else if (Objects.equals(x.tokenB, USDT_CODE)) {
                return x.tokenAmountB.multiply(new BigDecimal(2));
            } else {
                return x.tokenAmountA.multiply(this.priceMap.getOrDefault(toPair(x.tokenA, USDT_CODE), BigDecimal.ZERO)).add(x.tokenAmountB.multiply(this.priceMap.getOrDefault(toPair(x.tokenB, USDT_CODE), BigDecimal.ZERO)));
            }
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        log.info("update totalAssets:{}", totalAssets);
    }

    @Override
    public Map<String, BigDecimal> getCoinPriceInfos() {
        return this.priceMap;
    }

    @Override
    public List<CoinStatsInfoVO> coinInfos(int pageNum, int pageSize) {
        List<SwapCoins> swapCoins = swapCoinsService.getALlByPage(pageSize * (pageNum - 1), pageSize + 1);
        // 计算币种数量
        Map<String, BigDecimal> coinVolumeMap = new HashMap<>();
        liquidityPoolMap.forEach((x, y) -> {
            coinVolumeMap.compute(y.tokenA, (k, v) -> Objects.isNull(v) ? y.tokenAmountA : v.add(y.tokenAmountA));
            coinVolumeMap.compute(y.tokenB, (k, v) -> Objects.isNull(v) ? y.tokenAmountB : v.add(y.tokenAmountB));
        });
        List<CoinStatsInfoVO> coinInfoVos = swapCoins.stream().map(coin -> {
            SwapTokenMarketDto swapTokenMarketDto = redisCache.getValue(CommonConstant.SWAP_TOKEN_MARKET_PREFIX_KEY + coin.getAddress(), SwapTokenMarketDto.class);
            if (swapTokenMarketDto == null) {
                return CoinStatsInfoVO.builder()
                        .name(coin.getShortName())
                        .icon(coin.getIcon())
                        .price(this.priceMap.getOrDefault(toPair(coin.getAddress(), USDT_CODE), BigDecimal.ZERO).toPlainString())
                        .liquidity(coinVolumeMap.getOrDefault(coin.getAddress(), BigDecimal.ZERO).multiply(this.priceMap.getOrDefault(toPair(coin.getAddress(), USDT_CODE), BigDecimal.ZERO)).toPlainString())
                        .build();
            }
            return CoinStatsInfoVO.builder()
                    .name(coin.getShortName())
                    .icon(coin.getIcon())
                    .price(this.priceMap.getOrDefault(toPair(coin.getAddress(), USDT_CODE), BigDecimal.ZERO).toPlainString())
                    .rate(swapTokenMarketDto.getPriceRate().toPlainString())
                    .amount(swapTokenMarketDto.getSwapAmount().toPlainString())
                    .liquidity(coinVolumeMap.getOrDefault(coin.getAddress(), BigDecimal.ZERO).multiply(this.priceMap.getOrDefault(toPair(coin.getAddress(), USDT_CODE), BigDecimal.ZERO)).toPlainString())
                    .build();
        }).collect(Collectors.toList());
        return coinInfoVos;
    }

    @Override
    public Integer getCoinPrecision(String coinAddress) {
        if (CollectionUtils.isEmpty(coinPrecisionMap)) {
            return DEFAULT_PRECISION;
        }
        return coinPrecisionMap.containsKey(coinAddress) ? (int) coinPrecisionMap.get(coinAddress) : DEFAULT_PRECISION;
    }

    @Override
    public SwapMetaVO meta() {
        List<SwapCoins> swapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());
        Map<String, Pool> liquidityPoolMap = this.liquidityPoolMap;
        Long visits = swapUserRecordService.countVisits(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        return SwapMetaVO.convertToMeta(swapCoins, liquidityPoolMap, visits);
    }

    public static class Pool {
        public String tokenA;
        public String tokenB;
        public BigDecimal tokenAmountA;
        public BigDecimal tokenAmountB;
        public BigDecimal lpTokenAmount;

        public Pool(String tokenA, String tokenB) {
            this.tokenA = tokenA;
            this.tokenB = tokenB;
            this.tokenAmountA = BigDecimal.ZERO;
            this.tokenAmountB = BigDecimal.ZERO;
            this.lpTokenAmount = BigDecimal.ZERO;
        }
    }

}
