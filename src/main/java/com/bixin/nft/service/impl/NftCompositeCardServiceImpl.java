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

    /**
     *
     *
     * @return
     */
    public void createElementNFT(long groupId) {
        // 部署nft合约
        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(groupId);
        if (nftGroupDo == null) {
            return;
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
                        BcsSerializeHelper.serializeU64ToBytes(CardElementType.valueOf(element.getType()).getId()),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(element.getProperty())),
                        BcsSerializeHelper.serializeU128ToBytes(element.getScore().multiply(BigDecimal.TEN.pow(9)).toBigInteger())
                ))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

}
