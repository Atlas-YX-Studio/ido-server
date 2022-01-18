package com.bixin.nft.controller;

import com.alibaba.fastjson.JSONObject;
import com.bixin.common.config.StarConfig;
import com.bixin.common.constants.CommonConstant;
import com.bixin.common.response.P;
import com.bixin.common.response.R;
import com.bixin.common.utils.BeanCopyUtil;
import com.bixin.nft.bean.DO.*;
import com.bixin.nft.bean.dto.TokenDto;
import com.bixin.nft.bean.vo.NftGroupVo;
import com.bixin.nft.bean.vo.NftInfoVo;
import com.bixin.nft.bean.vo.OperationRecordVo;
import com.bixin.nft.bean.vo.SeriesListVo;
import com.bixin.nft.common.enums.CardElementType;
import com.bixin.nft.common.enums.NftEventType;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.common.enums.OccupationType;
import com.bixin.nft.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @class: NftInfoController
 * @Description: NFT信息记录表 Controller
 * @author: 系统
 * @created: 2021-09-15
 */
@Slf4j
@RestController
@RequestMapping("/v1/nft/")
public class NftInfoController {

    @Resource
    private NftInfoService nftInfoService;
    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private NftKikoCatService nftKikoCatService;
    @Resource
    private NftMarketService nftMarketService;
    @Resource
    private NftEventService nftEventService;
    @Resource
    private NftMetareverseService metareverseService;
    @Resource
    private NftCompositeCardService compositeCardService;
    @Resource
    private StarConfig starConfig;

    /**
     * 元数据
     *
     * @return
     */
    @GetMapping("/meta")
    public R meta() {
        Map<String, Object> result = new HashMap<>();
        // 属性
        List<JSONObject> elements = Stream.of(CardElementType.values()).filter(CardElementType::isEnable)
                .map(type -> {
                    JSONObject json = new JSONObject();
                    json.put("desc", type.getDesc());
                    json.put("cnDesc", type.getCnDesc());
                    json.put("id", type.getId());
                    json.put("maskOrder", type.getMaskOrder());
                    return json;
                }).collect(Collectors.toList());
        result.put("elements", elements);
        // 职业
        List<JSONObject> occupations = Stream.of(OccupationType.values())
                .map(type -> {
                    JSONObject json = new JSONObject();
                    json.put("desc", type.getDesc());
                    json.put("cnDesc", type.getCnDesc());
                    return json;
                }).collect(Collectors.toList());
        result.put("occupations", List.of(occupations));
        result.put("compositeFee", starConfig.getNft().getCompositeFee());

        return R.success(result);
    }

    /**
     * 获取系列列表
     *
     * @return
     */
    @GetMapping("/series/list")
    public R seriesList() {
        List<NftGroupDo> nftGroupDoList = nftGroupService.getListByEnabled(true);
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
        List<NftGroupDo> nftGroupDoList = nftGroupService.getListByEnabled(true);
        if (CollectionUtils.isEmpty(nftGroupDoList)) {
            return R.failed("group不存在");
        }
        List<NftGroupVo> nftGroupVoLis = BeanCopyUtil.copyListProperties(nftGroupDoList, nftGroupDo -> {
            NftGroupVo nftGroupVo = new NftGroupVo();
            nftGroupVo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            if (NftType.COMPOSITE_CARD.getType().equals(nftGroupDo.getType()) && nftGroupDo.getElementId() != 0) {
                NftGroupDo elementGroupDo = nftGroupService.selectById(nftGroupDo.getElementId());
                NftGroupVo elementGroupVo = BeanCopyUtil.copyProperties(elementGroupDo, NftGroupVo::new);
                nftGroupVo.setElement(elementGroupVo);
            }
            return nftGroupVo;
        });
        return R.success(nftGroupVoLis);
    }


