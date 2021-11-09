package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.SwapCoins;
import com.bixin.ido.server.bean.DO.TradingPoolDo;
import com.bixin.ido.server.bean.DO.TradingPoolUserDo;
import com.bixin.ido.server.bean.dto.TradingPoolDto;
import com.bixin.ido.server.bean.vo.TradingPoolVo;
import com.bixin.ido.server.core.mapper.TradingPoolMapper;
import com.bixin.ido.server.core.mapper.TradingPoolUserMapper;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ITradingMiningService;
import com.bixin.ido.server.utils.BeanCopyUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradingMiningServiceImpl implements ITradingMiningService {

    @Resource
    private TradingPoolUserMapper tradingPoolUserMapper;

    @Resource
    private TradingPoolMapper tradingPoolMapper;

    @Resource
    private ISwapCoinsService swapCoinsService;

    /**
     * 累计交易额（平台）
     */
    public void totalTradingAmount() {
        // select sum(current_trading_amount), sum(total_trading_amount) from trading_pool_users;
    }

    /**
     * 当前交易额（平台）
     */
    public void currentTradingAmount() {
        // select sum(current_trading_amount), sum(total_trading_amount) from trading_pool_users;
    }

    /**
     * 计算当前收益（个人）
     */
    @Override
    public void currentReward(Long blockId) {
        // 更新个人收益
        // update trading_pool_users a INNER JOIN (select sum(current_trading_amount) total FROM trading_pool_users) b  set a.current_reward = a.current_reward + 100 * a.current_trading_amount/b.total, a.total_reward = a.total_reward + 100 * a.current_trading_amount/b.total;
        // 更新交易对已发放收益
        // update trading_pools a left join (select pool_id, sum(total_reward) total_reward from trading_pool_users GROUP BY pool_id) b on a.id = b.pool_id set a.allocated_reward_amount = b.total_reward;
    }

    /**
     * 衰减
     */
    @Scheduled(cron = "0 0 0/4 * * ?")
    public void attenuation() {
        // update trading_pool_users set current_trading_amount = current_trading_amount * 0.8;
        // update trading_pool_users set current_trading_amount = current_trading_amount * 0.8, block_id = 101 where block_id=100;
    }

    /**
     * 交易矿池列表
     */
    @Override
    public List<TradingPoolVo> poolList(String address) {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Integer total = tradingPools.stream().map(TradingPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, TradingPoolDto> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> TradingPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;

        List<SwapCoins> swapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());
        Map<String, SwapCoins> coinMap = swapCoins.stream().collect(Collectors.toMap(SwapCoins::getAddress, y -> y));
        List<TradingPoolUserDo> userTradingPools = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(TradingPoolUserDo.builder().address(address).build());
        Map<Long, TradingPoolUserDo> userTradingPoolMap = userTradingPools.stream().collect(Collectors.toMap(TradingPoolUserDo::getPoolId, y -> y));
        List<TradingPoolVo> tradingPoolList = tradingPollMap.values().stream().map(tradingPool -> BeanCopyUtil.copyProperties(tradingPool, () -> {
            TradingPoolVo vo = new TradingPoolVo();
            BigDecimal currentPoolDayReward = dayTotalReward.multiply(tradingPool.getAllocationRatio());
            BigDecimal apy = BigDecimal.valueOf(999999L);
            if (tradingPool.getCurrentTradingAmount().compareTo(BigDecimal.ZERO) > 0) {
                apy = currentPoolDayReward.multiply(BigDecimal.valueOf(0.003))
                        .divide(tradingPool.getCurrentTradingAmount(), 18, RoundingMode.DOWN)
                        .subtract(BigDecimal.ONE).multiply(BigDecimal.valueOf(365L));
            }
            vo.setApy(apy);
            if (userTradingPoolMap.containsKey(tradingPool.getId())) {
                TradingPoolUserDo tempUserTradingPool = userTradingPoolMap.get(tradingPool.getId());
                vo.setTradingAmount(tempUserTradingPool.getCurrentTradingAmount());
                vo.setCurrentReward(tempUserTradingPool.getCurrentReward());
                vo.setTotalReward(tempUserTradingPool.getTotalReward());
            }
            if (coinMap.containsKey(tradingPool.getTokenA())) {
                vo.setTokenIconA(coinMap.get(tradingPool.getTokenA()).getIcon());
            }
            if (coinMap.containsKey(tradingPool.getTokenB())) {
                vo.setTokenIconB(coinMap.get(tradingPool.getTokenB()).getIcon());
            }
            return vo;
        })).collect(Collectors.toList());
        return tradingPoolList;
    }

    /**
     * 每日预估收益
     */
    public BigDecimal dayReward(String address) {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Integer total = tradingPools.stream().map(TradingPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, TradingPoolDto> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> TradingPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;

        TradingPoolUserDo param = TradingPoolUserDo.builder().address(address).build();
        List<TradingPoolUserDo> userPools = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(param);

        BigDecimal dayReward = userPools.stream().map(x -> {
            TradingPoolDto tradingPool = tradingPollMap.get(x.getPoolId());
            BigDecimal currentPoolDayReward = dayTotalReward.multiply(tradingPool.getAllocationRatio());
            return x.getCurrentReward().multiply(currentPoolDayReward).divide(tradingPool.getCurrentTradingAmount(), 18, RoundingMode.DOWN);
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        return dayReward;
    }

}
