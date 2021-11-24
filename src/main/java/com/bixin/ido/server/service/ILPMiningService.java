package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.LPMiningPoolUserDo;
import com.bixin.ido.server.bean.DO.MiningHarvestRecordDo;
import com.bixin.ido.server.bean.dto.LPStakeEventEventDto;
import com.bixin.ido.server.bean.dto.LPUnstakeEventEventDto;
import com.bixin.ido.server.bean.vo.LPMingPoolVo;
import com.bixin.ido.server.bean.vo.LPMingRewardVO;

import java.math.BigDecimal;
import java.util.List;

public interface ILPMiningService {

    void computeReward(Long blockId);

    void staking(LPStakeEventEventDto dto);

    void unStaking(LPUnstakeEventEventDto dto);

    List<LPMingPoolVo> poolList(String address);

    LPMingRewardVO reward(String address, Long poolId);

    String harvestReward(String address, Long poolId);

    void harvestRewardSuccess(LPMiningPoolUserDo lpMiningPoolUserDo, MiningHarvestRecordDo miningHarvestRecordDo);

    void harvestRewardFailed(LPMiningPoolUserDo lpMiningPoolUserDo, MiningHarvestRecordDo miningHarvestRecordDo);

    BigDecimal market();
}
