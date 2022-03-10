package com.bixin.nft.controller;

import com.bixin.common.response.R;
import com.bixin.nft.biz.NftContractBiz;
import com.bixin.nft.service.NftCompositeCardService;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.bixin.common.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/contract")
public class NftContractController {
    @Resource
    private NftContractBiz nftContractService;
    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private NftInfoService nftInfoService;
    @Resource
    private NftCompositeCardService nftCompositeCardService;

    private final static String SECRET_KEY = "766dF569970B22B29152eB326dad1b1E";

    /**
     * 初始化市场合约，发布NFT无需调用
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

    /**
     * 发布QQ秀NFT合约
     *
     * @param secretKey
     * @param groupId
     * @return
     */
    @GetMapping("/nft/createCompositeNFT")
    public R createCompositeNFT(@RequestParam String secretKey, @RequestParam Long groupId) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftCompositeCardService.createCompositeNFT(groupId);
        return R.success(true);
    }

    @GetMapping("/nft/createCompositeNFTV2")
    public R createCompositeNFTV2(@RequestParam String secretKey, @RequestParam Long groupId) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftCompositeCardService.createCompositeNFTV2(groupId);
        return R.success(true);
    }

    /**
     * 发布NFT普通合约
     *
     * @param secretKey
     * @return
     */
    @GetMapping("/nft/create")
    public R createNFT(@RequestParam(value = "secretKey") String secretKey) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.createNFT();
        return R.success(true);
    }

    /**
     * 发布不包含盲盒NFT
     *
     * @param secretKey
     * @param groupId
     * @return
     */
    @GetMapping("/nft/createNoBox")
    public R createNFTWithNoBox(@RequestParam String secretKey,
                                @RequestParam Long groupId) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.createNFTWithNoBox(groupId);
        return R.success(true);
    }

    /**
     * 发布批量NFT（当前仅用于测试，无线上合约对应）
     *
     * @param secretKey
     * @param groupId
     * @param batch
     * @param count
     * @param gas
     * @return
     */
    @GetMapping("/nft/createBatch")
    public R createBatchNFT(@RequestParam String secretKey,
                                @RequestParam Long groupId,
                                @RequestParam Integer batch,
                                @RequestParam Integer count,
                                @RequestParam Long gas) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.createBatchNFT(groupId, batch, count, gas);
        return R.success(true);
    }

    /**
     * 批量发送NFT，用于发送给运营
     *
     * @param secretKey
     * @param groupId
     * @param startNftId
     * @param endNftId
     * @param toAddress
     * @return
     */
    @GetMapping("/nft/transferNft")
    public R transferNft(@RequestParam String secretKey,
                                @RequestParam Long groupId,
                                @RequestParam Long startNftId,
                                @RequestParam Long endNftId,
                                @RequestParam String toAddress) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.transferNFT(groupId, startNftId, endNftId, toAddress);
        return R.success(true);
    }

    /**
     * 市场回购初始化，新增加系列时使用
     *
     * @param secretKey
     * @param groupId
     * @param payToken
     * @return
     */
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

    /**
     * 市场回购，新增加NFT时使用
     *
     * @param secretKey
     * @param infoId
     * @param payToken
     * @param price
     * @return
     */
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

    /**
     * NFT重新排序，一般无需调用
     *
     * @param secretKey
     * @param series
     * @return
     */
    @GetMapping("/nft/rank")
    public R rank(@RequestParam(value = "secretKey") String secretKey,
                  @RequestParam(value = "series") String series) {
        if (!SECRET_KEY.equals(secretKey)) {
            return R.failed("permission denied");
        }
        nftContractService.reRank(series);
        return R.success(true);
    }


}
