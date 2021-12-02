package com.bixin.ido.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bixin.ido.server.bean.vo.NftMiningOverviewVO;
import com.bixin.ido.server.common.enums.HarvestStatusEnum;
import com.bixin.ido.server.common.enums.MiningTypeEnum;
import com.bixin.ido.server.common.enums.RewardTypeEnum;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.mapper.NftMiningUsersMapper;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.entity.MiningHarvestRecords;
import com.bixin.ido.server.entity.NftMiningUsers;
import com.bixin.ido.server.service.MiningHarvestRecordsService;
import com.bixin.ido.server.service.NftMiningUsersService;
import com.bixin.ido.server.service.NftStakingUsersService;
import com.bixin.ido.server.utils.ApplicationContextUtils;
import com.bixin.ido.server.utils.BigDecimalUtil;
import com.bixin.ido.server.utils.ThreadPoolUtil;
import com.bixin.nft.core.service.ContractService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 用户NFT挖矿表 服务实现类
 *
 * @author Xiang Feihan
 * @since 2021-11-26
 */
@Slf4j
@Service
public class NftMiningUsersServiceImpl extends ServiceImpl<NftMiningUsersMapper, NftMiningUsers> implements NftMiningUsersService {

    @Resource
    private NftStakingUsersService nftStakingUsersService;
    @Resource
    private MiningHarvestRecordsService miningHarvestRecordsService;
    @Resource
    private NftMiningUsersService nftMiningUsersService;
    @Resource
    private ContractService contractService;
    @Resource
    private RedisCache redisCache;

    @Resource
    private StarConfig starConfig;

    @Override
    public void computeReward(Long blockId) {
        this.baseMapper.computeReward(starConfig.getMining().getNftMiningBlockReward(), System.currentTimeMillis());
    }

    @Override
    public NftMiningOverviewVO market(String userAddress) {
        BigDecimal totalScore = this.baseMapper.totalScore();
        int totalNftAmount = this.nftStakingUsersService.count();
        BigDecimal avgApr = NftMiningUsersServiceImpl.this.starConfig.getMining().getNftMiningDayReward()
                .multiply(BigDecimal.valueOf(365L))
                .divide(NftMiningUsersServiceImpl.this.starConfig.getMining().getNftUnitPrice().multiply(BigDecimal.valueOf(totalNftAmount)), 18, RoundingMode.DOWN);
        NftMiningOverviewVO vo = NftMiningOverviewVO.builder()
                .dailyTotalOutput(NftMiningUsersServiceImpl.this.starConfig.getMining().getNftMiningDayReward().toPlainString())
                .currentReward(BigDecimal.ZERO.toPlainString())
                .totalScore(totalScore.toPlainString())
                .userScore(BigDecimal.ZERO.toPlainString())
                .avgApr(avgApr.toPlainString())
                .userApr(BigDecimal.ZERO.toPlainString())
                .build();

        if (StringUtils.isNotBlank(userAddress)) {
            NftMiningUsers userMining = this.query().eq("address", userAddress).one();
            if (Objects.nonNull(userMining)) {
                BigDecimal userApr = userMining.getScore()
                        .multiply(NftMiningUsersServiceImpl.this.starConfig.getMining().getNftMiningDayReward())
                        .multiply(BigDecimal.valueOf(365L))
                        .divide(totalScore.multiply(NftMiningUsersServiceImpl.this.starConfig.getMining().getNftUnitPrice()).multiply(BigDecimal.valueOf(totalNftAmount)), 18, RoundingMode.DOWN);
                vo.setCurrentReward(userMining.getReward().toPlainString());
                vo.setUserScore(userMining.getScore().toPlainString());
                vo.setUserApr(userApr.toPlainString());
            }

        }

        return vo;
    }

