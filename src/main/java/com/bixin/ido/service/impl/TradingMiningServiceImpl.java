package com.bixin.ido.service.impl;

import com.bixin.common.code.IdoErrorCode;
import com.bixin.common.config.StarConfig;
import com.bixin.common.constants.CommonConstant;
import com.bixin.common.exception.IdoException;
import com.bixin.common.utils.ApplicationContextUtils;
import com.bixin.common.utils.BeanCopyUtil;
import com.bixin.common.utils.BigDecimalUtil;
import com.bixin.common.utils.ThreadPoolUtil;
import com.bixin.core.redis.RedisCache;
import com.bixin.ido.bean.DO.*;
import com.bixin.ido.bean.dto.TradingPoolDto;
import com.bixin.ido.bean.vo.TradingMingRewardVO;
import com.bixin.ido.bean.vo.TradingMiningOverviewVO;
import com.bixin.ido.bean.vo.TradingPoolVo;
import com.bixin.ido.common.enums.HarvestStatusEnum;
import com.bixin.ido.common.enums.MiningTypeEnum;
import com.bixin.ido.common.enums.RewardTypeEnum;
import com.bixin.ido.core.mapper.MiningHarvestRecordMapper;
import com.bixin.ido.core.mapper.TradingPoolMapper;
import com.bixin.ido.core.mapper.TradingPoolUserMapper;
import com.bixin.ido.core.mapper.TradingRewardUserMapper;
import com.bixin.ido.service.ISwapCoinsService;
import com.bixin.ido.service.ISwapPathService;
import com.bixin.ido.service.ITradingMiningService;
import com.bixin.nft.service.ContractService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TradingMiningServiceImpl implements ITradingMiningService {

    @Resource
    private TradingPoolUserMapper tradingPoolUserMapper;
    @Resource
    private TradingPoolMapper tradingPoolMapper;
    @Resource
    private TradingRewardUserMapper tradingRewardUserMapper;
    @Resource
    private MiningHarvestRecordMapper miningHarvestRecordMapper;
    @Resource
    private ContractService contractService;
    @Resource
    private ISwapCoinsService swapCoinsService;
    @Resource
    private ISwapPathService swapPathService;
    @Resource
    private StarConfig starConfig;
    @Resource
    private RedisCache redisCache;

    /**
     * ??????????????????????????????
     */
    @Override
    public void computeReward(Long blockId) {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Integer total = tradingPools.stream().map(TradingPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, TradingPoolDto> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> TradingPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;
        BigDecimal blockTotalReward = BigDecimal.TEN;

        tradingPollMap.values().parallelStream().forEach(pool -> {
            // ??????????????????
            tradingPoolUserMapper.computeReward(pool.getId(), blockTotalReward.multiply(pool.getAllocationRatio()), System.currentTimeMillis());
        });
        tradingPoolMapper.updateStatistic(System.currentTimeMillis());

    }

    /**
     * ??????
     */
    @Override
    public void attenuation() {
        tradingPoolUserMapper.attenuation(BigDecimal.valueOf(0.8), System.currentTimeMillis());
    }

    /**
     * ???????????????
     * @param userAddress
     * @param poolId
     * @param tradingAmount
     * @return
     */
    @Override
    public int addTradingAmount(String userAddress, Long poolId, BigDecimal tradingAmount) {
        TradingPoolUserDo selectDo = new TradingPoolUserDo();
        selectDo.setAddress(userAddress);
        selectDo.setPoolId(poolId);
        TradingPoolUserDo tradingPoolUserDo = tradingPoolUserMapper.selectByPrimaryKeySelective(selectDo);
        if (tradingPoolUserDo == null) {
            tradingPoolUserDo = new TradingPoolUserDo();
            tradingPoolUserDo.setAddress(userAddress);
            tradingPoolUserDo.setPoolId(poolId);
            tradingPoolUserDo.setCurrentTradingAmount(BigDecimal.ZERO);
            tradingPoolUserDo.setTotalTradingAmount(BigDecimal.ZERO);
            tradingPoolUserDo.setCurrentTradingAmount(BigDecimal.ZERO);
            tradingPoolUserDo.setCurrentReward(BigDecimal.ZERO);
            return tradingPoolUserMapper.insert(tradingPoolUserDo);
        }
        return tradingPoolUserMapper.addTradingAmount(tradingPoolUserDo.getId(), tradingAmount, System.currentTimeMillis());
    }

    /**
     * ??????????????????
     * @param tokenA
     * @param tokenB
     * @return
     */
    @Override
    public TradingPoolDo getTradingPoolByTokenCode(String tokenA, String tokenB) {
        TradingPoolDo selectDo = new TradingPoolDo();
        selectDo.setTokenA(tokenA);
        selectDo.setTokenB(tokenB);
        TradingPoolDo tradingPoolDo = tradingPoolMapper.selectByPrimaryKeySelective(selectDo);
        if (tradingPoolDo != null) {
            return tradingPoolDo;
        }
        // ??????token
        selectDo = new TradingPoolDo();
        selectDo.setTokenA(tokenB);
        selectDo.setTokenB(tokenA);
        return tradingPoolMapper.selectByPrimaryKeySelective(selectDo);
    }

    /**
     * ??????????????????
     * @param userAddress
     * @return
     */
    @Override
    @Transactional
    public String harvestCurrentReward(String userAddress) {
        // ????????????????????????????????????????????????
        // ????????????????????????
        MiningHarvestRecordDo harvestRecordSelectDo = MiningHarvestRecordDo.builder()
                .address(userAddress)
                .miningType(MiningTypeEnum.TRADING.name())
                .rewardType(RewardTypeEnum.CURRENT.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        MiningHarvestRecordDo miningHarvestRecordDo = miningHarvestRecordMapper.selectByPrimaryKeySelective(harvestRecordSelectDo);
        if (miningHarvestRecordDo != null) {
            throw new IdoException(IdoErrorCode.PENDING_HARVEST_RECORD_EXISTS);
        }
        // ??????????????????????????????
        TradingPoolUserDo selectDo = new TradingPoolUserDo();
        selectDo.setAddress(userAddress);
        List<TradingPoolUserDo> tradingPoolUserDos = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(selectDo);
        if (CollectionUtils.isEmpty(tradingPoolUserDos)) {
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        BigDecimal currentReward = BigDecimal.ZERO;
        for (TradingPoolUserDo tradingPoolUserDo : tradingPoolUserDos) {
            currentReward = currentReward.add(tradingPoolUserDo.getCurrentReward());
        }
        // stc ???????????????usdt
        BigDecimal stcFeePrice = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
        BigDecimal kikoFee = starConfig.getMining().getStcFee().subtract(stcFeePrice);

        // kiko ????????????usdt
        SwapCoins swapCoins = new SwapCoins();
        swapCoins.setShortName(CommonConstant.KIKO_NAME);
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(swapCoins);
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            log.info("harvestCurrentReward ?????????KIKO???????????? {}", CommonConstant.KIKO_NAME);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        SwapCoins swapCoinsKIKO = swapCoinsList.get(0);
        if (currentReward.compareTo(kikoFee) <= 0) {
            log.info("harvestCurrentReward ????????????????????? {}", currentReward.toPlainString());
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        // ??????+?????????????????????+??????????????????
        BigDecimal freedReward = currentReward.multiply(BigDecimal.valueOf(0.5));

        MiningHarvestRecordDo recordDo = MiningHarvestRecordDo.builder()
                .address(userAddress)
                .amount(freedReward)
                .miningType(MiningTypeEnum.TRADING.name())
                .rewardType(RewardTypeEnum.CURRENT.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        miningHarvestRecordMapper.insert(recordDo);
        for (TradingPoolUserDo tradingPoolUserDo : tradingPoolUserDos) {
            tradingPoolUserMapper.harvestReward(tradingPoolUserDo.getId(), tradingPoolUserDo.getCurrentReward(), tradingPoolUserDo.getCurrentReward(), System.currentTimeMillis());
        }
        // ?????????????????????hash?????????????????????????????????????????????????????????+50%????????????????????????????????????
        BigInteger amount = freedReward.subtract(BigDecimalUtil.addPrecision(freedReward, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress()))).toBigInteger();
        BigInteger fee = BigDecimalUtil.addPrecision(kikoFee, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress())).toBigInteger();
        String hash = harvestTradingReward(userAddress, amount, fee);
        ThreadPoolUtil.execute(() -> {
            boolean success;
            try {
                success = contractService.checkTxt(hash);
            } catch (Exception e) {
                log.error("harvestCurrentReward ?????????????????? hash:{}", hash);
                return;
            }
            ITradingMiningService tradingMiningService = ApplicationContextUtils.getBean(ITradingMiningService.class);
            if (success) {
                tradingMiningService.harvestCurrentRewardSuccess(tradingPoolUserDos, recordDo);
            } else {
                tradingMiningService.harvestCurrentRewardFailed(tradingPoolUserDos, recordDo);
            }
        });
        return hash;
    }

    @Override
    @Transactional
    public void harvestCurrentRewardSuccess(List<TradingPoolUserDo> tradingPoolUserDos, MiningHarvestRecordDo miningHarvestRecordDo) {
        for (TradingPoolUserDo tradingPoolUserDo : tradingPoolUserDos) {
            tradingPoolUserMapper.harvestSuccess(tradingPoolUserDo.getId(), tradingPoolUserDo.getCurrentTradingAmount(),
                    tradingPoolUserDo.getCurrentReward(), System.currentTimeMillis());
        }
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.SUCCESS.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordMapper.updateByPrimaryKeySelective(miningHarvestRecordDo);
        // ???????????????????????????
        TradingRewardUserDo tradingRewardUserDo = new TradingRewardUserDo();
        tradingRewardUserDo.setAddress(miningHarvestRecordDo.getAddress());
        tradingRewardUserDo.setLockedReward(miningHarvestRecordDo.getAmount());
        tradingRewardUserDo.setUnlockRewardPerDay(miningHarvestRecordDo.getAmount().divide(BigDecimal.valueOf(7L), 9, RoundingMode.DOWN));
        tradingRewardUserDo.setFreedReward(BigDecimal.ZERO);
        tradingRewardUserDo.setPendingReward(BigDecimal.ZERO);
        tradingRewardUserDo.setNextUnlockTime(System.currentTimeMillis());
        tradingRewardUserDo.setCreateTime(System.currentTimeMillis());
        tradingRewardUserDo.setUpdateTime(System.currentTimeMillis());
        tradingRewardUserMapper.insert(tradingRewardUserDo);
    }

    @Override
    @Transactional
    public void harvestCurrentRewardFailed(List<TradingPoolUserDo> tradingPoolUserDos, MiningHarvestRecordDo miningHarvestRecordDo) {
        for (TradingPoolUserDo tradingPoolUserDo : tradingPoolUserDos) {
            tradingPoolUserMapper.harvestFailed(tradingPoolUserDo.getId(), tradingPoolUserDo.getCurrentTradingAmount(),
                    tradingPoolUserDo.getCurrentReward(), System.currentTimeMillis());
        }
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.FAILED.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordMapper.updateByPrimaryKeySelective(miningHarvestRecordDo);
    }

    /**
     * ?????????????????????
     * @param userAddress
     * @return
     */
    @Transactional
    public String harvestFreedReward(String userAddress) {
        // ???????????????????????????????????????????????????
        MiningHarvestRecordDo harvestRecordSelectDo = MiningHarvestRecordDo.builder()
                .address(userAddress)
                .miningType(MiningTypeEnum.TRADING.name())
                .rewardType(RewardTypeEnum.FREED.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        MiningHarvestRecordDo miningHarvestRecordDo = miningHarvestRecordMapper.selectByPrimaryKeySelective(harvestRecordSelectDo);
        if (miningHarvestRecordDo != null) {
            throw new IdoException(IdoErrorCode.PENDING_HARVEST_RECORD_EXISTS);
        }
        // ??????????????????????????????
        TradingRewardUserDo selectDo = new TradingRewardUserDo();
        selectDo.setAddress(userAddress);
        List<TradingRewardUserDo> tradingRewardUserDos = tradingRewardUserMapper.selectByPrimaryKeySelectiveList(selectDo);
        if (CollectionUtils.isEmpty(tradingRewardUserDos)) {
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        BigDecimal freedReward = BigDecimal.ZERO;
        for (TradingRewardUserDo tradingRewardUserDo : tradingRewardUserDos) {
            freedReward = freedReward.add(tradingRewardUserDo.getFreedReward());
        }
        // stc ???????????????usdt
        BigDecimal stcFeePrice = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
        BigDecimal kikoFee = starConfig.getMining().getStcFee().subtract(stcFeePrice);

        // kiko ????????????usdt
        SwapCoins swapCoins = new SwapCoins();
        swapCoins.setShortName(CommonConstant.KIKO_NAME);
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(swapCoins);
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            log.info("harvestFreedReward ?????????KIKO???????????? {}", CommonConstant.KIKO_NAME);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        SwapCoins swapCoinsKIKO = swapCoinsList.get(0);

        if (freedReward.compareTo(kikoFee) <= 0) {
            log.info("harvestFreedReward ????????????????????? {}", freedReward.toPlainString());
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }

        // ??????+?????????????????????+??????????????????
        MiningHarvestRecordDo recordDo = MiningHarvestRecordDo.builder()
                .address(userAddress)
                .amount(freedReward)
                .miningType(MiningTypeEnum.TRADING.name())
                .rewardType(RewardTypeEnum.CURRENT.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        miningHarvestRecordMapper.insert(recordDo);
        for (TradingRewardUserDo tradingRewardUserDo : tradingRewardUserDos) {
            tradingRewardUserMapper.harvestReward(tradingRewardUserDo.getId(), tradingRewardUserDo.getFreedReward(), System.currentTimeMillis());
        }
        // ?????????????????????hash?????????????????????????????????????????????????????????????????????????????????
        BigInteger amount = freedReward.subtract(BigDecimalUtil.addPrecision(freedReward, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress()))).toBigInteger();
        BigInteger fee = BigDecimalUtil.addPrecision(kikoFee, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress())).toBigInteger();
        String hash = harvestTradingReward(userAddress, amount, fee);
        ThreadPoolUtil.execute(() -> {
            boolean success;
            try {
                success = contractService.checkTxt(hash);
            } catch (Exception e) {
                log.error("harvestCurrentReward ?????????????????? hash:{}", hash);
                return;
            }
            ITradingMiningService tradingMiningService = ApplicationContextUtils.getBean(ITradingMiningService.class);
            if (success) {
                tradingMiningService.harvestFreedRewardSuccess(tradingRewardUserDos, recordDo);
            } else {
                tradingMiningService.harvestFreedRewardFailed(tradingRewardUserDos, recordDo);
            }
        });
        return hash;
    }

    @Override
    @Transactional
    public void harvestFreedRewardSuccess(List<TradingRewardUserDo> tradingRewardUserDos, MiningHarvestRecordDo miningHarvestRecordDo) {
        for (TradingRewardUserDo tradingRewardUserDo : tradingRewardUserDos) {
            tradingRewardUserMapper.harvestSuccess(tradingRewardUserDo.getId(), tradingRewardUserDo.getFreedReward(), System.currentTimeMillis());
        }
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.SUCCESS.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordMapper.updateByPrimaryKeySelective(miningHarvestRecordDo);
        // ???????????????????????????
        TradingRewardUserDo tradingRewardUserDo = new TradingRewardUserDo();
        tradingRewardUserDo.setAddress(miningHarvestRecordDo.getAddress());
        tradingRewardUserDo.setLockedReward(miningHarvestRecordDo.getAmount());
        tradingRewardUserDo.setFreedReward(BigDecimal.ZERO);
        tradingRewardUserDo.setPendingReward(BigDecimal.ZERO);
        tradingRewardUserDo.setCreateTime(System.currentTimeMillis());
        tradingRewardUserDo.setUpdateTime(System.currentTimeMillis());
        tradingRewardUserMapper.insert(tradingRewardUserDo);
    }

    @Override
    @Transactional
    public void harvestFreedRewardFailed(List<TradingRewardUserDo> tradingRewardUserDos, MiningHarvestRecordDo miningHarvestRecordDo) {
        for (TradingRewardUserDo tradingRewardUserDo : tradingRewardUserDos) {
            tradingRewardUserMapper.harvestFailed(tradingRewardUserDo.getId(), tradingRewardUserDo.getFreedReward(), System.currentTimeMillis());
        }
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.FAILED.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordMapper.updateByPrimaryKeySelective(miningHarvestRecordDo);
    }

    /**
     * ????????????????????????????????????
     * @param userAddress
     * @param amount
     * @return
     */
    public String harvestTradingReward(String userAddress, BigInteger amount, BigInteger fee) {
        log.info("harvestTradingReward  userAddress:{} amount:{} ???????????????...", userAddress, amount);

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getMining().getMiningAddress())
                .moduleName(starConfig.getMining().getMiningModule())
                .functionName("trading_harvest")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create(userAddress)),
                        BcsSerializeHelper.serializeU128ToBytes(amount),
                        BcsSerializeHelper.serializeU128ToBytes(fee)
                ))
                .tyArgs(Lists.newArrayList())
                .build();
        return contractService.callFunctionAndGetHash(starConfig.getMining().getManagerAddress(), scriptFunctionObj);
    }

    @Override
    public void unlockReward() {
        Long now = System.currentTimeMillis();
        tradingRewardUserMapper.unlockReward(now, now);
    }

    @Override
    public TradingMingRewardVO reward(String address) {
        if (StringUtils.isBlank(address)) {
            return TradingMingRewardVO.builder().build();
        }
        List<TradingPoolUserDo> userPools = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(TradingPoolUserDo.builder().address(address).build());
        List<TradingRewardUserDo> userRewards = tradingRewardUserMapper.selectByPrimaryKeySelectiveList(TradingRewardUserDo.builder().address(address).build());
        BigDecimal currentReward = userPools.stream().map(TradingPoolUserDo::getCurrentReward).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal lockedReward = userRewards.stream().filter(x->x.getLockedReward().compareTo(x.getUnlockRewardPerDay()) > 0).map(TradingRewardUserDo::getLockedReward).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        BigDecimal freedReward = userRewards.stream().filter(x->x.getLockedReward().compareTo(x.getUnlockRewardPerDay()) > 0).map(TradingRewardUserDo::getFreedReward).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        return TradingMingRewardVO.builder()
                .currentReward(currentReward.toPlainString())
                .lockedReward(lockedReward.add(freedReward).toPlainString())
                .freedReward(freedReward.toPlainString())
                .build();
    }

    /**
     * ??????????????????
     */
    @Override
    public List<TradingPoolVo> poolList(String address) {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Integer total = tradingPools.stream().map(TradingPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, TradingPoolDto> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> TradingPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;

        List<SwapCoins> swapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());
        Map<String, SwapCoins> coinMap = swapCoins.stream().collect(Collectors.toMap(SwapCoins::getAddress, y -> y));
        List<TradingPoolUserDo> userTradingPools = Lists.newArrayList();
        if (StringUtils.isNotBlank(address)) {
            userTradingPools = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(TradingPoolUserDo.builder().address(address).build());
        }
        Map<Long, TradingPoolUserDo>  userTradingPoolMap = userTradingPools.stream().collect(Collectors.toMap(TradingPoolUserDo::getPoolId, y -> y));
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
     * ????????????
     */
    @Override
    public TradingMiningOverviewVO market(String address) {
        List<TradingPoolDo> tradingPools = tradingPoolMapper.selectByPrimaryKeySelectiveList(TradingPoolDo.builder().build());
        Integer total = tradingPools.stream().map(TradingPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, TradingPoolDto> tradingPollMap = tradingPools.stream().collect(Collectors.toMap(TradingPoolDo::getId, y -> TradingPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;

        BigDecimal userCurrentTradingAmount = BigDecimal.ZERO;
        BigDecimal dayReward = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(address)) {
            TradingPoolUserDo param = TradingPoolUserDo.builder().address(address).build();
            List<TradingPoolUserDo> userPools = tradingPoolUserMapper.selectByPrimaryKeySelectiveList(param);

            dayReward = userPools.stream().map(x -> {
                TradingPoolDto tradingPool = tradingPollMap.get(x.getPoolId());
                if (x.getCurrentTradingAmount().compareTo(BigDecimal.ZERO) <= 0
                        || tradingPool.getCurrentTradingAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    return BigDecimal.ZERO;
                }
                BigDecimal currentPoolDayReward = dayTotalReward.multiply(tradingPool.getAllocationRatio());
                return x.getCurrentTradingAmount().multiply(currentPoolDayReward).divide(tradingPool.getCurrentTradingAmount(), 18, RoundingMode.DOWN);
            }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            userCurrentTradingAmount = userPools.stream().map(TradingPoolUserDo::getCurrentTradingAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
        }

        return TradingMiningOverviewVO.builder()
                .currentTradingAmount(tradingPools.stream().map(TradingPoolDo::getCurrentTradingAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).toPlainString())
                .totalTradingAmount(tradingPools.stream().map(TradingPoolDo::getTotalTradingAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO).toPlainString())
                .userCurrentTradingAmount(userCurrentTradingAmount.toPlainString())
                .dailyTotalOutput(dayTotalReward.toPlainString())
                .dailyUserReward(dayReward.toPlainString())
                .build();
    }

}
