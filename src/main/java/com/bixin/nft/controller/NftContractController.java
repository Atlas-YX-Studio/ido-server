package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.nft.core.service.NftContractService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/contract")
public class NftContractController {
    @Resource
    private NftContractService nftContractService;

    private final static String SECRET_KEY = "766dF569970B22B29152eB326dad1b1E";

    /**
     * 初始化市场合约
     *
     * @return
     */
    @GetMapping("/market/init")
    public R initNFTMarket(@RequestParam(value = "secretKey") String secretKey,
                       @RequestParam(value = "creatorFee") String creatorFee,
                       @RequestParam(value = "platformFee") String platformFee) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.initNFTMarket(new BigInteger(creatorFee), new BigInteger(platformFee));
        return R.success(true);
    }

    @GetMapping("/nft/create")
    public R createNFT(@RequestParam(value = "secretKey") String secretKey) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.createNFT();
        return R.success(true);
    }

    @GetMapping("/nft/buyback-init")
    public R initBuyBackNFT(@RequestParam(value = "secretKey") String secretKey,
                            @RequestParam(value = "groupId") Long groupId,
                            @RequestParam(value = "payToken") String payToken) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.initBuyBackNFT(groupId, payToken);
        return R.success(true);
    }

    @GetMapping("/nft/buyback")
    public R buyBackNFT(@RequestParam(value = "secretKey") String secretKey,
                        @RequestParam(value = "infoId") Long infoId,
                        @RequestParam(value = "payToken") String payToken,
                        @RequestParam(value = "price") String price) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.buyBackNFT(infoId, payToken, new BigDecimal(price));
        return R.success(true);
    }

}
