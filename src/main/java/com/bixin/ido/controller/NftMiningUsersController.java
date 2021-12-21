package com.bixin.ido.controller;


import com.bixin.ido.bean.vo.NftStakingVO;
import com.bixin.common.response.R;
import com.bixin.common.config.StarConfig;
import com.bixin.ido.entity.NftStakingUsers;
import com.bixin.ido.service.NftMiningUsersService;
import com.bixin.ido.service.NftStakingUsersService;
import com.bixin.common.utils.BeanCopyUtil;
import com.bixin.common.utils.StcSignatureUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户NFT挖矿表 前端控制器
 *
 * @author Xiang Feihan
 * @since 2021-11-26
 */
//@RestController
@RequestMapping("/v1/mining/nft")
public class NftMiningUsersController {

    @Resource
    private NftMiningUsersService nftMiningUsersService;
    @Resource
    private NftStakingUsersService nftStakingUsersService;
    @Resource
    private NftInfoService nftInfoService;
    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private StarConfig starConfig;

    @GetMapping("/market")
    public R market(@RequestParam(required = false, value = "address") String address) {
        return R.success(nftMiningUsersService.market(address));
    }

    @GetMapping("/staking/list")
    public R stakingList(@RequestParam String address) {
        List<NftStakingUsers> nftStakingUserList = nftStakingUsersService.lambdaQuery()
                .eq(NftStakingUsers::getAddress, address)
                .orderByAsc(NftStakingUsers::getOrder)
                .list();
        if (CollectionUtils.isEmpty(nftStakingUserList)) {
            return R.success(List.of());
        }
        List<NftStakingVO> nftInfoVoList = new ArrayList<>();
        nftStakingUserList.forEach(nftStakingUser -> {
            NftStakingVO nftStakingVO = BeanCopyUtil.copyProperties(nftStakingUser, NftStakingVO::new);
            // nft info
            NftInfoDo nftInfoDo = nftInfoService.selectById(nftStakingUser.getInfoId());
            nftStakingVO.setName(nftInfoDo.getName());
            nftStakingVO.setImageLink(nftInfoDo.getImageLink());
            // group info
            NftGroupDo nftGroupDo = nftGroupService.selectById(nftInfoDo.getGroupId());
            nftStakingVO.setNftMeta(nftGroupDo.getNftMeta());
            nftStakingVO.setNftBody(nftGroupDo.getNftBody());
            nftInfoVoList.add(nftStakingVO);
        });
        return R.success(nftInfoVoList);
    }

    @PostMapping("/reward/harvest")
    public R rewardHarvest(@RequestParam String signature) {
        String address = StcSignatureUtil.getAddress(signature);
        return R.success(nftMiningUsersService.harvestReward(address));
    }

    @GetMapping("/fee")
    public R fee() {
        return R.success(starConfig.getMining().getNftMiningStcFee());
    }

}
