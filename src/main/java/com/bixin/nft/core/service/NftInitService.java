package com.bixin.nft.core.service;

import com.bixin.ido.server.enums.NftGroupStatus;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.core.mapper.NftGroupMapper;
import com.bixin.nft.core.mapper.NftInfoMapper;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.starcoin.bean.ScriptFunctionObj;

import javax.annotation.Resource;
import java.util.List;

@Service
public class NftInitService {

    @Resource
    private NftGroupMapper nftGroupMapper;
    @Resource
    private NftInfoMapper nftInfoMapper;
    @Resource
    private ContractService contractService;

    public void createNft() {
        // init nft
        NftGroupDo selectNftGroupDo = new NftGroupDo();
        selectNftGroupDo.setStatus(NftGroupStatus.APPENDING.name());
        List<NftGroupDo> nftGroupDos = nftGroupMapper.selectByPrimaryKeySelectiveList(selectNftGroupDo);
        nftGroupDos.forEach(nftGroupDo -> {
            initNft();
        });
        // create nft
    }

    public boolean initNft() {
        String nftAddress = "0x290c7b35320a4dd26f651fd184373fe7";
        String path = "contract/nft/KikoCat01.mv";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(nftAddress)
                .moduleName("KikoCat01")
                .functionName("init")
                .tyArgs(Lists.newArrayList())
                .args(Lists.newArrayList())
                .build();
        return contractService.deployContract(nftAddress, path, scriptFunctionObj);
    }

    public boolean test() {
        String nftAddress = "0xf8af03dd08de49d81e4efd9e24c039cc";
        String path = "contract/MyCounter.mv";
        ScriptFunctionObj scriptFunctionObj = ScriptFunctionObj
                .builder()
                .moduleAddress(nftAddress)
                .moduleName("KikoCat01")
                .functionName("init")
                .tyArgs(Lists.newArrayList())
                .args(Lists.newArrayList())
                .build();
        return contractService.deployContract(nftAddress, path, scriptFunctionObj);
    }

}
