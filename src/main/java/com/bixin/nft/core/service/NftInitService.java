package com.bixin.nft.core.service;

import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.enums.NftGroupStatus;
import com.bixin.ido.server.utils.TypeArgsUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.DO.NftKikoCatDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.core.mapper.NftKikoCatMapper;
import com.google.common.collect.Lists;
import com.novi.serde.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class NftInitService {

    @Resource
    private NftGroupMapper nftGroupMapper;
    @Resource
    private NftInfoMapper nftInfoMapper;
    @Resource
    private NftKikoCatMapper nftKikoCatMapper;
    @Resource
    private ContractService contractService;

    @Value("${ido.star.nft.market}")
    private String market;

    @Value("${ido.star.nft.scripts}")
    private String scripts;

    /**
     * 1.部署NFT Market
     * 2.部署NFT Scripts
     * 3.初始化config
     *
     * @return
     */
    public boolean initNFTMarket(BigInteger creatorFee, BigInteger platformFee) {
        if (!contractService.deployContract(market, "contract/nft/NFTMarket.mv", null)) {
            log.error("NFT Market部署失败");
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        if (!contractService.deployContract(scripts, "contract/nft/NFTScripts.mv", null)) {
            log.error("NFT Scripts部署失败");
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName("NFTScripts")
                .functionName("init_config")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(creatorFee),
                        BcsSerializeHelper.serializeU128ToBytes(platformFee)
                ))
                .build();
        return contractService.callFunction(market, scriptFunctionObj);
    }

    /**
     * 1.部署NFT合约
     * 2.mint所有NFT
     * 3.盲盒首发
     *
     * @return
     */
    public void createNFT() {
        // 部署nft合约
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setStatus(NftGroupStatus.APPENDING.name());
        List<NftGroupDo> nftGroupDos = nftGroupMapper.selectByPrimaryKeySelectiveList(selectNftGroupDo);
        if (nftGroupDos != null) {
            nftGroupDos.forEach(nftGroupDo -> {
                if (!deployNFTContract(nftGroupDo)) {
                    log.error("NFT合约 {} 部署失败", nftGroupDo.getName());
                    throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
                }
                nftGroupDo.setStatus(NftGroupStatus.INITIALIZED.name());
                log.info("NFT合约 {} 部署成功", nftGroupDo.getName());
                nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
            });
        }
        // mint nft + 盲盒
        selectNftGroupDo.setStatus(NftGroupStatus.INITIALIZED.name());
        nftGroupDos = nftGroupMapper.selectByPrimaryKeySelectiveList(selectNftGroupDo);
        if (nftGroupDos != null) {
            nftGroupDos.forEach(nftGroupDo -> {
                NftInfoDo selectNftInfoDo = new NftInfoDo();
                selectNftInfoDo.setGroupId(nftGroupDo.getId());
                selectNftInfoDo.setCreated(false);
                // 取出该组下所有待铸造NFT
                List<NftInfoDo> nftInfoDos = nftInfoMapper.selectByPrimaryKeySelectiveList(selectNftInfoDo);
                if (nftInfoDos == null) {
                    return;
                }
                MutableInt nftId = new MutableInt(1);
                nftInfoDos.stream().sorted(Comparator.comparingLong(NftInfoDo::getId)).forEach(nftInfoDo -> {
                    if (!mintKikoCatNFT(nftGroupDo, nftInfoDo)) {
                        log.error("NFT {} mint失败", nftInfoDo.getName());
                        throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                    }
                    log.info("NFT {} mint成功", nftInfoDo.getName());
                    nftInfoDo.setNftId(nftId.longValue());
                    nftInfoDo.setCreated(true);
                    nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                    nftId.add(1);
                });
                // 全部铸造完成，修改
                nftGroupDo.setStatus(NftGroupStatus.CREATED.name());
                nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
            });
        }

        // 盲盒发售
        selectNftGroupDo.setStatus(NftGroupStatus.CREATED.name());
        nftGroupDos = nftGroupMapper.selectByPrimaryKeySelectiveList(selectNftGroupDo);
        if (nftGroupDos != null) {
            nftGroupDos.forEach(nftGroupDo -> {
                if (!transferBox(nftGroupDo)) {
                    log.error("NFT {} 盲盒转账失败", nftGroupDo.getName());
                    throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                }
                if (!initBoxOffering(nftGroupDo)) {
                    log.error("NFT {} 盲盒发售创建失败", nftGroupDo.getName());
                    throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                }
                // 发售成功
                log.info("NFT {} 盲盒发售创建成功", nftGroupDo.getName());
                nftGroupDo.setStatus(NftGroupStatus.OFFERING.name());
                nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
            });
        }
    }

    /**
     * 部署NFT Token
     *
     * @return
     */
    private boolean deployNFTContract(NftGroupDo nftGroupDo) {
        String path = "contract/nft/KikoCat01.mv";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(nftGroupDo.getCreator())
                .moduleName("KikoCat01")
                .functionName("init")
                .tyArgs(Lists.newArrayList())
                .args(Lists.newArrayList())
                .build();
        return contractService.deployContract(nftGroupDo.getCreator(), path, scriptFunctionObj);
    }

    /**
     * mint NFT
     */
    private boolean mintKikoCatNFT(NftGroupDo nftGroupDo, NftInfoDo nftInfoDo) {
        String address = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()).getModuleAddress();
        NftKikoCatDo nftKikoCatDo = new NftKikoCatDo();
        nftKikoCatDo.setInfoId(nftInfoDo.getId());
        nftKikoCatDo = nftKikoCatMapper.selectByPrimaryKeySelective(nftKikoCatDo);

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName("KikoCat01")
                .functionName("mint")
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftInfoDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftInfoDo.getImageData())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getBackground())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFur())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getClothes())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFacialExpression())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHead())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getAccessories())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getEyes()))
                ))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

    /**
     * 将盲盒转至market
     *
     * @param nftGroupDo
     * @return
     */
    private boolean transferBox(NftGroupDo nftGroupDo) {
        double boxTokenDecimal = Math.pow(10, nftGroupDo.getBoxTokenPrecision());
        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getBoxToken());
        return contractService.transfer(nftGroupDo.getCreator(), market, typeObj, BigInteger.valueOf((long)boxTokenDecimal));
    }

    /**
     * 创建盲盒发售
     */
    private boolean initBoxOffering(NftGroupDo nftGroupDo) {
        double boxTokenDecimal = Math.pow(10, nftGroupDo.getBoxTokenPrecision());
        double payTokenDecimal = Math.pow(10, nftGroupDo.getBoxTokenPrecision());
        BigInteger boxAmount = BigInteger.valueOf(nftGroupDo.getQuantity() * (long) boxTokenDecimal);
        BigInteger sellingPrice = BigInteger.valueOf(nftGroupDo.getSellingPrice() * (long) payTokenDecimal);
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName("NFTScripts")
                .functionName("box_initial_offering")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(boxAmount),
                        BcsSerializeHelper.serializeU128ToBytes(sellingPrice),
                        BcsSerializeHelper.serializeU64ToBytes(nftGroupDo.getSellingTime()),
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create(nftGroupDo.getCreator()))
                ))
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftBody()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getBoxToken()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getPayToken())
                ))
                .build();
        return contractService.callFunction(scripts, scriptFunctionObj);
    }

}
