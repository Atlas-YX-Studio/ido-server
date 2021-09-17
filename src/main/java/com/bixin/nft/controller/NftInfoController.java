package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.vo.NftInfoVo;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @class: NftInfoController
 * @Description:  NFT信息记录表 Controller
 * @author: 系统
 * @created: 2021-09-15
 */
@RestController
@RequestMapping("/v1/nft/")
public class NftInfoController {

    @Autowired
    public NftInfoService nftInfoService;

    @Autowired
    public NftGroupService groupService;

    /**
     * 获取待发售盲盒
     * 获取
     * @return
     */
    @GetMapping("/box/offering")
    public R offering() {
        NftGroupDo nftGroupDo = groupService.offering(true);
        return R.success(nftGroupDo);
    }

    /**
     * 获取盲盒详情
     * @param groupId
     * @return
     */
    @GetMapping("/box/info/{groupId}")
    public R boxInfo(@PathVariable(value = "groupId") Long groupId) {
        NftGroupDo nftGroupDo = groupService.selectById(groupId);
        return R.success(nftGroupDo);
    }

    /**
     * 获取NFT详情
     * @return
     */
    @GetMapping("/nft/info/{id}")
    public R nftInfo(@PathVariable(value = "id") Long id) {
        NftInfoDo nftInfoDo = nftInfoService.selectById(id);
        if(ObjectUtils.isEmpty(nftInfoDo)){
            R.failed("nftInfoDo不存在，id = "+id);
        }
        NftGroupDo nftGroupDo = groupService.selectById(nftInfoDo.getGroupId());
        if(ObjectUtils.isEmpty(nftGroupDo)){
            R.failed("nftGroupDo不存在，groupId = "+nftInfoDo.getGroupId());
        }
        NftInfoVo nftInfoVo = null;
        return R.success(nftInfoVo);
    }


    /**
     *
     * @param series
     * @param pageSize
     * @param nextId
     * @return
     */
    @GetMapping("/getAllByPage")
    public P getALlByPage(
            @RequestParam(value = "series") String series,
            @RequestParam(value = "currency") String currency,
            @RequestParam(value = "open") String open,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
            @RequestParam(value = "nextId", defaultValue = "0") long nextId) {

        if (nextId < 0 || pageSize <= 0 || StringUtils.isEmpty(currency)) {
            return P.failed("parameter is invalid");
        }
        pageSize = pageSize > CommonConstant.MAX_PAGE_SIZE ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;

        //List<LiquidityUserRecord> records = liquidityUserRecordService.getALlByPage(userAddress, pageSize + 1, nextId);
        List<NftInfoDo>  records = null;
        boolean hasNext = false;
        if (records.size() > pageSize) {
            records = records.subList(0, records.size() - 1);
            hasNext = true;
        }
        return P.success(records, hasNext);

    }

}