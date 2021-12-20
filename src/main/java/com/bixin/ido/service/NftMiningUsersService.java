package com.bixin.ido.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bixin.ido.bean.vo.NftMiningOverviewVO;
import com.bixin.ido.entity.NftMiningUsers;

/**
 * 用户NFT挖矿表 服务类
 *
 * @author Xiang Feihan
 * @since 2021-11-26
 */
public interface NftMiningUsersService extends IService<NftMiningUsers> {

    void computeReward(Long blockId);

    NftMiningOverviewVO market(String userAddress);

    String harvestReward(String userAddress);
}
