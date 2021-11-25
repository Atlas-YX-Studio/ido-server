package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.*;
import com.bixin.ido.server.bean.dto.LPMiningPoolDto;
import com.bixin.ido.server.bean.dto.LPStakeEventEventDto;
import com.bixin.ido.server.bean.dto.LPUnstakeEventEventDto;
import com.bixin.ido.server.bean.vo.LPMingPoolVo;
import com.bixin.ido.server.bean.vo.LPMingRewardVO;
import com.bixin.ido.server.common.enums.HarvestStatusEnum;
import com.bixin.ido.server.common.enums.MiningTypeEnum;
import com.bixin.ido.server.common.enums.RewardTypeEnum;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.mapper.*;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ILPMiningService;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.ITradingMiningService;
import com.bixin.ido.server.utils.*;
import com.bixin.nft.core.service.ContractService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LPMiningServiceImpl implements ILPMiningService {

    @Resource
    private LPMiningPoolUserMapper lpMiningPoolUserMapper;
    @Resource
    private LPMiningPoolMapper lpMiningPoolMapper;
    @Resource
    private ISwapPathService swapPathService;
    //    @Resource
//    private TradingRewardUserMapper tradingRewardUserMapper;
    @Resource
    private MiningHarvestRecordMapper miningHarvestRecordMapper;
    @Resource
    private ContractService contractService;
    @Resource
    private ISwapCoinsService swapCoinsService;
    //    @Resource
//    private ISwapPathService swapPathService;
    @Resource
    private StarConfig starConfig;
    @Resource
    private RedisCache redisCache;
    @Value("${ido.star.swap.usdt-address}")
    private String USDT_CODE;
    @Resource
    private StarConfig idoStarConfig;

    @Override
    public void computeReward(Long blockId) {
        List<LPMiningPoolDo> lpMiningPools = lpMiningPoolMapper.selectByPrimaryKeySelectiveList(LPMiningPoolDo.builder().build());
        Integer total = lpMiningPools.stream().map(LPMiningPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, LPMiningPoolDto> tradingPollMap = lpMiningPools.stream().collect(Collectors.toMap(LPMiningPoolDo::getId, y -> LPMiningPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;
        BigDecimal blockTotalReward = BigDecimal.TEN;

        tradingPollMap.values().parallelStream().forEach(pool -> {
            // 更新个人收益
            lpMiningPoolUserMapper.computeReward(pool.getId(), blockTotalReward.multiply(pool.getAllocationRatio()), System.currentTimeMillis());
        });
        lpMiningPoolMapper.updateStatistic(System.currentTimeMillis());

    }

    @Override
    @Transactional
    public void staking(LPStakeEventEventDto dto) {
        // FIXME: 2021/11/22 获取poolId
//        Long poolId = dto.getTokenCodeStr();
        Long poolId = null;
        LPMiningPoolUserDo userPool = lpMiningPoolUserMapper.selectByPrimaryKeySelective(LPMiningPoolUserDo.builder().address(dto.getSender()).poolId(poolId).build());
        userPool.setStakingAmount(userPool.getStakingAmount().add(dto.getAmount()));
        lpMiningPoolUserMapper.updateByPrimaryKeySelective(userPool);
    }

    @Override
    @Transactional
    public void unStaking(LPUnstakeEventEventDto dto) {
//        Long poolId = dto.getTokenCodeStr();
        Long poolId = null;
        LPMiningPoolUserDo userPool = lpMiningPoolUserMapper.selectByPrimaryKeySelective(LPMiningPoolUserDo.builder().address(dto.getSender()).poolId(poolId).build());
        userPool.setStakingAmount(userPool.getStakingAmount().subtract(dto.getAmount()));
        lpMiningPoolUserMapper.updateByPrimaryKeySelective(userPool);
    }

    @Override
    public List<LPMingPoolVo> poolList(String address) {
        List<LPMiningPoolDo> lpMiningPools = lpMiningPoolMapper.selectByPrimaryKeySelectiveList(LPMiningPoolDo.builder().build());
        Integer total = lpMiningPools.stream().map(LPMiningPoolDo::getAllocationMultiple).reduce(Integer::sum).orElse(0);
        Map<Long, LPMiningPoolDto> tradingPollMap = lpMiningPools.stream().collect(Collectors.toMap(LPMiningPoolDo::getId, y -> LPMiningPoolDto.convertToDto(y, total)));
        BigDecimal dayTotalReward = BigDecimal.TEN;
        BigDecimal blockTotalReward = BigDecimal.TEN;

        List<SwapCoins> swapCoins = swapCoinsService.selectByDDL(SwapCoins.builder().build());
        Map<String, SwapCoins> coinMap = swapCoins.stream().collect(Collectors.toMap(SwapCoins::getAddress, y -> y));
        List<LPMiningPoolUserDo> lpMiningUserPools = Lists.newArrayList();
        if (StringUtils.isNotBlank(address)) {
            lpMiningUserPools = lpMiningPoolUserMapper.selectByPrimaryKeySelectiveList(LPMiningPoolUserDo.builder().address(address).build());
        }
        Map<Long, LPMiningPoolUserDo> userLPMiningPoolMap = lpMiningUserPools.stream().collect(Collectors.toMap(LPMiningPoolUserDo::getPoolId, y -> y));

        List<LPMingPoolVo> lpMiningPoolList = tradingPollMap.values().stream().map(lpMiningPool -> BeanCopyUtil.copyProperties(lpMiningPool, () -> {
            LPMingPoolVo vo = new LPMingPoolVo();
            BigDecimal dayReward = lpMiningPool.getAllocationRatio().multiply(dayTotalReward);
            vo.setDailyTotalOutput(dayReward.toPlainString());
            if (lpMiningPool.getTotalStakingAmount().compareTo(BigDecimal.ZERO) > 0) {
                vo.setApy(dayReward.multiply(BigDecimal.valueOf(365L)).divide(lpMiningPool.getTotalStakingAmount(), 18, RoundingMode.DOWN).setScale(18, RoundingMode.DOWN).toPlainString());
                vo.setCompoundApy(BigDecimal.ONE.add(dayReward.divide(lpMiningPool.getTotalStakingAmount(), 18, RoundingMode.DOWN)).pow(365).setScale(18, RoundingMode.DOWN).toPlainString());
            } else {
                vo.setApy("999999");
                vo.setCompoundApy("999999");
            }

            if (userLPMiningPoolMap.containsKey(lpMiningPool.getId())) {
                LPMiningPoolUserDo tempUserLPMiningPool = userLPMiningPoolMap.get(lpMiningPool.getId());
                vo.setUserStakingAmount(tempUserLPMiningPool.getStakingAmount().toPlainString());
                vo.setUserReward(tempUserLPMiningPool.getReward().toPlainString());
            }
            if (coinMap.containsKey(lpMiningPool.getTokenA())) {
                vo.setTokenIconA(coinMap.get(lpMiningPool.getTokenA()).getIcon());
            }
            if (coinMap.containsKey(lpMiningPool.getTokenB())) {
                vo.setTokenIconB(coinMap.get(lpMiningPool.getTokenB()).getIcon());
            }
            return vo;
        })).collect(Collectors.toList());
        return lpMiningPoolList;
    }

    @Override
    public LPMingRewardVO reward(String address, Long poolId) {
        LPMiningPoolUserDo lpMiningPoolUserDo = lpMiningPoolUserMapper.selectByPrimaryKeySelective(LPMiningPoolUserDo.builder().address(address).poolId(poolId).build());
        if (Objects.isNull(lpMiningPoolUserDo)) {
            return LPMingRewardVO.builder().build();
        }
        return LPMingRewardVO.builder()
                .currentReward(lpMiningPoolUserDo.getReward().toPlainString())
                .stakingAmount(lpMiningPoolUserDo.getStakingAmount().toPlainString())
                .build();
    }


    /**
     * 收取当前收益
     *
     * @param userAddress
     * @param poolId
     * @return
     */
    @Override
    @Transactional
    public String harvestReward(String userAddress, Long poolId) {
        // 获取用户当前收益，判断是否可领取
        // 是否有待确认领取
        MiningHarvestRecordDo harvestRecordSelectDo = MiningHarvestRecordDo.builder()
                .address(userAddress)
                .miningType(MiningTypeEnum.LP_STAKING.name())
                .rewardType(RewardTypeEnum.CURRENT.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        MiningHarvestRecordDo miningHarvestRecordDo = miningHarvestRecordMapper.selectByPrimaryKeySelective(harvestRecordSelectDo);
        if (miningHarvestRecordDo != null) {
            throw new IdoException(IdoErrorCode.PENDING_HARVEST_RECORD_EXISTS);
        }
        // 是否有足够可领取收益
        LPMiningPoolUserDo selectDo = new LPMiningPoolUserDo();
        selectDo.setAddress(userAddress);
        selectDo.setPoolId(poolId);
        LPMiningPoolUserDo userLPMiningPool = lpMiningPoolUserMapper.selectByPrimaryKeySelective(selectDo);
        if (Objects.isNull(userLPMiningPool)) {
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        LPMiningPoolDo lpMiningPool = lpMiningPoolMapper.selectByPrimaryKey(userLPMiningPool.getId());
        Objects.requireNonNull(lpMiningPool, "pool not found");
        // stc 手续费兑换usdt
        BigDecimal stcFeePrice = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
        BigDecimal kikoFee = starConfig.getMining().getStcFee().subtract(stcFeePrice);

        // kiko 收益兑换usdt
        SwapCoins swapCoins = new SwapCoins();
        swapCoins.setShortName(CommonConstant.KIKO_NAME);
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(swapCoins);
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            log.info("harvestCurrentReward 找不到KIKO代币信息 {}", CommonConstant.KIKO_NAME);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        SwapCoins swapCoinsKIKO = swapCoinsList.get(0);
        if (userLPMiningPool.getReward().compareTo(kikoFee) <= 0) {
            log.info("harvestCurrentReward 可提取收益不足 {}", userLPMiningPool.getReward().toPlainString());
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }

        // 扣减+当前交易额扣除+记录提取事件
        MiningHarvestRecordDo recordDo = MiningHarvestRecordDo.builder()
                .address(userAddress)
                .amount(userLPMiningPool.getReward())
                .miningType(MiningTypeEnum.LP_STAKING.name())
                .rewardType(RewardTypeEnum.CURRENT.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        miningHarvestRecordMapper.insert(recordDo);
        lpMiningPoolUserMapper.harvestReward(userLPMiningPool.getId(), userLPMiningPool.getReward(), System.currentTimeMillis());
        // 调取合约，返回hash，异步获取合约结果，成功后更新事件状态+50%进入锁仓，失败后恢复数据
        BigInteger amount = userLPMiningPool.getReward().subtract(BigDecimalUtil.addPrecision(userLPMiningPool.getReward(), swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress()))).toBigInteger();
        BigInteger fee = BigDecimalUtil.addPrecision(kikoFee, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress())).toBigInteger();
        String hash = harvestLPMiningReward(userAddress, amount, fee, lpMiningPool);
        ThreadPoolUtil.execute(() -> {
            boolean success;
            try {
                success = contractService.checkTxt(hash);
            } catch (Exception e) {
                log.error("harvestReward 合约执行超时 hash:{}", hash);
                return;
            }
            ILPMiningService lpMiningService = ApplicationContextUtils.getBean(ILPMiningService.class);
            if (success) {
                lpMiningService.harvestRewardSuccess(userLPMiningPool, recordDo);
            } else {
                lpMiningService.harvestRewardFailed(userLPMiningPool, recordDo);
            }
        });
        return hash;
    }

    @Override
    @Transactional
    public void harvestRewardSuccess(LPMiningPoolUserDo lpMiningPoolUserDo, MiningHarvestRecordDo miningHarvestRecordDo) {
        lpMiningPoolUserMapper.harvestSuccess(lpMiningPoolUserDo.getId(), lpMiningPoolUserDo.getReward(), System.currentTimeMillis());
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.SUCCESS.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordMapper.updateByPrimaryKeySelective(miningHarvestRecordDo);
    }

    @Override
    @Transactional
    public void harvestRewardFailed(LPMiningPoolUserDo lpMiningPoolUserDo, MiningHarvestRecordDo miningHarvestRecordDo) {
        lpMiningPoolUserMapper.harvestFailed(lpMiningPoolUserDo.getId(), lpMiningPoolUserDo.getReward(), System.currentTimeMillis());
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.FAILED.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordMapper.updateByPrimaryKeySelective(miningHarvestRecordDo);
    }

    @Override
    public BigDecimal market() {
        List<LPMiningPoolDo> lpMiningPools = lpMiningPoolMapper.selectByPrimaryKeySelectiveList(LPMiningPoolDo.builder().build());
        Map<String, SwapPathServiceImpl.Pool> liquidityPoolMap = swapPathService.getLiquidityPoolMap();
        Map<String, BigDecimal> coinPriceInfos = swapPathService.getCoinPriceInfos();

        return lpMiningPools.stream().map(pool -> {
            SwapPathServiceImpl.Pool liquidityPool = liquidityPoolMap.get(toPair(pool.getTokenA(), pool.getTokenB()));
            if (Objects.isNull(liquidityPool)) {
                log.error("pair not found in liquidity pools, pairName={}, pools={}", toPair(pool.getTokenA(), pool.getTokenB()), liquidityPoolMap.keySet());
                return BigDecimal.ZERO;
            }
            BigDecimal tokenRate = pool.getTotalStakingAmount().divide(liquidityPool.lpTokenAmount, 18, RoundingMode.DOWN);
            BigDecimal usdtAmountA = tokenRate.multiply(liquidityPool.tokenAmountA).multiply(coinPriceInfos.getOrDefault(toPair(liquidityPool.tokenA, USDT_CODE), BigDecimal.ZERO));
            BigDecimal usdtAmountB = tokenRate.multiply(liquidityPool.tokenAmountB).multiply(coinPriceInfos.getOrDefault(toPair(liquidityPool.tokenB, USDT_CODE), BigDecimal.ZERO));
            return usdtAmountA.add(usdtAmountB);
        }).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    /**
     * 执行合约领取交易挖矿收益
     *
     * @param userAddress
     * @param amount
     * @param lpMiningPool
     * @return
     */
    private String harvestLPMiningReward(String userAddress, BigInteger amount, BigInteger fee, LPMiningPoolDo lpMiningPool) {
        log.info("harvestLPMiningReward  userAddress:{} amount:{} 提取收益中...", userAddress, amount);
        TypeObj lp_token = TypeArgsUtil.parseTypeObj(idoStarConfig.getSwap().getLpTokenResourceName());
        lp_token.setName(lp_token.getName() + "<" + lpMiningPool.getTokenA() + "," + lpMiningPool.getTokenB() + ">");

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getMining().getMiningAddress())
                .moduleName(starConfig.getMining().getMiningModule())
                .functionName("lp_harvest")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create(userAddress)),
                        BcsSerializeHelper.serializeU128ToBytes(amount),
                        BcsSerializeHelper.serializeU128ToBytes(fee)
                ))
                .tyArgs(Lists.newArrayList(lp_token))
                .build();
        return contractService.callFunctionAndGetHash(starConfig.getMining().getManagerAddress(), scriptFunctionObj);
    }

    private String toPair(String tokenA, String tokenB) {
        return tokenA + "_" + tokenB;
    }

}
