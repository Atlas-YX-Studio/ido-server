package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.nft.core.service.NftContractService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.math.BigInteger;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/contract")
public class NftContractController {
    @Resource
    private NftContractService nftContractService;

    private final static String SECRET_KEY = "766dF569970B22B29152eB326dad1b1E";

    /**
     *
     *
     * @return
     */
    @GetMapping("/group/list")
    public R groupList(@RequestParam(value = "secretKey") String secretKey,
                       @RequestParam(value = "creatorFee") String creatorFee,
                       @RequestParam(value = "platformFee") String platformFee) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed();
        }
        boolean success = nftContractService.initNFTMarket(new BigInteger(creatorFee), new BigInteger(platformFee));
        if (success) {
            return R.success(true);
        } else {
            return R.failed();
        }
    }

}
