package com.bixin.nft.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bixin.common.code.IdoErrorCode;
import com.bixin.common.config.StarConfig;
import com.bixin.common.exception.IdoException;
import com.bixin.common.utils.TypeArgsUtil;
import com.bixin.ido.common.enums.NftGroupStatus;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftCompositeElement;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.dto.TokenDto;
import com.bixin.nft.biz.NftContractBiz;
import com.bixin.nft.common.enums.CardElementType;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.core.mapper.NftCompositeCardMapper;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.core.mapper.NftKikoCatMapper;
import com.bixin.nft.service.ContractService;
import com.bixin.nft.service.NftCompositeCardService;
import com.bixin.nft.service.NftCompositeElementService;
import com.google.common.collect.Lists;
import com.novi.serde.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * NFT 卡牌 服务实现类
 *
 * @author Xiang Feihan
 * @since 2021-12-20
 */
@Slf4j
@Service
public class NftCompositeCardServiceImpl extends ServiceImpl<NftCompositeCardMapper, NftCompositeCard> implements NftCompositeCardService {

    @Resource
    private NftCompositeElementService elementService;
    @Resource
    private NftGroupMapper nftGroupMapper;
    @Resource
    private NftInfoMapper nftInfoMapper;
    @Resource
    private NftKikoCatMapper nftKikoCatMapper;
    @Resource
    private ContractService contractService;
    @Resource
    private NftContractBiz nftContractBiz;
    @Resource
    private StarConfig starConfig;

