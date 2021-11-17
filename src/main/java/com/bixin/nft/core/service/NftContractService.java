package com.bixin.nft.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.enums.NftGroupStatus;
import com.bixin.ido.server.utils.BigDecimalUtil;
import com.bixin.ido.server.utils.TypeArgsUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.DO.NftKikoCatDo;
import com.bixin.nft.bean.dto.TokenDto;
import com.bixin.nft.biz.NftImagesUploadBiz;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.bixin.nft.core.mapper.NftKikoCatMapper;
import com.google.common.collect.Lists;
import com.novi.serde.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.starcoin.bean.ScriptFunctionObj;
import org.starcoin.bean.TypeObj;
import org.starcoin.utils.AccountAddressUtils;
import org.starcoin.utils.BcsSerializeHelper;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class NftContractService {

    @Resource
    private NftGroupMapper nftGroupMapper;
    @Resource
    private NftInfoMapper nftInfoMapper;
    @Resource
    private NftKikoCatMapper nftKikoCatMapper;
    @Resource
    private ContractService contractService;
    @Resource
    private NftImagesUploadBiz nftImagesUploadBiz;

    @Value("${ido.star.nft.market}")
    private String market;
    @Value("${ido.star.nft.scripts}")
    private String scripts;
    @Value("${ido.star.nft.market-module}")
    private String marketModule;
    @Value("${ido.star.nft.scripts-module}")
    private String scriptsModule;
    @Value("${ido.star.nft.image-group-api}")
    private String imageGroupApi;
    @Value("${ido.star.nft.image-info-api}")
    private String imageInfoApi;

    /**
     * 1.部署NFT Market
     * 2.部署NFT Scripts
     * 3.初始化config
     *
     * @return
     */
    public void initNFTMarket(BigInteger creatorFee, BigInteger platformFee) {
        if (!contractService.deployContract(market, "contract/nft/" + marketModule + ".mv", null)) {
            log.error("NFT Market部署失败");
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        if (!contractService.deployContract(scripts, "contract/nft/" + scriptsModule + ".mv", null)) {
            log.error("NFT Scripts部署失败");
            throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
        }
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("init_config")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(creatorFee),
                        BcsSerializeHelper.serializeU128ToBytes(platformFee)
                ))
                .build();
        if (!contractService.callFunction(market, scriptFunctionObj)) {
            log.error("NFT Config初始化失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    /**
     * 1.部署NFT合约
     * 2.mint所有NFT
     * 3.盲盒首发
     *
     * @return
     */
    public void createNFT() {
        // 同步上传图片
        nftImagesUploadBiz.run();

        // 部署nft合约
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setStatus(NftGroupStatus.APPENDING.name());
        List<NftGroupDo> nftGroupDos = nftGroupMapper.selectByPrimaryKeySelectiveList(selectNftGroupDo);
        if (nftGroupDos != null) {
            nftGroupDos.forEach(nftGroupDo -> {
                if (!deployNFTContractWithImage(nftGroupDo)) {
                    log.error("NFT合约 {} 部署失败", nftGroupDo.getName());
                    throw new IdoException(IdoErrorCode.CONTRACT_DEPLOY_FAILURE);
                }
                nftGroupDo.setStatus(NftGroupStatus.INITIALIZED.name());
                nftGroupDo.setUpdateTime(System.currentTimeMillis());
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
                    NftInfoDo nftInfoDoWithImage = nftInfoMapper.selectByPrimaryKey(nftInfoDo.getId());
                    nftInfoDoWithImage.setNftId(nftId.longValue());
                    nftInfoMapper.updateByPrimaryKeySelective(nftInfoDoWithImage);
                    // 铸造NFT，存放图片url
                    if (!mintKikoCatNFTWithImage(nftGroupDo, nftInfoDoWithImage)) {
                        log.error("NFT {} mint失败", nftInfoDoWithImage.getName());
                        throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                    }
                    log.info("NFT {} mint成功", nftInfoDoWithImage.getName());
                    nftInfoDoWithImage.setOwner("");
                    nftInfoDoWithImage.setCreated(true);
                    nftInfoDoWithImage.setUpdateTime(System.currentTimeMillis());
                    nftInfoMapper.updateByPrimaryKeySelective(nftInfoDoWithImage);
                    nftId.add(1);
                });
                // 全部铸造完成，修改
                nftGroupDo.setStatus(NftGroupStatus.CREATED.name());
                nftGroupDo.setUpdateTime(System.currentTimeMillis());
                nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
                // 重新rank
                reRank(nftGroupDo.getSeries());
            });
        }

        // 市场创建resource + 盲盒发售
        selectNftGroupDo.setStatus(NftGroupStatus.CREATED.name());
        nftGroupDos = nftGroupMapper.selectByPrimaryKeySelectiveList(selectNftGroupDo);
        if (nftGroupDos != null) {
            nftGroupDos.forEach(nftGroupDo -> {
                List<TokenDto> supportTokenList = JSON.parseObject(nftGroupDo.getSupportToken(),
                        new TypeReference<>() {});
                supportTokenList.forEach(tokenDto -> {
                    if (!initMarket(nftGroupDo, tokenDto.getAddress())) {
                        log.error("NFT {} 市场初始化失败, 设置币种:{}", nftGroupDo.getName(), tokenDto.getAddress());
                        throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
                    }
                });
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
                nftGroupDo.setUpdateTime(System.currentTimeMillis());
                nftGroupMapper.updateByPrimaryKeySelective(nftGroupDo);
            });
        }
    }

    /**
     * 部署NFT Token，封面为图片url
     *
     * @return
     */
    private boolean deployNFTContractWithImage(NftGroupDo nftGroupDo) {
        String moduleName = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()).getModuleName();
        String path = "contract/nft/" + moduleName + ".mv";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(nftGroupDo.getCreator())
                .moduleName(moduleName)
                .functionName("init_with_image")
                .tyArgs(Lists.newArrayList())
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(imageGroupApi + nftGroupDo.getId())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription()))
                ))
                .build();
        return contractService.deployContract(nftGroupDo.getCreator(), path, scriptFunctionObj);
    }

    /**
     * mint NFT，存放图片url
     */
    private boolean mintKikoCatNFTWithImage(NftGroupDo nftGroupDo, NftInfoDo nftInfoDo) {
        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta());
        String address = typeObj.getModuleAddress();
        NftKikoCatDo nftKikoCatDo = new NftKikoCatDo();
        nftKikoCatDo.setInfoId(nftInfoDo.getId());
        nftKikoCatDo = nftKikoCatMapper.selectByPrimaryKeySelective(nftKikoCatDo);

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName(typeObj.getModuleName())
                .functionName("mint_with_image")
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftInfoDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(imageInfoApi + nftInfoDo.getId())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getBackground())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFur())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getClothes())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFacialExpression())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHead())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getAccessories())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getEyes())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHat())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getCostume())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMakeup())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getShoes())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMouth())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getEarring())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getNecklace())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getNeck())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHair())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHorn())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHands())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getBody())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getSkin())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getTattoo())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getPeople())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getCharacteristic())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHobby())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getZodiac())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getAction())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getToys())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFruits())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getVegetables())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMeat())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getBeverages())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFood())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getVehicle())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getWeather())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMonth())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getSports())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMusic())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMovies())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getSeason())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getOutfit()))
                ))
                .build();
        return contractService.callFunction(address, scriptFunctionObj);
    }

    /**
     * 部署NFT Token，封面为图片元数据
     *
     * @return
     */
    private boolean deployNFTContractWithImageData(NftGroupDo nftGroupDo) {
        String moduleName = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()).getModuleName();
        String path = "contract/nft/" + moduleName + ".mv";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(nftGroupDo.getCreator())
                .moduleName(moduleName)
                .functionName("init_with_image_data")
                .tyArgs(Lists.newArrayList())
                .args(Lists.newArrayList(
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getName())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getNftTypeImageData())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftGroupDo.getEnDescription()))
                ))
                .build();
        return contractService.deployContract(nftGroupDo.getCreator(), path, scriptFunctionObj);
    }

    /**
     * mint NFT，存放图片元数据
     */
    private boolean mintKikoCatNFTWithImageData(NftGroupDo nftGroupDo, NftInfoDo nftInfoDo) {
        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta());
        String address = typeObj.getModuleAddress();
        NftKikoCatDo nftKikoCatDo = new NftKikoCatDo();
        nftKikoCatDo.setInfoId(nftInfoDo.getId());
        nftKikoCatDo = nftKikoCatMapper.selectByPrimaryKeySelective(nftKikoCatDo);

        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName(typeObj.getModuleName())
                .functionName("mint_with_image_data")
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
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getEyes())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHat())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getCostume())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMakeup())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getShoes())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMouth())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getEarring())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getNecklace())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getNeck())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHair())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHorn())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHands())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getBody())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getSkin())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getTattoo())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getPeople())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getCharacteristic())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getHobby())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getZodiac())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getAction())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getToys())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFruits())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getVegetables())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMeat())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getBeverages())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getFood())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getVehicle())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getWeather())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMonth())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getSports())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMusic())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getMovies())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getSeason())),
                        Bytes.valueOf(BcsSerializeHelper.serializeString(nftKikoCatDo.getOutfit()))
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
        double boxTokenDecimal = nftGroupDo.getOfferingQuantity() * Math.pow(10, nftGroupDo.getBoxTokenPrecision());
        TypeObj typeObj = TypeArgsUtil.parseTypeObj(nftGroupDo.getBoxToken());
        return contractService.transfer(nftGroupDo.getCreator(), market, typeObj, BigInteger.valueOf((long) boxTokenDecimal));
    }

    /**
     * 创建盲盒发售
     */
    private boolean initBoxOffering(NftGroupDo nftGroupDo) {
        double boxTokenDecimal = Math.pow(10, nftGroupDo.getBoxTokenPrecision());
        double payTokenDecimal = Math.pow(10, nftGroupDo.getPayTokenPrecision());
        BigInteger boxAmount = BigInteger.valueOf(nftGroupDo.getOfferingQuantity() * (long) boxTokenDecimal);
        BigInteger sellingPrice = BigInteger.valueOf(nftGroupDo.getSellingPrice() * (long) payTokenDecimal);
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
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
        return contractService.callFunction(market, scriptFunctionObj);
    }

    /**
     * 初始化市场
     */
    private boolean initMarket(NftGroupDo nftGroupDo, String payToken) {
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("init_market")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeAddressToBytes(AccountAddressUtils.create(nftGroupDo.getCreator()))
                ))
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftBody()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getBoxToken()),
                        TypeArgsUtil.parseTypeObj(payToken)
                ))
                .build();
        return contractService.callFunction(market, scriptFunctionObj);
    }

    /**
     * 初始化NFT回购
     */
    public void initBuyBackNFT(Long groupId, String payToken) {
        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(groupId);
        if (nftGroupDo == null) {
            log.error("NFT市场回购初始化失败, groupId:{} 不存在", groupId);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("init_buy_back_list")
                .args(Lists.newArrayList())
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftBody()),
                        TypeArgsUtil.parseTypeObj(payToken)
                ))
                .build();
        if (!contractService.callFunction(market, scriptFunctionObj)) {
            log.error("NFT市场回购初始化失败, 合约请求失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    /**
     * @return
     */
    public void buyBackNFT(Long infoId, String payToken, BigDecimal price) {
        NftInfoDo nftInfoDo = nftInfoMapper.selectByPrimaryKey(infoId);
        if (nftInfoDo == null) {
            log.error("NFT市场回购失败, infoId:{} 不存在", infoId);
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        NftGroupDo nftGroupDo = nftGroupMapper.selectByPrimaryKey(nftInfoDo.getGroupId());
        if (nftGroupDo == null) {
            log.error("NFT市场回购失败, groupId:{} 不存在", nftInfoDo.getGroupId());
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
        BigInteger priceFactor = BigDecimalUtil.getPrecisionFactor(9).multiply(price).toBigInteger();
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("nft_buy_back")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU64ToBytes(nftInfoDo.getNftId()),
                        BcsSerializeHelper.serializeU128ToBytes(priceFactor)
                ))
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftMeta()),
                        TypeArgsUtil.parseTypeObj(nftGroupDo.getNftBody()),
                        TypeArgsUtil.parseTypeObj(payToken)
                ))
                .build();
        if (!contractService.callFunction(market, scriptFunctionObj)) {
            log.error("NFT市场回购失败, 合约请求失败");
            throw new IdoException(IdoErrorCode.CONTRACT_CALL_FAILURE);
        }
    }

    /**
     * 指定系列重新排序
     * @param series
     */
    public void reRank(String series) {
        // 获取该系列下所有NFT
        List<NftInfoDo> nftInfoList = new ArrayList<>();
        NftGroupDo selectGroupDo = new NftGroupDo();
        selectGroupDo.setSeries(series);
        List<NftGroupDo> nftGroupDoList = nftGroupMapper.selectByPrimaryKeySelectiveList(selectGroupDo);
        nftGroupDoList.forEach(nftGroupDo -> {
            NftInfoDo selectInfoDo = new NftInfoDo();
            selectInfoDo.setGroupId(nftGroupDo.getId());
            List<NftInfoDo> nftInfoDoList = nftInfoMapper.selectByPrimaryKeySelectiveList(selectInfoDo);
            nftInfoList.addAll(nftInfoDoList);
        });
        // 根据score排序
        MutableInt index = new MutableInt(0);
        MutableInt preRank = new MutableInt(0);
        MutableObject<BigDecimal> preScore = new MutableObject<>(new BigDecimal(0));
        nftInfoList.stream().sorted(Comparator.comparing(NftInfoDo::getScore).reversed()).forEach(nftInfoDo -> {
            if (nftInfoDo.getScore().equals(preScore.getValue())) {
                // 与前者分数相同，排名不变
                nftInfoDo.setRank(preRank.getValue());
                nftInfoDo.setUpdateTime(System.currentTimeMillis());
                nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                index.increment();
            } else {
                // 大于前者，排名为序列号
                nftInfoDo.setRank(index.incrementAndGet());
                nftInfoDo.setUpdateTime(System.currentTimeMillis());
                nftInfoMapper.updateByPrimaryKeySelective(nftInfoDo);
                // 排名变更
                preRank.setValue(index.getValue());
                preScore.setValue(nftInfoDo.getScore());
            }
        });
    }

    public boolean buyFromOffering() {
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("box_buy_from_offering")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(1))
                ))
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj("0x69f1e543a3bef043b63bed825fcd2cf6::KikoCat09::KikoCatBox"),
                        TypeArgsUtil.parseTypeObj("0x1::STC::STC")
                ))
                .build();
        return contractService.callFunction(market, scriptFunctionObj);
    }

    public boolean open_box(String address, String module) {
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(address)
                .moduleName(module)
                .functionName("open_box")
                .args(Lists.newArrayList())
                .build();
        return contractService.callFunction(market, scriptFunctionObj);
    }

    public boolean sellNFT() {
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("nft_sell")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU64ToBytes(6L),
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(100000000))
                ))
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj("0xd30b4de81d71c1793aa4db4763211e63::KikoCat06::KikoCatMeta"),
                        TypeArgsUtil.parseTypeObj("0xd30b4de81d71c1793aa4db4763211e63::KikoCat06::KikoCatBody"),
                        TypeArgsUtil.parseTypeObj("0x1::STC::STC")
                ))
                .build();
        return contractService.callFunction("0x142f352A24FEB989C65C1d48c4d884a9", scriptFunctionObj);
    }

    public boolean sellBox() {
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(scripts)
                .moduleName(scriptsModule)
                .functionName("box_sell")
                .args(Lists.newArrayList(
                        BcsSerializeHelper.serializeU128ToBytes(BigInteger.valueOf(200000000))
                ))
                .tyArgs(Lists.newArrayList(
                        TypeArgsUtil.parseTypeObj("0xd30b4de81d71c1793aa4db4763211e63::KikoCat06::KikoCatBox"),
                        TypeArgsUtil.parseTypeObj("0x1::STC::STC")
                ))
                .build();
        return contractService.callFunction("0x142f352A24FEB989C65C1d48c4d884a9", scriptFunctionObj);
    }

}
