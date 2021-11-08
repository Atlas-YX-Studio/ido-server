package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.TradingPoolDo;
import com.bixin.ido.server.bean.DO.TradingPoolUserDo;
import com.bixin.ido.server.core.mapper.TradingPoolMapper;
import com.bixin.ido.server.core.mapper.TradingPoolUserMapper;
import com.bixin.ido.server.service.ITradingMiningService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TradingMiningServiceImpl implements ITradingMiningService {

    @Resource
    private TradingPoolUserMapper tradingPoolUserMapper;

    @Resource
    private TradingPoolMapper tradingPoolMapper;

    /**
     * 累计交易额（平台）
     */
    public void totalTradingAmount() {

    }

    /**
     * 当前交易额（平台）
     */
    public void currentTradingAmount() {

    }

    /**
     * 当前收益（个人）
     */
    public void currentReward() {
        // update trading_pool_users a INNER JOIN (select sum(current_trading_amount) total FROM trading_pool_users) b  set a.current_reward = a.current_reward + 100 * a.current_trading_amount/b.total, a.current_reward = a.total_reward + 100 * a.current_trading_amount/b.total;
    }


    /**
     * 衰减
     */
    public void attenuation() {
        // update trading_pool_users set current_trading_amount = current_trading_amount * 0.8;
        // update trading_pool_users set current_trading_amount = current_trading_amount * 0.8, block_id = 101 where block_id=100;
    }

    /**
     * apy
     */
    public void apy() {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Map<Long, TradingPoolDo> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> y));
        BigDecimal dayTotalReward = BigDecimal.TEN;
        List<BigDecimal> apyList = tradingPools.stream().map(tradingPool -> {
            BigDecimal currentPoolDayReward = dayTotalReward.multiply(tradingPool.getAllocationRatio());
            BigDecimal apy = currentPoolDayReward.multiply(BigDecimal.valueOf(0.003))
                    .divide(tradingPool.getCurrentTradingAmount(), 18, RoundingMode.DOWN)
                    .subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(365L));
            return apy;
        }).collect(Collectors.toList());


    }

    /**
     * 每日预估收益
     */
    public BigDecimal dayReward(String address) {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Map<Long, TradingPoolDo> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> y));
        BigDecimal dayTotalReward = BigDecimal.TEN;

        TradingPoolUserDo param = TradingPoolUserDo.builder().address(address).build();
        List<TradingPoolUserDo> userPools = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(param);

        BigDecimal dayReward = userPools.stream().map(x -> {
            TradingPoolDo tradingPool = tradingPollMap.get(x.getPoolId());
            BigDecimal currentPoolDayReward = dayTotalReward.multiply(tradingPool.getAllocationRatio());
            return x.getCurrentReward().multiply(currentPoolDayReward).divide(tradingPool.getCurrentTradingAmount(), 18, RoundingMode.DOWN);
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        return dayReward;
    }

}
