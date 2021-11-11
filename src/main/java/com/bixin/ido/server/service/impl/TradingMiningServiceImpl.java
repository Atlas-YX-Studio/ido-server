package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.*;
import com.bixin.ido.server.common.enums.HarvestStatusEnum;
import com.bixin.ido.server.common.enums.MiningTypeEnum;
import com.bixin.ido.server.common.enums.RewardTypeEnum;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.mapper.MiningHarvestRecordMapper;
import com.bixin.ido.server.core.mapper.TradingPoolMapper;
import com.bixin.ido.server.core.mapper.TradingPoolUserMapper;
import com.bixin.ido.server.core.mapper.TradingRewardUserMapper;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.service.ISwapPathService;
import com.bixin.ido.server.service.ITradingMiningService;
import com.bixin.ido.server.utils.ApplicationContextUtils;
import com.bixin.ido.server.utils.BigDecimalUtil;
import com.bixin.ido.server.utils.ThreadPoolUtil;
import com.bixin.nft.core.service.ContractService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 累加交易额
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
     * 查询交易矿池
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
        // 交换token
        selectDo = new TradingPoolDo();
        selectDo.setTokenA(tokenB);
        selectDo.setTokenB(tokenA);
        return tradingPoolMapper.selectByPrimaryKeySelective(selectDo);
    }

    /**
     * 收取当前收益
     * @param userAddress
     * @return
     */
    @Override
    @Transactional
    public String harvestCurrentReward(String userAddress) {
        // 获取用户当前收益，判断是否可领取
        // 是否有待确认领取
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
        // 是否有足够可领取收益
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
        // stc 手续费兑换usdt
        BigDecimal stcFeePrice = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
        BigDecimal usdtFee = starConfig.getMining().getStcFee().subtract(stcFeePrice);

        // kiko 收益兑换usdt
        SwapCoins swapCoins = new SwapCoins();
        swapCoins.setShortName(CommonConstant.KIKO_NAME);
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(swapCoins);
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            log.info("harvestCurrentReward 找不到KIKO代币信息 {}", CommonConstant.KIKO_NAME);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        SwapCoins swapCoinsKIKO = swapCoinsList.get(0);
        BigDecimal kikoPrice = swapPathService.getCoinPriceInfos().getOrDefault(swapCoinsKIKO.getAddress() + "_" + starConfig.getSwap().getUsdtAddress(), BigDecimal.ZERO);
        BigDecimal usdtReward = currentReward.subtract(kikoPrice);

        if (usdtReward.compareTo(usdtFee) <= 0) {
            log.info("harvestCurrentReward 可提取收益不足 {}", currentReward.toPlainString());
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        // 扣减+当前交易额扣除+记录提取事件
        BigDecimal freedReward = currentReward.subtract(BigDecimal.valueOf(0.5));

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
        // 调取合约，返回hash，异步获取合约结果，成功后更新事件状态+50%进入锁仓，失败后恢复数据
        BigInteger amount = freedReward.subtract(BigDecimalUtil.addPrecision(freedReward, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress()))).toBigInteger();
        String hash = harvestTradingReward(userAddress, amount);
        ThreadPoolUtil.execute(() -> {
            boolean success;
            try {
                success = contractService.checkTxt(hash);
            } catch (Exception e) {
                log.error("harvestCurrentReward 合约执行超时 hash:{}", hash);
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
        // 锁仓部分进入收益表
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
     * 收取已释放收益
     * @param userAddress
     * @return
     */
    @Transactional
    public String harvestFreedReward(String userAddress) {
        // 获取用户已解锁收益，判断是否可领取
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
        // 是否有足够可领取收益
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
        // stc 手续费兑换usdt
        BigDecimal stcFeePrice = (BigDecimal) redisCache.getValue(CommonConstant.STC_FEE_PRICE_KEY);
        BigDecimal usdtFee = starConfig.getMining().getStcFee().subtract(stcFeePrice);

        // kiko 收益兑换usdt
        SwapCoins swapCoins = new SwapCoins();
        swapCoins.setShortName(CommonConstant.KIKO_NAME);
        List<SwapCoins> swapCoinsList = swapCoinsService.selectByDDL(swapCoins);
        if (CollectionUtils.isEmpty(swapCoinsList)) {
            log.info("harvestFreedReward 找不到KIKO代币信息 {}", CommonConstant.KIKO_NAME);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        SwapCoins swapCoinsKIKO = swapCoinsList.get(0);
        BigDecimal kikoPrice = swapPathService.getCoinPriceInfos().getOrDefault(swapCoinsKIKO.getAddress() + "_" + starConfig.getSwap().getUsdtAddress(), BigDecimal.ZERO);
        BigDecimal usdtReward = freedReward.subtract(kikoPrice);

        if (usdtReward.compareTo(usdtFee) <= 0) {
            log.info("harvestFreedReward 可提取收益不足 {}", freedReward.toPlainString());
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }

        // 扣减+当前交易额扣除+记录提取事件
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
        // 调取合约，返回hash，异步获取合约结果，成功后更新事件状态，失败后恢复数据
        BigInteger amount = freedReward.subtract(BigDecimalUtil.addPrecision(freedReward, swapPathService.getCoinPrecision(swapCoinsKIKO.getAddress()))).toBigInteger();
        String hash = harvestTradingReward(userAddress, amount);
        ThreadPoolUtil.execute(() -> {
            boolean success;
            try {
                success = contractService.checkTxt(hash);
            } catch (Exception e) {
                log.error("harvestCurrentReward 合约执行超时 hash:{}", hash);
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
        // 锁仓部分进入收益表
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
     * 执行合约领取交易挖矿收益
     * @param userAddress
     * @param amount
     * @return
     */
    public String harvestTradingReward(String userAddress, BigInteger amount) {
        log.info("harvestTradingReward  userAddress:{} amount:{} 提取收益中...", userAddress, amount);

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getMining().getMiningAddress())
                .moduleName(starConfig.getMining().getMiningModule())
                .functionName("trading_harvest")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create(userAddress)),
                        BcsSerializeHelper.serializeU128ToBytes(amount)
                ))
                .tyArgs(Lists.newArrayList())
                .build();
        return contractService.callFunctionAndGetHash(starConfig.getMining().getManagerAddress(), scriptFunctionObj);
    }

}