    /**
     * 收取当前收益
     *
     * @param userAddress
     * @return
     */
    @Override
    public String harvestReward(String userAddress) {
        // 获取用户当前收益，判断是否可领取
        // 是否有待确认领取
        Wrapper<MiningHarvestRecords> miningHarvestRecordsWrapper = Wrappers.<MiningHarvestRecords>lambdaQuery()
                .eq(MiningHarvestRecords::getAddress, userAddress)
                .eq(MiningHarvestRecords::getMiningType, MiningTypeEnum.NFT_STAKING.name())
                .eq(MiningHarvestRecords::getRewardType, RewardTypeEnum.CURRENT.name())
                .eq(MiningHarvestRecords::getStatus, HarvestStatusEnum.PENDING.name());
        MiningHarvestRecords pendingHarvestRecords = miningHarvestRecordsService.getOne(miningHarvestRecordsWrapper, false);
        if (pendingHarvestRecords != null) {
            throw new IdoException(IdoErrorCode.PENDING_HARVEST_RECORD_EXISTS);
        }
        // 是否有足够可领取收益
        Wrapper<NftMiningUsers> nftMiningUsersWrapper = Wrappers.<NftMiningUsers>lambdaQuery()
                .eq(NftMiningUsers::getAddress, userAddress);
        NftMiningUsers nftMiningUsers = getOne(nftMiningUsersWrapper, false);
        if (Objects.isNull(nftMiningUsers)) {
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        // stc 手续费兑换usdt
        BigDecimal stcFee = starConfig.getMining().getStcFee();
        if (nftMiningUsers.getReward().compareTo(stcFee) <= 0) {
            log.info("harvestCurrentReward 可提取收益不足 {}", nftMiningUsers.getReward().toPlainString());
            throw new IdoException(IdoErrorCode.REWARD_NOT_INSUFFICIENT);
        }
        // 记录提取事件
        MiningHarvestRecords harvestRecords = MiningHarvestRecords.builder()
                .address(userAddress)
                .amount(nftMiningUsers.getReward())
                .miningType(MiningTypeEnum.LP_STAKING.name())
                .rewardType(RewardTypeEnum.CURRENT.name())
                .status(HarvestStatusEnum.PENDING.name())
                .build();
        miningHarvestRecordsService.save(harvestRecords);
        // 扣除收益
        LambdaUpdateWrapper<NftMiningUsers> nftMiningUsersUpdateWrapper = Wrappers.<NftMiningUsers>lambdaUpdate()
                .setSql("reward = reward - " + nftMiningUsers.getReward())
                .setSql("pending_reward = pending_reward + " + nftMiningUsers.getReward())
                .set(NftMiningUsers::getUpdateTime, System.currentTimeMillis())
                .eq(NftMiningUsers::getId, nftMiningUsers.getId());
        update(nftMiningUsersUpdateWrapper);
        // 调取合约，返回hash，异步获取合约结果，成功后更新事件状态+50%进入锁仓，失败后恢复数据
        BigInteger amount = nftMiningUsers.getReward().subtract(BigDecimalUtil.addPrecision(nftMiningUsers.getReward(), 18)).toBigInteger();
        BigInteger fee = BigDecimalUtil.addPrecision(stcFee, 18).toBigInteger();
        String hash = harvestTradingReward(userAddress, amount, fee);
        ThreadPoolUtil.execute(() -> {
            boolean success;
            try {
                success = contractService.checkTxt(hash);
            } catch (Exception e) {
                log.error("harvestReward 合约执行超时 hash:{}", hash);
                return;
            }
            NftMiningUsersServiceImpl nftMiningUsersServiceImpl = ApplicationContextUtils.getBean(this.getClass());
            if (success) {
                nftMiningUsersServiceImpl.harvestRewardSuccess(nftMiningUsers, harvestRecords);
            } else {
                nftMiningUsersServiceImpl.harvestRewardFailed(nftMiningUsers, harvestRecords);
            }
        });
        return hash;
    }

    /**
     * 执行合约领取交易挖矿收益
     * @param userAddress
     * @param amount
     * @return
     */
    private String harvestTradingReward(String userAddress, BigInteger amount, BigInteger fee) {
        log.info("harvestTradingReward  userAddress:{} amount:{} 提取收益中...", userAddress, amount);
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getMining().getNftMiningAddress())
                .moduleName(starConfig.getMining().getNftMiningModule())
                .functionName("nft_mining_harvest_stc")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create(userAddress)),
                        BcsSerializeHelper.serializeU128ToBytes(amount),
                        BcsSerializeHelper.serializeU128ToBytes(fee)
                ))
                .tyArgs(Lists.newArrayList())
                .build();
        return contractService.callFunctionAndGetHash(starConfig.getMining().getNftMiningAddress(), scriptFunctionObj);
    }

    @Transactional
    public void harvestRewardSuccess(NftMiningUsers nftMiningUsers, MiningHarvestRecords miningHarvestRecordDo) {
        LambdaUpdateWrapper<NftMiningUsers> nftMiningUsersUpdateWrapper = Wrappers.<NftMiningUsers>lambdaUpdate()
                .setSql("pending_reward = pending_reward - " + nftMiningUsers.getReward())
                .set(NftMiningUsers::getUpdateTime, System.currentTimeMillis())
                .eq(NftMiningUsers::getId, nftMiningUsers.getId());
        nftMiningUsersService.update(nftMiningUsersUpdateWrapper);
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.SUCCESS.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordsService.updateById(miningHarvestRecordDo);
    }

    @Transactional
    public void harvestRewardFailed(NftMiningUsers nftMiningUsers, MiningHarvestRecords miningHarvestRecordDo) {
        LambdaUpdateWrapper<NftMiningUsers> nftMiningUsersUpdateWrapper = Wrappers.<NftMiningUsers>lambdaUpdate()
                .setSql("reward = reward + " + nftMiningUsers.getReward())
                .setSql("pending_reward = pending_reward - " + nftMiningUsers.getReward())
                .set(NftMiningUsers::getUpdateTime, System.currentTimeMillis())
                .eq(NftMiningUsers::getId, nftMiningUsers.getId());
        nftMiningUsersService.update(nftMiningUsersUpdateWrapper);
        miningHarvestRecordDo.setStatus(HarvestStatusEnum.FAILED.name());
        miningHarvestRecordDo.setUpdateTime(System.currentTimeMillis());
        miningHarvestRecordsService.updateById(miningHarvestRecordDo);
    }

}
