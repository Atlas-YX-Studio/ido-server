package com.bixin.ido.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bixin.ido.server.bean.vo.NftMiningOverviewVO;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.mapper.NftMiningUsersMapper;
import com.bixin.ido.server.entity.NftMiningUsers;
import com.bixin.ido.server.service.NftMiningUsersService;
import com.bixin.ido.server.service.NftStakingUsersService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 用户NFT挖矿表 服务实现类
 *
 * @author Xiang Feihan
 * @since 2021-11-26
 */
@Service
public class NftMiningUsersServiceImpl extends ServiceImpl<NftMiningUsersMapper, NftMiningUsers> implements NftMiningUsersService {

    @Resource
    private NftStakingUsersService nftStakingUsersService;

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
            NftMiningUsers userMining = this.query().eq("address", userAddress).getEntity();
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




}
