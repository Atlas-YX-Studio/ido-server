package com.bixin.ido.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.beust.jcommander.internal.Lists;
import com.bixin.ido.server.bean.DO.LiquidityPool;
import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.vo.SwapPathInVO;
import com.bixin.ido.server.bean.vo.SwapPathOutVO;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.client.ChainClientHelper;
import com.bixin.ido.server.core.mapper.LiquidityPoolMapper;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.utils.GrfAllEdge;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SwapPathServiceImpl implements ISwapPathService {

    private static final int DEFAULT_SCALE = 18;

    private static final BigDecimal FEE_RATE = new BigDecimal("0.003");

    private static final BigDecimal REMAIN_RATE = BigDecimal.ONE.subtract(FEE_RATE);

    private static final String USDT_CODE = "0x5b876a58b0e1cff855b6489cd8cf3bec::DummyToken::USDT";

    private Map<String, Pool> liquidityPoolMap;

    private GrfAllEdge grf;

    private BigDecimal totalAssets;

    @Resource
    private LiquidityPoolImpl liquidityPool;

    @Resource
    private ChainClientHelper chainClientHelper;

    @Resource
    StarConfig idoStarConfig;

    @Resource
    ISwapCoinsService swapCoinsService;

    @PostConstruct
    public void init() {
//        this.fillTestData();
        this.refreshPools();
    }

    @Override
    public SwapPathInVO exchangeIn(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance) {
        List<List<String>> paths = getPaths(tokenA, tokenB);
        log.info("paths: {}", paths);
        // 选中的路径
        List<String> path = null;
        // 路径上各层可兑换到的数量
        List<BigDecimal> amounts = null;
        for (List<String> tempPath : paths) {
            List<BigDecimal> tempAmounts = getAmountsOut(tempPath, tokenAmount);
            // 当前路径兑换到的数量大于选中的路径兑换到的数量
            if(Objects.isNull(amounts) || amounts.get(amounts.size()-1).compareTo(tempAmounts.get(tempAmounts.size()-1)) < 0) {
                path = tempPath;
                amounts = tempAmounts;
            }
        }

        if (Objects.isNull(amounts)) {
            return null;
        }

        // 兑换数量
        BigDecimal exchangeAmount = amounts.get(amounts.size()-1);

        // 按照A计算成交均价
        BigDecimal avgPriceA = exchangeAmount.divide(tokenAmount, DEFAULT_SCALE, RoundingMode.DOWN);

        // 按照B计算成交均价
        BigDecimal avgPriceB = tokenAmount.divide(exchangeAmount, DEFAULT_SCALE, RoundingMode.DOWN);

        // 最小接收量
        BigDecimal minReceived = exchangeAmount.multiply(BigDecimal.ONE.subtract(slippageTolerance));

        // 价格影响
        BigDecimal priceImpact = getPriceImpact(path, amounts);

        // 手续费
        BigDecimal feeAmount = tokenAmount.subtract(tokenAmount.multiply(REMAIN_RATE.pow(path.size()-1)));
        // 路径

        log.info("兑换数量: {}, 按照A计算成交均价: {}, 按照B计算成交均价: {}, 最小接收量: {}, 价格影响: {}, 手续费: {}, 路径: {}", exchangeAmount, avgPriceA, avgPriceB, minReceived, priceImpact, feeAmount, path );
        return SwapPathInVO.convertToVO(exchangeAmount, avgPriceA, avgPriceB, minReceived, priceImpact, feeAmount, path);
    }

    @Override
    public SwapPathOutVO exchangeOut(String tokenA, String tokenB, BigDecimal tokenAmount, BigDecimal slippageTolerance) {
        List<List<String>> paths = getPaths(tokenA, tokenB);
        log.info("paths: {}", paths);
        // 选中的路径
        List<String> path = null;
        // 路径上各层需要的数量
        List<BigDecimal> amounts = null;
        for (List<String> tempPath : paths) {
            List<BigDecimal> tempAmounts = getAmountsIn(tempPath, tokenAmount);
            // 当前路径需要的数量小于选中的路径需要的数量
            if(Objects.isNull(amounts) || amounts.get(0).compareTo(tempAmounts.get(0)) > 0) {
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
        BigDecimal avgPriceA = tokenAmount.divide(payAmount, DEFAULT_SCALE, RoundingMode.DOWN);;

        // 按照B计算成交均价
        BigDecimal avgPriceB = payAmount.divide(tokenAmount, DEFAULT_SCALE, RoundingMode.DOWN);

        // 最大发送量
        BigDecimal maxSold = payAmount.divide(BigDecimal.ONE.subtract(slippageTolerance), DEFAULT_SCALE, RoundingMode.DOWN);

        // 价格影响
        BigDecimal priceImpact = getPriceImpact(path, amounts);

        // 手续费
        BigDecimal feeAmount = payAmount.subtract(payAmount.multiply(REMAIN_RATE.pow(path.size()-1)));

        // 路径

        log.info("支付数量: {}, 按照A计算成交均价: {}, 按照B计算成交均价: {}, 最大发送量: {}, 价格影响: {}, 手续费: {}, 路径: {}", payAmount, avgPriceA, avgPriceB, maxSold, priceImpact, feeAmount, path );
        return SwapPathOutVO.convertToVO(payAmount, avgPriceA, avgPriceB, maxSold, priceImpact, feeAmount, path);
    }

    @Override
    public BigDecimal totalAssets() {
        return this.totalAssets;
    }

    private List<List<String>> getPaths(String tokenA, String tokenB) {
        return grf.getAllPath(tokenA, tokenB);
    }

    private List<BigDecimal> getAmountsOut(List<String> path, BigDecimal tokenAmount) {
        if (path.size() < 2) {
            throw new RuntimeException("币种无法转换");
        }
        BigDecimal[] amounts = new BigDecimal[path.size()];
        amounts[0] = tokenAmount;
        for (int i = 0; i < path.size() - 1; i++) {
            Pool pool = getPool(path.get(i), path.get(i+1));
            if (path.get(i).equals(pool.tokenA)) {
                amounts[i+1] = getAmountOut(amounts[i], pool.tokenAmountA, pool.tokenAmountB);
            } else {
                amounts[i+1] = getAmountOut(amounts[i], pool.tokenAmountB, pool.tokenAmountA);
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
        amounts[path.size()-1] = tokenAmount;
        for (int i = path.size() - 1; i > 0; i--) {
            Pool pool = getPool(path.get(i-1), path.get(i));
            if (path.get(i-1).equals(pool.tokenA)) {
                amounts[i-1] = getAmountIn(amounts[i], pool.tokenAmountA, pool.tokenAmountB);
            } else {
                amounts[i-1] = getAmountIn(amounts[i], pool.tokenAmountB, pool.tokenAmountA);
            }
        }
        return Arrays.asList(amounts);
    }

    // a = XY/Y-b - X = bX / (Y-b)
    private BigDecimal getAmountIn(BigDecimal tokenAmount, BigDecimal tokenAmountA, BigDecimal tokenAmountB) {
        if (tokenAmount.compareTo(BigDecimal.ZERO) <= 0 || tokenAmountA.compareTo(BigDecimal.ZERO) <= 0 || tokenAmountB.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("兑换参数异常");
        }
        return tokenAmount.multiply(tokenAmountA).divide(tokenAmountB.subtract(tokenAmount).multiply(REMAIN_RATE), DEFAULT_SCALE, RoundingMode.DOWN);
    }

    private BigDecimal getPriceImpact(List<String> path, List<BigDecimal> amounts) {
        BigDecimal priceA = BigDecimal.ONE;
        BigDecimal finalPriceA = BigDecimal.ONE;
        for (int i = 0; i < path.size() - 1; i++) {
            Pool pool = getPool(path.get(i), path.get(i+1));
            if (path.get(i).equals(pool.tokenA)) {
                priceA = priceA.multiply(pool.tokenAmountB.divide(pool.tokenAmountA, DEFAULT_SCALE, RoundingMode.DOWN));
                finalPriceA = finalPriceA.multiply(pool.tokenAmountB.subtract(amounts.get(i+1)).divide(pool.tokenAmountA.add(amounts.get(i)), DEFAULT_SCALE, RoundingMode.DOWN));
            } else {
                priceA = priceA.multiply(pool.tokenAmountA.divide(pool.tokenAmountB, DEFAULT_SCALE, RoundingMode.DOWN));
                finalPriceA = priceA.multiply(pool.tokenAmountA.subtract(amounts.get(i+1)).divide(pool.tokenAmountB.add(amounts.get(i)), DEFAULT_SCALE, RoundingMode.DOWN));
            }
        }
        return priceA.subtract(finalPriceA).divide(priceA, DEFAULT_SCALE, RoundingMode.DOWN);
    }

    private Pool getPool(String tokenA, String tokenB) {
        return liquidityPoolMap.containsKey(toPair(tokenA, tokenB)) ? liquidityPoolMap.get(toPair(tokenA, tokenB)) : liquidityPoolMap.get(toPair(tokenB, tokenA));
    }

    private String toPair(String tokenA, String tokenB){
            return tokenA + "_" + tokenB;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void refreshPools() {
        // 获取区块高度

        // 有新块产生，更新数据
        List<Pool> allChainPools = getAllChainPools();
        liquidityPoolMap = allChainPools.stream().filter(x -> x.tokenAmountA.compareTo(BigDecimal.ZERO) > 0 || x.tokenAmountB.compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toMap(x -> toPair(x.tokenA, x.tokenB), y -> y));
        Set<String> nodes = new HashSet<>();
        liquidityPoolMap.values().forEach(x -> {
            nodes.add(x.tokenA);
            nodes.add(x.tokenB);
        });
        if (Objects.nonNull(this.grf) && this.grf.getNodes().containsAll(nodes)) {
            return;
        }
        GrfAllEdge tempGrf = new GrfAllEdge(nodes.size(), new ArrayList<>(nodes));
        liquidityPoolMap.values().forEach(x -> tempGrf.addPath(x.tokenA, x.tokenB));
        this.grf = tempGrf;
    }

    private List<Pool> getAllChainPools() {
        List<SwapCoins> swapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());
        Map<String, Short> coinMap = swapCoins.stream().collect(Collectors.toMap(SwapCoins::getAddress, SwapCoins::getExchangePrecision));
        List<Pool> pools = Lists.newArrayList();
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
                    return pools;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> result = (Map<String, Object>) respMap.get("result");
                if (!result.containsKey("resources")) {
                    log.error("getChainPool result value is empty {}, {}, {}",
                            JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                    return pools;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> resourceMap = (Map<String, Object>) result.get("resources");
                resourceMap.forEach((key, rs) -> {
                    if (!key.startsWith(idoStarConfig.getSwap().getLpPoolResourceName())) {
                        return;
                    }
                    String[] tokenArr = key.substring(key.indexOf("<")+1, key.length()-1).split(",");
                    Pool pool = new Pool(tokenArr[0].trim(), tokenArr[1].trim(), BigDecimal.ZERO, BigDecimal.ZERO);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> rsMap = (Map<String, Object>) ((Map<String, Object>) rs).get("json");

                    rsMap.forEach((x,y) -> {
                        if ("reserve_x".equalsIgnoreCase(x)) {
                            pool.tokenAmountA = new BigDecimal(y.toString()).movePointLeft(coinMap.get(pool.tokenA));
                        } else if ("reserve_y".equalsIgnoreCase(x)) {
                            pool.tokenAmountB = new BigDecimal(y.toString()).movePointLeft(coinMap.get(pool.tokenB));
                        }
                    });
                    pools.add(pool);
                });
            } else {
                log.error("getChainPool get remote result {}", JSON.toJSONString(resp));
            }
        } catch (Exception e) {
            log.error("getChainPool get remote chain exception", e);
        }

        return pools;
    }

    private Pool getChainPool(String tokenA, String tokenB) {
        Pool resPool = new Pool(tokenA, tokenB, BigDecimal.ZERO, BigDecimal.ZERO);
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

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void allAssets() {
        Map<String, BigDecimal> tempPrice = new HashMap<>();
        liquidityPoolMap.forEach((x, y)-> {
            if (Objects.equals(y.tokenA, USDT_CODE)) {
                tempPrice.put(toPair(y.tokenB, y.tokenA), y.tokenAmountA.divide(y.tokenAmountB, DEFAULT_SCALE, RoundingMode.DOWN));
            } else if (Objects.equals(y.tokenB, USDT_CODE)) {
                tempPrice.put(toPair(y.tokenA, y.tokenB), y.tokenAmountB.divide(y.tokenAmountA, DEFAULT_SCALE, RoundingMode.DOWN));
            }
        });
        totalAssets = liquidityPoolMap.values().stream().map(x->{
            if (Objects.equals(x.tokenA, USDT_CODE)) {
                return x.tokenAmountA.multiply(new BigDecimal(2));
            } else if (Objects.equals(x.tokenB, USDT_CODE)) {
                return x.tokenAmountB.multiply(new BigDecimal(2));
            } else {
                return x.tokenAmountA.multiply(tempPrice.getOrDefault(toPair(x.tokenA, USDT_CODE), BigDecimal.ZERO)).add(x.tokenAmountB.multiply(tempPrice.getOrDefault(toPair(x.tokenB, USDT_CODE), BigDecimal.ZERO)));
            }
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public static class Pool {
        public String tokenA;
        public String tokenB;
        public BigDecimal tokenAmountA;
        public BigDecimal tokenAmountB;

        public Pool(String tokenA, String tokenB, BigDecimal tokenAmountA, BigDecimal tokenAmountB) {
            this.tokenA = tokenA;
            this.tokenB = tokenB;
            this.tokenAmountA = tokenAmountA;
            this.tokenAmountB = tokenAmountB;
        }
    }

    private void fillTestData() {
        liquidityPoolMap = new HashMap<>();
        Pool pool1 = new Pool("0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::BTC", "0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::USDT", new BigDecimal(100), new BigDecimal(1000000));
        Pool pool2 = new Pool("0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::ETH", "0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::USDT", new BigDecimal(100), new BigDecimal(200000));
        Pool pool3 = new Pool("0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::EOS", "0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::USDT", new BigDecimal(10000), new BigDecimal(100000));
        Pool pool4 = new Pool("0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::STC", "0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::USDT", new BigDecimal(100000000), new BigDecimal(20000000));
        Pool pool5 = new Pool("0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::BTC", "0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::ETH", new BigDecimal(100), new BigDecimal(500));
        Pool pool6 = new Pool("0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::BTC", "0xc9bcf78045e7264c2b9d0b62a09e566a::DummyToken::STC", new BigDecimal(2000), new BigDecimal(100000000));
        liquidityPoolMap.put(toPair(pool1.tokenA, pool1.tokenB), pool1);
        liquidityPoolMap.put(toPair(pool2.tokenA, pool2.tokenB), pool2);
        liquidityPoolMap.put(toPair(pool3.tokenA, pool3.tokenB), pool3);
        liquidityPoolMap.put(toPair(pool4.tokenA, pool4.tokenB), pool4);
        liquidityPoolMap.put(toPair(pool5.tokenA, pool5.tokenB), pool5);
        liquidityPoolMap.put(toPair(pool6.tokenA, pool6.tokenB), pool6);

        Set<String> nodes = new HashSet<>();
        liquidityPoolMap.values().forEach(x -> {
            nodes.add(x.tokenA);
            nodes.add(x.tokenB);
        });
        grf = new GrfAllEdge(nodes.size(), new ArrayList<>(nodes));
        liquidityPoolMap.values().forEach(x -> grf.addPath(x.tokenA, x.tokenB));
        this.allAssets();
    }



    public static void main(String[] args) {
        SwapPathServiceImpl swapPathService = new SwapPathServiceImpl();
        swapPathService.init();

//        swapPathService.exchangeIn("STC", "USDT", new BigDecimal("1"), new BigDecimal("0.01"));
//        swapPathService.exchangeOut("USDT", "STC", new BigDecimal("1000"), new BigDecimal("0.01"));
//        swapPathService.exchangeOut("BTC", "STC", new BigDecimal("1000"), new BigDecimal("0.01"));

    }

}