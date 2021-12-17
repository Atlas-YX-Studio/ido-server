package com.bixin.ido.service;

import com.bixin.ido.bean.DO.MiningHarvestRecordDo;
import com.bixin.ido.bean.DO.TradingPoolDo;
import com.bixin.ido.bean.DO.TradingPoolUserDo;
import com.bixin.ido.bean.DO.TradingRewardUserDo;
import com.bixin.ido.bean.vo.TradingMingRewardVO;
import com.bixin.ido.bean.vo.TradingMiningOverviewVO;
import com.bixin.ido.bean.vo.TradingPoolVo;

import java.math.BigDecimal;
import java.util.List;

public interface ITradingMiningService {

    void attenuation();

    int addTradingAmount(String userAddress, Long poolId, BigDecimal tradingAmount);

    TradingPoolDo getTradingPoolByTokenCode(String tokenA, String tokenB);

    String harvestCurrentReward(String userAddress);

    void harvestCurrentRewardSuccess(List<TradingPoolUserDo> tradingPoolUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    void harvestCurrentRewardFailed(List<TradingPoolUserDo> tradingPoolUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    String harvestFreedReward(String userAddress);

    void harvestFreedRewardSuccess(List<TradingRewardUserDo> tradingRewardUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    void harvestFreedRewardFailed(List<TradingRewardUserDo> tradingRewardUserDos, MiningHarvestRecordDo miningHarvestRecordDo);

    void computeReward(Long blockId);

    void unlockReward();

    TradingMingRewardVO reward(String address);

    List<TradingPoolVo> poolList(String address);

    TradingMiningOverviewVO market(String address);
}
