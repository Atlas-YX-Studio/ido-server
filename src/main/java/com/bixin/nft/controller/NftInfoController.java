package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.utils.BeanCopyUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.DO.NftKikoCatDo;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.bean.vo.NftInfoVo;
import com.bixin.nft.bean.vo.OperationRecordVo;
import com.bixin.nft.bean.vo.SeriesListVo;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import com.bixin.nft.core.service.NftKikoCatService;
import com.bixin.nft.core.service.NftMarketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @class: NftInfoController
 * @Description: NFT信息记录表 Controller
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

    @Autowired
    public NftKikoCatService nftKikoCatService;

    @Autowired
    public NftMarketService nftMarketService;

    /**
     * 获取系列列表
     *
     * @return
     */
    @GetMapping("/series/list")
    public R seriesList() {
        List<NftGroupDo> nftGroupDoList = groupService.getListByEnabled(true);
        List<SeriesListVo> list = new ArrayList<>();
        for (NftGroupDo nftGroupDo : nftGroupDoList) {
            SeriesListVo seriesListVo = new SeriesListVo();
            seriesListVo.setGroupId(nftGroupDo.getId());
            seriesListVo.setGroupName(nftGroupDo.getName());
            seriesListVo.setSeriesName(nftGroupDo.getSeriesName());
            list.add(seriesListVo);
        }
        return R.success(list);
    }


    /**
     * 操作记录
     *
     * @return
     */
    @GetMapping("/operation/record")
    public P operationRecord(@RequestParam(value = "tab") String tab,
                             @RequestParam(value = "sort") String sort,
                             @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                             @RequestParam(value = "nextId", defaultValue = "0") long nextId) {

        if (nextId < 0 || pageSize <= 0 || StringUtils.isEmpty(tab)) {
            return P.failed("parameter is invalid");
        }
        pageSize = pageSize > CommonConstant.MAX_PAGE_SIZE ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;

        List<OperationRecordVo> records = new ArrayList<>();

        OperationRecordVo operationRecordVo = new OperationRecordVo();
        //todo
        operationRecordVo.setAddress("aaaaa");
        operationRecordVo.setPrice(new BigDecimal(12));
        operationRecordVo.setCurrencyName("STC");
        operationRecordVo.setStatus("aaa");
        operationRecordVo.setCreated(new Date().getTime());
        records.add(operationRecordVo);

        boolean hasNext = false;
        if (records.size() > pageSize) {
            records = records.subList(0, records.size() - 1);
            hasNext = true;
        }
        return P.success(records, hasNext);
    }

    /**
     * 获取待发售盲盒
     *
     * @return
     */
    @GetMapping("/box/offering")
    public R offering() {
        NftGroupDo nftGroupDo = groupService.offering(true);
        return R.success(nftGroupDo);
    }

    /**
     * 获取盲盒详情
     *
     * @param groupId
     * @return
     */
    @GetMapping("/box/info/{groupId}")
    public R boxInfo(@PathVariable(value = "groupId") Long groupId) {
        NftGroupDo nftGroupDo = groupService.selectById(groupId);
        return R.success(nftGroupDo);
    }

    @GetMapping("/nft/info")
    public R nftInfo(@RequestParam("meta") String meta, @RequestParam("body") String body, @RequestParam("nftId") Long nftId) {
        // 获取groupId
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setNftMeta(meta);
        selectNftGroupDo.setNftBody(body);
        List<NftGroupDo> nftGroupDos = groupService.listByObject(selectNftGroupDo);
        if (CollectionUtils.isEmpty(nftGroupDos)) {
            return R.failed("nftGroupDo不存在，meta = " + meta + "，body = " + body);
        }
        // 获取infoId
        NftGroupDo nftGroupDo = nftGroupDos.get(0);
        NftInfoDo selectNftInfoDo = new NftInfoDo();
        selectNftInfoDo.setGroupId(nftGroupDo.getId());
        selectNftInfoDo.setNftId(nftId);
        List<NftInfoDo> nftInfoDos = nftInfoService.listByObject(selectNftInfoDo);
        if (CollectionUtils.isEmpty(nftInfoDos)) {
            return R.failed("nftInfoDo不存在，meta = " + meta + "，body = " + body + "，nftId = " + nftId);
        }
        NftInfoDo nftInfoDo = nftInfoDos.get(0);
        // 获取cat
        NftKikoCatDo nftKikoCatDo = nftKikoCatService.selectByNftId(nftInfoDo.getNftId());
        if (ObjectUtils.isEmpty(nftKikoCatDo)) {
            return R.failed("nftKikoCatDo不存在，nftId = " + nftInfoDo.getNftId());
        }
        NftInfoVo nftInfoVo = BeanCopyUtil.copyProperties(nftGroupDo, () -> BeanCopyUtil.copyProperties(nftInfoDo, NftInfoVo::new));
        // 是否出售中
        NftMarketDo nftMarketParm = new NftMarketDo();
        nftMarketParm.setNftBoxId(nftInfoDo.getNftId());
        nftMarketParm.setNftBoxId(nftInfoDo.getGroupId());
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParm);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftInfoVo.setOnSell(false);
            nftInfoVo.setOwner("");
        } else {
            nftInfoVo.setOnSell(true);
            nftInfoVo.setTopBidPrice(nftMarketDo.getOfferPrice());
            nftInfoVo.setOwner(nftMarketDo.getOwner());
        }
        nftInfoVo.setProperties(nftKikoCatDo);
        return R.success(nftInfoVo);
    }


    /**
     * 获取NFT详情
     *
     * @return
     */
    @GetMapping("/nft/info/{id}")
    public R nftInfo(@PathVariable(value = "id") Long id) {
        NftInfoVo nftInfoVo = new NftInfoVo();
        NftInfoDo nftInfoDo = nftInfoService.selectById(id);
        if (ObjectUtils.isEmpty(nftInfoDo)) {
            return R.failed("nftInfoDo不存在，id = " + id);
        }
        NftGroupDo nftGroupDo = groupService.selectById(nftInfoDo.getGroupId());
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("nftGroupDo不存在，groupId = " + nftInfoDo.getGroupId());
        }
        NftKikoCatDo nftKikoCatDo = nftKikoCatService.selectByNftId(nftInfoDo.getNftId());
        if (ObjectUtils.isEmpty(nftKikoCatDo)) {
            return R.failed("nftKikoCatDo不存在，nftId = " + nftInfoDo.getNftId());
        }
        NftMarketDo nftMarketParm = new NftMarketDo();
        nftMarketParm.setNftBoxId(nftInfoDo.getNftId());
        nftMarketParm.setNftBoxId(nftInfoDo.getGroupId());
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParm);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftInfoVo.setOnSell(false);
            nftInfoVo.setOwner("");
        } else {
            nftInfoVo.setOnSell(true);
            nftInfoVo.setTopBidPrice(nftMarketDo.getOfferPrice());
            nftInfoVo.setOwner(nftMarketDo.getOwner());
        }
        nftInfoVo.setNftId(nftInfoDo.getNftId());
        nftInfoVo.setGroupId(nftInfoDo.getGroupId());
        nftInfoVo.setImageLink(nftInfoDo.getImageLink());
        nftInfoVo.setScore(nftInfoDo.getScore());
        nftInfoVo.setRank(nftInfoDo.getRank());
        BeanUtils.copyProperties(nftInfoVo, nftGroupDo);
        nftInfoVo.setGroupName(nftGroupDo.getName());
        nftInfoVo.setProperties(nftKikoCatDo);
        return R.success(nftInfoVo);
    }

    /**
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
        List<NftInfoDo> records = null;
        boolean hasNext = false;
        if (records.size() > pageSize) {
            records = records.subList(0, records.size() - 1);
            hasNext = true;
        }
        return P.success(records, hasNext);

    }

}