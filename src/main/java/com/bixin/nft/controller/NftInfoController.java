package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.utils.BeanCopyUtil;
import com.bixin.ido.server.utils.HexStringUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.*;
import com.bixin.nft.bean.vo.NftGroupVo;
import com.bixin.nft.bean.vo.NftInfoVo;
import com.bixin.nft.bean.vo.OperationRecordVo;
import com.bixin.nft.bean.vo.SeriesListVo;
import com.bixin.nft.core.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Autowired
    public NftEventService nftEventService;

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
     * 获取group列表
     *
     * @return
     */
    @GetMapping("/group/list")
    public R groupList() {
        NftGroupDo nftGroupDo = new NftGroupDo();
        List<NftGroupDo> nftGroupDoList = groupService.listByObject(nftGroupDo);
        if (CollectionUtils.isEmpty(nftGroupDoList)) {
            return R.failed("group不存在");
        }
        return R.success(nftGroupDoList);
    }


    /**
     * 操作记录
     * ID 为 nft id
     * @return
     */
    @GetMapping("/operation/record")
    public P operationRecord(@RequestParam(value = "type") String type,
                             @RequestParam(value = "id") Long id,
                             @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                             @RequestParam(value = "nextId", defaultValue = "0") long nextId) {

        if (nextId < 0 || pageSize <= 0 || StringUtils.isEmpty(type)) {
            return P.failed("parameter is invalid");
        }
        pageSize = pageSize > CommonConstant.MAX_PAGE_SIZE ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        List<NftEventDo> list = nftEventService.getALlByPage(id,type, pageSize,nextId);
        List<OperationRecordVo> records = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            for(NftEventDo nftEventDo : list){
                OperationRecordVo operationRecordVo = new OperationRecordVo();
                operationRecordVo.setAddress(nftEventDo.getSeller());
                operationRecordVo.setPrice(nftEventDo.getSellingPrice());
                operationRecordVo.setCurrencyName(nftEventDo.getPayTokenName());
                operationRecordVo.setType(nftEventDo.getType());
                operationRecordVo.setCreateTime(nftEventDo.getCreateTime());
                records.add(operationRecordVo);
            }
        }
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
     * 获取盲盒详情，从市场列表进入
     *
     * @param groupId
     * @return
     */
    @GetMapping("/box/info/{groupId}/{boxId}")
    public R boxInfo(@PathVariable(value = "groupId") Long groupId, @PathVariable(value = "boxId") Long boxId) {
        NftGroupDo nftGroupDo = groupService.selectById(groupId);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("boxToken不存在");
        }
        NftGroupVo nftGroupVo = BeanCopyUtil.copyProperties(nftGroupDo, () -> BeanCopyUtil.copyProperties(nftGroupDo, NftGroupVo::new));
        // 是否出售中
        NftMarketDo nftMarketParam = new NftMarketDo();
        nftMarketParam.setChainId(boxId);
        nftMarketParam.setGroupId(nftGroupDo.getId());
        nftMarketParam.setType("box");
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParam);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftGroupVo.setOnSell(false);
        } else {
            nftGroupVo.setOnSell(true);
            nftGroupVo.setSellingPrice(nftMarketDo.getSellPrice());
            nftGroupVo.setTopBidPrice(nftMarketDo.getOfferPrice());
            nftGroupVo.setOwner(nftMarketDo.getOwner());
        }
        return R.success(nftGroupVo);
    }

    /**
     * 获取盲盒详情
     *
     * @param boxToken
     * @return
     */
    @GetMapping("/box/info")
    public R boxInfo(@RequestParam("boxToken") String boxToken, @RequestParam("payToken") String payToken) {
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setBoxToken(boxToken);
        selectNftGroupDo.setPayToken(payToken);
        NftGroupDo nftGroupDo = groupService.selectByObject(selectNftGroupDo);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("boxToken不存在");
        }
        return R.success(nftGroupDo);
    }

    @GetMapping("/nft/info")
    public R nftInfo(@RequestParam("nftMeta") String nftMeta, @RequestParam("nftBody") String nftBody,
                     @RequestParam("nftId") Long nftId, @RequestParam("payToken") String payToken) {
        // 获取groupId
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setNftMeta(nftMeta);
        selectNftGroupDo.setNftBody(nftBody);
        selectNftGroupDo.setPayToken(payToken);
        NftGroupDo nftGroupDo = groupService.selectByObject(selectNftGroupDo);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("nftGroupDo不存在，meta = " + nftMeta + "，body = " + nftBody);
        }
        // 获取infoId
        NftInfoDo selectNftInfoDo = new NftInfoDo();
        selectNftInfoDo.setGroupId(nftGroupDo.getId());
        selectNftInfoDo.setNftId(nftId);
        NftInfoDo nftInfoDo = nftInfoService.selectByObject(selectNftInfoDo);
        if (ObjectUtils.isEmpty(nftInfoDo)) {
            return R.failed("nftInfoDo不存在，meta = " + nftMeta + "，body = " + nftBody + "，nftId = " + nftId);
        }
        // 获取cat
        NftKikoCatDo selectNftKikoCatDo = new NftKikoCatDo();
        selectNftKikoCatDo.setInfoId(nftInfoDo.getId());
        NftKikoCatDo nftKikoCatDo = nftKikoCatService.selectByObject(selectNftKikoCatDo);
        if (ObjectUtils.isEmpty(nftKikoCatDo)) {
            return R.failed("nftKikoCatDo不存在，nftId = " + nftInfoDo.getNftId());
        }
        NftInfoVo nftInfoVo = BeanCopyUtil.copyProperties(nftInfoDo, () -> BeanCopyUtil.copyProperties(nftGroupDo, NftInfoVo::new));
        // 是否出售中
        NftMarketDo nftMarketParam = new NftMarketDo();
        nftMarketParam.setChainId(nftId);
        nftMarketParam.setNftBoxId(nftInfoDo.getId());
        nftMarketParam.setGroupId(nftInfoDo.getGroupId());
        nftMarketParam.setType("nft");
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParam);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftInfoVo.setOnSell(false);
            nftInfoVo.setOwner(nftInfoDo.getOwner());
        } else {
            nftInfoVo.setOnSell(true);
            nftInfoVo.setSellingPrice(nftMarketDo.getSellPrice());
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
    @GetMapping("/nft/info/{infoId}")
    public R nftInfo(@PathVariable(value = "infoId") Long id) {
        NftInfoVo nftInfoVo = new NftInfoVo();
        NftInfoDo nftInfoDo = nftInfoService.selectById(id);
        if (ObjectUtils.isEmpty(nftInfoDo)) {
            return R.failed("nftInfoDo不存在，id = " + id);
        }
        NftGroupDo nftGroupDo = groupService.selectById(nftInfoDo.getGroupId());
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("nftGroupDo不存在，groupId = " + nftInfoDo.getGroupId());
        }
        NftKikoCatDo parm = new NftKikoCatDo();
        parm.setInfoId(nftInfoDo.getId());
        NftKikoCatDo nftKikoCatDo = nftKikoCatService.selectByObject(parm);
        if (ObjectUtils.isEmpty(nftKikoCatDo)) {
            return R.failed("nftKikoCatDo不存在，nftId = " + nftInfoDo.getNftId());
        }
        NftMarketDo nftMarketParam = new NftMarketDo();
        nftMarketParam.setChainId(nftInfoDo.getNftId());
        nftMarketParam.setNftBoxId(nftInfoDo.getId());
        nftMarketParam.setGroupId(nftInfoDo.getGroupId());
        nftMarketParam.setType("nft");
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParam);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftInfoVo.setOnSell(false);
        } else {
            nftInfoVo.setOnSell(true);
            nftInfoVo.setSellingPrice(nftMarketDo.getSellPrice());
            nftInfoVo.setTopBidPrice(nftMarketDo.getOfferPrice());
            nftInfoVo.setOwner(nftMarketDo.getOwner());
        }
        nftInfoVo.setNftId(nftInfoDo.getNftId());
        nftInfoVo.setGroupId(nftInfoDo.getGroupId());
        nftInfoVo.setImageLink(nftInfoDo.getImageLink());
        nftInfoVo.setScore(nftInfoDo.getScore());
        nftInfoVo.setRank(nftInfoDo.getRank());
        BeanUtils.copyProperties(nftGroupDo, nftInfoVo);
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