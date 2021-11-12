package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.MiningHarvestRecordDo;
import com.bixin.ido.server.bean.DO.TradingPoolDo;
import com.bixin.ido.server.bean.DO.TradingPoolUserDo;
import com.bixin.ido.server.bean.DO.TradingRewardUserDo;
import com.bixin.ido.server.bean.vo.TradingMiningOverviewVO;
import com.bixin.ido.server.bean.vo.TradingPoolVo;

import java.math.BigDecimal;
import java.util.List;

public interface ITradingMiningService {

    int addTradingAmount(String userAddress, Long poolId, BigDecimal tradingAmount);

    TradingPoolDo getTradingPoolByTokenCode(String tokenA, String tokenB);

    String harvestCurrentReward(String userAddress);

    void harvestCurrentRewardSuccess(List<TradingPoolUserDo> tradingPoolUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    void harvestCurrentRewardFailed(List<TradingPoolUserDo> tradingPoolUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    String harvestFreedReward(String userAddress);

    void harvestFreedRewardSuccess(List<TradingRewardUserDo> tradingRewardUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    void harvestFreedRewardFailed(List<TradingRewardUserDo> tradingRewardUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    void currentReward(Long blockId);

    List<TradingPoolVo> poolList(String address);

    TradingMiningOverviewVO market(String address);
}
