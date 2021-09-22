package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.P;
import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.nft.bean.DO.NftMarketDo;
import com.bixin.nft.core.service.NftMarketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create   2021/9/18
 */
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/store")
public class NftStoreController {


    @Resource
    public NftMarketService nftMarketService;

    @GetMapping("/selling")
    public R getALlByPage(@RequestParam(value = "userAddress", defaultValue = "") String userAddress) {

        if (StringUtils.isEmpty(userAddress)) {
            return R.failed("parameter is invalid");
        }
        NftMarketDo marketDo = NftMarketDo.builder().owner(userAddress).build();
        List<NftMarketDo> nftMarketDos = nftMarketService.listByObject(marketDo);

        return R.success(nftMarketDos);
    }
}