    @Override
    public void createCompositeNFT(long cardGroupId) {
        NftGroupDo cardGroupDo = nftGroupMapper.selectByPrimaryKey(cardGroupId);
        if (cardGroupDo == null) {
            log.error("groupId:{} 不存在", cardGroupId);
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        if (!NftType.COMPOSITE_CARD.getType().equals(cardGroupDo.getType())) {
            log.error("NFT type:{} 必须为card", cardGroupDo.getType());
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        createElementNFT(cardGroupDo.getElementId());
        createCardNFT(cardGroupDo.getId());
    }

    /**
     *
     *
     * @return
     */
    public void createElementNFT(long groupId) {
        // 部署nft合约
        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(groupId);
        if (nftGroupDo == null) {
            log.error("groupId:{} 不存在", groupId);
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        if (NftGroupStatus.PENDING.name().equals(nftGroupDo.getStatus())) {
            if (!nftContractBiz.deployNFTContractWithImage(nftGroupDo)) {
                log.error("NFT合约 {} 部署失败", nftGroupDo.getName());
                throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
            }
            nftGroupDo.setStatus(NftGroupStatus.INITIALIZED.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            log.info("NFT合约 {} 部署成功", nftGroupDo.getName());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
        }
        // mint nft
        if (NftGroupStatus.INITIALIZED.name().equals(nftGroupDo.getStatus())) {
            NftInfoDo selectNftInfoDo = new NftInfoDo();
            selectNftInfoDo.setGroupId(nftGroupDo.getId());
            selectNftInfoDo.setCreated(false);
            // 取出该组下所有待铸造NFT
            List<NftInfoDo> nftInfoDos = nftInfoMapper.selectByPrimaryKeySelectiveList(selectNftInfoDo);
            if (CollectionUtils.isEmpty(nftInfoDos)) {
                return;
            }
            MutableInt nftId = new MutableInt(1);
            // 获取该组最后一个id
            selectNftInfoDo = new NftInfoDo();
            selectNftInfoDo.setGroupId(nftGroupDo.getId());
            selectNftInfoDo.setCreated(true);
            List<NftInfoDo> createdNftInfoDos = nftInfoMapper.selectByPrimaryKeySelectiveList(selectNftInfoDo);
            if (!CollectionUtils.isEmpty(createdNftInfoDos)) {
                createdNftInfoDos.sort(Comparator.comparingLong(NftInfoDo::getNftId).reversed());
                nftId.setValue(createdNftInfoDos.get(0).getNftId() + 1);
            }
            nftInfoDos.stream().sorted(Comparator.comparingLong(NftInfoDo::getId)).forEach(nftInfoDo -> {
                nftInfoDo.setNftId(nftId.longValue());
                nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                // 铸造NFT，存放图片url
                if (!mintElementNFT(nftGroupDo, nftInfoDo)) {
                    log.error("NFT {} mint失败", nftInfoDo.getName());
                    throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                }
                log.info("NFT {} mint成功", nftInfoDo.getName());
                nftInfoDo.setOwner("");
                nftInfoDo.setCreated(true);
                nftInfoDo.setUpdateTime(System.currentTimeMillis());
                nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                nftId.add(1);
            });
            // 全部铸造完成，修改
            nftGroupDo.setStatus(NftGroupStatus.CREATED.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
            // 重新rank
            nftContractBiz.reRank(nftGroupDo.getSeries());
        }

        // 市场创建resource + 盲盒发售
        if (NftGroupStatus.CREATED.name().equals(nftGroupDo.getStatus())) {
            List<TokenDto> supportTokenList = JSON.parseObject(nftGroupDo.getSupportToken(),
                    new TypeReference<>() {
                    });
            supportTokenList.forEach(tokenDto -> {
                if (!nftContractBiz.initMarket(nftGroupDo, tokenDto.getAddress())) {
                    log.error("NFT {} 市场初始化失败, 设置币种:{}", nftGroupDo.getName(), tokenDto.getAddress());
                    throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                }
            });
            // 发售成功
            log.info("NFT {} 盲盒发售创建成功", nftGroupDo.getName());
            nftGroupDo.setStatus(NftGroupStatus.OFFERING.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
        }
    }

    /**
     * 部署NFT合约
     *
     * @return
     */
    public void createCardNFT(long groupId) {
        // 部署nft合约
        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(groupId);
        if (nftGroupDo == null) {
            log.error("groupId:{} 不存在", groupId);
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        NftGroupDo elementGroupDo = nftGroupMapper.selectByPrimaryKey(nftGroupDo.getElementId());
        if (elementGroupDo == null) {
            log.error("element groupId:{} 不存在", nftGroupDo.getElementId());
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        if (!NftGroupStatus.OFFERING.name().equals(elementGroupDo.getStatus())) {
            log.error("element groupId:{} 未创建", nftGroupDo.getElementId());
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }

        if (NftGroupStatus.PENDING.name().equals(nftGroupDo.getStatus())) {
            if (!nftContractBiz.deployNFTContractWithImage(nftGroupDo)) {
                log.error("NFT合约 {} 部署失败", nftGroupDo.getName());
                throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
            }
            nftGroupDo.setStatus(NftGroupStatus.INITIALIZED.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            log.info("NFT合约 {} 部署成功", nftGroupDo.getName());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
        }
        // mint nft
        if (NftGroupStatus.INITIALIZED.name().equals(nftGroupDo.getStatus())) {
            NftInfoDo selectNftInfoDo = new NftInfoDo();
            selectNftInfoDo.setGroupId(nftGroupDo.getId());
            selectNftInfoDo.setCreated(false);
            // 取出该组下所有待铸造NFT
            List<NftInfoDo> nftInfoDos = nftInfoMapper.selectByPrimaryKeySelectiveList(selectNftInfoDo);
            if (CollectionUtils.isEmpty(nftInfoDos)) {
                return;
            }
            MutableInt nftId = new MutableInt(1);
            // 获取该组最后一个id
            selectNftInfoDo = new NftInfoDo();
            selectNftInfoDo.setGroupId(nftGroupDo.getId());
            selectNftInfoDo.setCreated(true);
            List<NftInfoDo> createdNftInfoDos = nftInfoMapper.selectByPrimaryKeySelectiveList(selectNftInfoDo);
            if (!CollectionUtils.isEmpty(createdNftInfoDos)) {
                createdNftInfoDos.sort(Comparator.comparingLong(NftInfoDo::getNftId).reversed());
                nftId.setValue(createdNftInfoDos.get(0).getNftId() + 1);
            }
            nftInfoDos.stream().sorted(Comparator.comparingLong(NftInfoDo::getId)).forEach(nftInfoDo -> {
                nftInfoDo.setNftId(nftId.longValue());
                nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                // 铸造NFT，存放图片url
                if (!mintCardNFT(nftGroupDo, nftInfoDo)) {
                    log.error("NFT {} mint失败", nftInfoDo.getName());
                    throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                }
                log.info("NFT {} mint成功", nftInfoDo.getName());
                nftInfoDo.setOwner("");
                nftInfoDo.setCreated(true);
                nftInfoDo.setUpdateTime(System.currentTimeMillis());
                nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                nftId.add(1);
            });
            // 全部铸造完成，修改
            nftGroupDo.setStatus(NftGroupStatus.CREATED.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
            // 重新rank
            nftContractBiz.reRank(nftGroupDo.getSeries());
        }

        // 市场创建resource
        if (NftGroupStatus.CREATED.name().equals(nftGroupDo.getStatus())) {
            List<TokenDto> supportTokenList = JSON.parseObject(nftGroupDo.getSupportToken(),
                    new TypeReference<>() {
                    });
            supportTokenList.forEach(tokenDto -> {
                if (!nftContractBiz.initMarket(nftGroupDo, tokenDto.getAddress())) {
                    log.error("NFT {} 市场初始化失败, 设置币种:{}", nftGroupDo.getName(), tokenDto.getAddress());
                    throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                }
            });
            if (!nftContractBiz.transferBox(nftGroupDo)) {
                log.error("NFT {} 盲盒转账失败", nftGroupDo.getName());
                throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
            }
            log.info("NFT {} 盲盒转账成功", nftGroupDo.getName());
            nftGroupDo.setStatus(NftGroupStatus.TRANSFER.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
        }

        // 盲盒发售
        if (NftGroupStatus.TRANSFER.name().equals(nftGroupDo.getStatus())) {
            if (!nftContractBiz.initBoxOffering(nftGroupDo)) {
                log.error("NFT {} 盲盒发售创建失败", nftGroupDo.getName());
                throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
            }
            // 发售成功
            log.info("NFT {} 盲盒发售创建成功", nftGroupDo.getName());
            nftGroupDo.setStatus(NftGroupStatus.OFFERING.name());
            nftGroupDo.setUpdateTime(System.currentTimeMillis());
            nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
        }

    }

    /**
     * mint NFT，存放图片url
     */
    private boolean mintElementNFT(NftGroupDo nftGroupDo, NftInfoDo nftInfoDo) {
        NftCompositeElement element = elementService.lambdaQuery().eq(NftCompositeElement::getInfoId, nftInfoDo.getId()).one();

        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta());
        String address = typeObj.getModuleAddress();

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName(typeObj.getModuleName())
                .functionName("mint_with_image")
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftInfoDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(starConfig.getNft().getImageInfoApi() + nftInfoDo.getId())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(element.getType())),
                        BcsSerializeHelper.serializeU64ToBytes(CardElementType.of(element.getType()).getId()),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(element.getProperty())),
                        BcsSerializeHelper.serializeU128ToBytes(element.getScore().multiply(BigDecimal.TEN.pow(9)).toBigInteger())
                ))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

    /**
     * mint NFT，存放图片url
     */
    private boolean mintCardNFT(NftGroupDo nftGroupDo, NftInfoDo nftInfoDo) {
        NftCompositeCard card = this.lambdaQuery().eq(NftCompositeCard::getInfoId, nftInfoDo.getId()).one();

        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta());
        String address = typeObj.getModuleAddress();

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getNft().getCatadd())
                .moduleName(typeObj.getModuleName())
                .functionName("composite_original_card")
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftInfoDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(starConfig.getNft().getImageInfoApi() + nftInfoDo.getId())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(card.getOccupation())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(card.getCustomName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeU8(card.getSex())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getBackgroundId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFurId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getClothesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getExpressionId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHeadId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getAccessoriesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getEyesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHatId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getCostumeId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMakeupId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getShoesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMouthId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getEarringId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getNecklaceId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getNeckId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHairId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHornId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHandsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getBodyId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSkinId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getTattooId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getPeopleId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getCharacteristicId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHobbyId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getZodiacId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getActionId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getToysId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFruitsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getVegetablesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMeatId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getBeveragesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFoodId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getVehicleId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getWeatherId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMonthId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSportsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMusicId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMoviesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSeasonId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getOutfitId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFaceId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getArmId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getLegId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFoodId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getWeaponId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHelmetId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getArmorId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMechaId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getPantsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSkirtId()))
                ))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

    private long getNftId(Long infoId) {
        if (infoId != 0) {
            NftInfoDo nftInfoDo = nftInfoMapper.selectByPrimaryKey(infoId);
            return nftInfoDo.getNftId();
        }
        return 0;
    }

    public boolean mintCustomCardNFT(String address, Long infoId) {
        NftInfoDo nftInfoDo = nftInfoMapper.selectByPrimaryKey(infoId);

        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(nftInfoDo.getGroupId());
        NftCompositeCard card = this.lambdaQuery().eq(NftCompositeCard::getInfoId, nftInfoDo.getId()).one();
        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta());
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getNft().getCatadd())
                .moduleName(typeObj.getModuleName())
                .functionName("composite_custom_card")
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftInfoDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(starConfig.getNft().getImageInfoApi() + nftInfoDo.getId())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(card.getOccupation())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(card.getCustomName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeU8(card.getSex())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getBackgroundId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFurId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getClothesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getExpressionId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHeadId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getAccessoriesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getEyesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHatId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getCostumeId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMakeupId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getShoesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMouthId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getEarringId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getNecklaceId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getNeckId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHairId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHornId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHandsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getBodyId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSkinId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getTattooId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getPeopleId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getCharacteristicId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHobbyId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getZodiacId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getActionId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getToysId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFruitsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getVegetablesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMeatId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getBeveragesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFoodId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getVehicleId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getWeatherId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMonthId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSportsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMusicId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMoviesId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSeasonId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getOutfitId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFaceId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getArmId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getLegId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getFoodId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getWeaponId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getHelmetId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getArmorId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getMechaId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getPantsId())),
                        BcsSerializeHelper.serializeU64ToBytes(getNftId(card.getSkirtId()))
                ))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

    public boolean resolve_card(String address, String module, Long nftId) {
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(starConfig.getNft().getCatadd())
                .moduleName(module)
                .functionName("resolve_card")
                .args(Lists.newArrayList(BcsSerializeHelper.serializeU64ToBytes(nftId)))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

}