    /**
     * 操作记录
     * ID 为 nftInfoId
     *
     * @return
     */
    @GetMapping("/operation/record")
    public P operationRecord(@RequestParam(value = "type", defaultValue = "") String type,
                             @RequestParam(value = "id") Long id,
                             @RequestParam(value = "pageSize", defaultValue = "20") long pageSize,
                             @RequestParam(value = "nextId", defaultValue = "0") long nextId) {

        if (nextId < 0 || pageSize <= 0) {
            return P.failed("parameter is invalid");
        }
        pageSize = pageSize > CommonConstant.MAX_PAGE_SIZE || pageSize <= 0 ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        // type = null 查询所有
        List<NftEventDo> list = nftEventService.getALlByPage(id, type, pageSize + 1, nextId);

        List<OperationRecordVo> records = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (NftEventDo nftEventDo : list) {
                OperationRecordVo operationRecordVo = new OperationRecordVo();

                if (NftEventType.NFT_MINT_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 铸造
                    operationRecordVo.setAddress(nftEventDo.getCreator());
                    operationRecordVo.setPrice(null);
                    operationRecordVo.setCurrencyName("");
                } else if (NftEventType.NFT_SELL_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 上架
                    operationRecordVo.setAddress(nftEventDo.getSeller());
                    operationRecordVo.setPrice(nftEventDo.getSellingPrice());
                    operationRecordVo.setCurrencyName(nftEventDo.getPayToken().split("::")[1]);
                } else if (NftEventType.NFT_BID_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 报价
                    operationRecordVo.setAddress(nftEventDo.getBider());
                    operationRecordVo.setPrice(nftEventDo.getBidPrice());
                    operationRecordVo.setCurrencyName(nftEventDo.getPayToken().split("::")[1]);
                } else if (NftEventType.NFT_BUY_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 获得
                    operationRecordVo.setAddress(nftEventDo.getBider());
                    operationRecordVo.setPrice(nftEventDo.getBidPrice());
                    operationRecordVo.setCurrencyName(nftEventDo.getPayToken().split("::")[1]);
                } else if (NftEventType.NFT_ACCEPT_BID_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 接受报价
                    operationRecordVo.setAddress(nftEventDo.getBider());
                    operationRecordVo.setPrice(nftEventDo.getBidPrice());
                    operationRecordVo.setCurrencyName(nftEventDo.getPayToken().split("::")[1]);
                } else if (NftEventType.NFT_OFFLINE_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 下架
                    operationRecordVo.setAddress(nftEventDo.getSeller());
                    operationRecordVo.setPrice(nftEventDo.getSellingPrice());
                    operationRecordVo.setCurrencyName(nftEventDo.getPayToken().split("::")[1]);
                } else if (NftEventType.NFT_CHANGE_PRICE_EVENT.getDesc().equals(nftEventDo.getType())) {
                    // 修改售价
                    operationRecordVo.setAddress(nftEventDo.getSeller());
                    operationRecordVo.setPrice(nftEventDo.getSellingPrice());
                    operationRecordVo.setCurrencyName(nftEventDo.getPayToken().split("::")[1]);
                } else {
                    continue;
                }
                operationRecordVo.setId(nftEventDo.getId());
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
     * 获取盲盒发售列表
     *
     * @return
     */
    @GetMapping("/box/offering/list")
    public P offeringList(@RequestParam(value = "pageSize", defaultValue = "10") long pageSize,
                          @RequestParam(value = "pageNum", defaultValue = "1") long pageNum) {
        if (pageNum < 0 || pageSize <= 0) {
            return P.failed("parameter is invalid");
        }
        pageSize = pageSize > CommonConstant.MAX_PAGE_SIZE || pageSize <= 0 ? CommonConstant.DEFAULT_PAGE_SIZE : pageSize;
        // type = null 查询所有
        List<NftGroupDo> nftGroupDoList = nftGroupService.getListByPage(true, pageSize + 1, pageNum, true);
        if (CollectionUtils.isEmpty(nftGroupDoList)) {
            return P.success(null, false);
        }
        boolean hasNext = false;
        if (nftGroupDoList.size() > pageSize) {
            nftGroupDoList = nftGroupDoList.subList(0, nftGroupDoList.size() - 1);
            hasNext = true;
        }
        List<NftGroupVo> nftGroupVoLis = BeanCopyUtil.copyListProperties(nftGroupDoList, nftGroupDo -> {
            NftGroupVo nftGroupVo = new NftGroupVo();
            nftGroupVo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            nftGroupVo.setSellingPrice(new BigDecimal(nftGroupDo.getSellingPrice()));
            return nftGroupVo;
        });
        nftGroupVoLis.sort(Comparator.comparingLong(NftGroupVo::getSellingTime).reversed());
        return P.success(nftGroupVoLis, hasNext);
    }

    /**
     * 获取待发售盲盒
     *
     * @return
     */
    @GetMapping("/box/offering/{groupId}")
    public R offering(@PathVariable("groupId") Long groupId) {
        NftGroupDo nftGroupDo = nftGroupService.selectById(groupId);
        if (nftGroupDo == null) {
            return R.success();
        }
        NftGroupVo nftGroupVo = BeanCopyUtil.copyProperties(nftGroupDo, () -> {
            NftGroupVo vo = new NftGroupVo();
            vo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            vo.setSellingPrice(new BigDecimal(nftGroupDo.getSellingPrice()));
            return vo;
        });
        return R.success(nftGroupVo);
    }

    /**
     * 获取盲盒详情，从我的收藏进入
     *
     * @param boxToken
     * @return
     */
    @GetMapping("/box/info")
    public R boxInfo(@RequestParam("boxToken") String boxToken) {
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setBoxToken(boxToken);
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(selectNftGroupDo);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("boxToken不存在");
        }
        NftGroupVo nftGroupVo = BeanCopyUtil.copyProperties(nftGroupDo, () -> {
            NftGroupVo vo = new NftGroupVo();
            vo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            return vo;
        });
        return R.success(nftGroupVo);
    }

    /**
     * 获取盲盒详情，从市场进入
     *
     * @param groupId
     * @return
     */
    @GetMapping("/box/info/{groupId}/{boxId}")
    public R boxInfo(@PathVariable(value = "groupId") Long groupId, @PathVariable(value = "boxId") Long boxId) {
        NftGroupDo nftGroupDo = nftGroupService.selectById(groupId);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("boxToken不存在");
        }
        NftGroupVo nftGroupVo = BeanCopyUtil.copyProperties(nftGroupDo, () -> {
            NftGroupVo vo = new NftGroupVo();
            vo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            return vo;
        });
        NftType nftType = Objects.nonNull(nftGroupDo.getType()) ? NftType.of(nftGroupDo.getType()) : NftType.NORMAL;
        nftGroupVo.setNftType(nftType);

        // 是否出售中
        NftMarketDo nftMarketParam = new NftMarketDo();
        nftMarketParam.setChainId(boxId);
        nftMarketParam.setGroupId(nftGroupDo.getId());
        nftMarketParam.setType(Objects.nonNull(nftGroupDo.getType()) ? nftGroupDo.getType() : "box");
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParam);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftGroupVo.setOnSell(false);
        } else {
            nftGroupVo.setOnSell(true);
            nftGroupVo.setSellingPrice(nftMarketDo.getSellPrice());
            nftGroupVo.setTopBidPrice(nftMarketDo.getOfferPrice());
            nftGroupVo.setOwner(nftMarketDo.getOwner());
        }
        nftGroupVo.setImageLink(nftMarketDo.getIcon());

        return R.success(nftGroupVo);
    }

    /**
     * 未出售NFT列表
     *
     * @param address
     * @return
     */
    @GetMapping("/unsell/list")
    public R unSellList(@RequestParam String address) {
        return R.success(nftInfoService.getUnSellNftList(address));
    }

    /**
     * 待质押NFT列表
     *
     * @param address
     * @return
     */
    @GetMapping("/unused/list")
    public R unusedList(@RequestParam String address) {
        return R.success(nftInfoService.getUnStakingNftList(address));
    }

    /**
     * 获取NFT详情，从我的收藏进入
     *
     * @param nftMeta
     * @param nftBody
     * @param nftId
     * @return
     */
    @GetMapping("/nft/info")
    public R nftInfo(@RequestParam("nftMeta") String nftMeta,
                     @RequestParam("nftBody") String nftBody,
                     @RequestParam("nftId") Long nftId) {
        // 获取groupId
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setNftMeta(nftMeta);
        selectNftGroupDo.setNftBody(nftBody);
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(selectNftGroupDo);
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
        NftCompositeCard compositeCard = null;
        List<NftCompositeElement> compositeElements = new ArrayList<>();
        //cat
        NftKikoCatDo nftKikoCatDo = null;

        NftType nftType = Objects.nonNull(nftGroupDo.getType()) ? NftType.of(nftGroupDo.getType()) : NftType.NORMAL;

        if (nftType == NftType.COMPOSITE_CARD) {
            List<NftCompositeCard> compositeCards = metareverseService.getCompositeCard(nftInfoDo.getId());
            if (CollectionUtils.isEmpty(compositeCards)) {
                return R.failed("NftCompositeCard 不存在，nftId = " + nftInfoDo.getNftId());
            }
            compositeCard = compositeCards.get(0);
            List<Long> elementIds = NftCompositeCard.getElementIds(compositeCard);
            List<NftCompositeElement> elementList = metareverseService.getCompositeElements(new HashSet<>(elementIds));
            if (CollectionUtils.isEmpty(elementList)) {
                return R.failed("NftCompositeElement 不存在，nftIds = " + elementIds);
            }
            compositeElements.addAll(elementList);
        } else if (nftType == NftType.COMPOSITE_ELEMENT) {
            Set<Long> ids = new HashSet<>() {{
                add(nftInfoDo.getId());
            }};
            List<NftCompositeElement> elementList = metareverseService.getCompositeElements(ids);
            if (CollectionUtils.isEmpty(elementList)) {
                return R.failed("NftCompositeElement 不存在，nftIds = " + ids);
            }
            compositeElements.addAll(elementList);
        } else {
            NftKikoCatDo selectNftKikoCatDo = new NftKikoCatDo();
            selectNftKikoCatDo.setInfoId(nftInfoDo.getId());
            nftKikoCatDo = nftKikoCatService.selectByObject(selectNftKikoCatDo);
            if (ObjectUtils.isEmpty(nftKikoCatDo)) {
                return R.failed("nftKikoCatDo不存在，nftId = " + nftInfoDo.getNftId());
            }
        }
        NftInfoVo nftInfoVo = BeanCopyUtil.copyProperties(nftInfoDo, () -> BeanCopyUtil.copyProperties(nftGroupDo, () -> {
            NftInfoVo vo = new NftInfoVo();
            vo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            return vo;
        }));
        if (Objects.nonNull(nftKikoCatDo)) {
            nftInfoVo.setProperties(nftKikoCatDo);
        }
        if (!CollectionUtils.isEmpty(compositeElements)) {
            nftInfoVo.setCompositeElements(compositeElements);
        }
        if (Objects.nonNull(compositeCard)) {
            nftInfoVo.setOccupation(compositeCard.getOccupation());
            nftInfoVo.setCustomName(compositeCard.getCustomName());
            nftInfoVo.setSex(compositeCard.getSex());
        }
        nftInfoVo.setNftType(nftType);
        return R.success(nftInfoVo);
    }

    /**
     * 获取NFT详情，从市场列表进入
     *
     * @return
     */
    @GetMapping("/nft/info/{infoId}")
    public R nftInfo(@PathVariable(value = "infoId") Long id) {
        NftInfoDo nftInfoDo = nftInfoService.selectById(id);
        if (ObjectUtils.isEmpty(nftInfoDo)) {
            return R.failed("nftInfoDo不存在，id = " + id);
        }
        NftGroupDo nftGroupDo = nftGroupService.selectById(nftInfoDo.getGroupId());
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            return R.failed("nftGroupDo不存在，groupId = " + nftInfoDo.getGroupId());
        }
        NftType nftType = Objects.nonNull(nftGroupDo.getType()) ? NftType.of(nftGroupDo.getType()) : NftType.NORMAL;

        NftCompositeCard compositeCard = null;
        List<NftCompositeElement> compositeElements = new ArrayList<>();
        //cat
        NftKikoCatDo nftKikoCatDo = null;

        if (nftType == NftType.COMPOSITE_CARD) {
            List<NftCompositeCard> compositeCards = metareverseService.getCompositeCard(nftInfoDo.getId());
            if (CollectionUtils.isEmpty(compositeCards)) {
                return R.failed("NftCompositeCard 不存在，nftId = " + nftInfoDo.getNftId());
            }
            compositeCard = compositeCards.get(0);
            List<Long> elementIds = NftCompositeCard.getElementIds(compositeCard);
            List<NftCompositeElement> elementList = metareverseService.getCompositeElements(new HashSet<>(elementIds));
            if (CollectionUtils.isEmpty(elementList)) {
                return R.failed("NftCompositeElement 不存在，nftIds = " + elementIds);
            }
            compositeElements.addAll(elementList);
        } else if (nftType == NftType.COMPOSITE_ELEMENT) {
            Set<Long> ids = new HashSet<>() {{
                add(nftInfoDo.getId());
            }};
            List<NftCompositeElement> elementList = metareverseService.getCompositeElements(ids);
            if (CollectionUtils.isEmpty(elementList)) {
                return R.failed("NftCompositeElement 不存在，nftIds = " + ids);
            }
            compositeElements.addAll(elementList);
        } else {
            NftKikoCatDo parm = new NftKikoCatDo();
            parm.setInfoId(nftInfoDo.getId());
            nftKikoCatDo = nftKikoCatService.selectByObject(parm);
            if (ObjectUtils.isEmpty(nftKikoCatDo)) {
                return R.failed("nftKikoCatDo不存在，nftId = " + nftInfoDo.getNftId());
            }
        }
        NftInfoVo nftInfoVo = BeanCopyUtil.copyProperties(nftInfoDo, () -> BeanCopyUtil.copyProperties(nftGroupDo, () -> {
            NftInfoVo vo = new NftInfoVo();
            vo.setSupportToken(TokenDto.of(nftGroupDo.getSupportToken()));
            return vo;
        }));
        // 是否出售中
        NftMarketDo nftMarketParam = new NftMarketDo();
        nftMarketParam.setChainId(nftInfoDo.getNftId());
        nftMarketParam.setNftBoxId(nftInfoDo.getId());
        nftMarketParam.setGroupId(nftInfoDo.getGroupId());
        String type = "nft";
        if (nftType == NftType.COMPOSITE_ELEMENT) {
            type = NftType.COMPOSITE_ELEMENT.getType();
        } else if (nftType == NftType.COMPOSITE_CARD) {
            type = NftType.COMPOSITE_CARD.getType();
        }
        nftMarketParam.setType(type);
        NftMarketDo nftMarketDo = nftMarketService.selectByObject(nftMarketParam);
        if (ObjectUtils.isEmpty(nftMarketDo)) {
            nftInfoVo.setOnSell(false);
        } else {
            nftInfoVo.setOnSell(true);
            nftInfoVo.setSellingPrice(nftMarketDo.getSellPrice());
            nftInfoVo.setTopBidPrice(nftMarketDo.getOfferPrice());
            nftInfoVo.setOwner(nftMarketDo.getOwner());
        }
        if (Objects.nonNull(nftKikoCatDo)) {
            nftInfoVo.setProperties(nftKikoCatDo);
        }
        nftInfoVo.setNftType(nftType);
        if (!CollectionUtils.isEmpty(compositeElements)) {
            nftInfoVo.setCompositeElements(compositeElements);
        }
        if (Objects.nonNull(compositeCard)) {
            nftInfoVo.setOccupation(compositeCard.getOccupation());
            nftInfoVo.setCustomName(compositeCard.getCustomName());
            nftInfoVo.setSex(compositeCard.getSex());
        }
        return R.success(nftInfoVo);
    }

}