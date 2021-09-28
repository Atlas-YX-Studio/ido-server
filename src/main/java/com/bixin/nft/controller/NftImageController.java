package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.nft.biz.NftImagesUploadBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create   2021/9/27
 */
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/image")
public class NftImageController {

    @Resource
    NftImagesUploadBiz nftImagesUploadBiz;

    @GetMapping("/upload2cdn")
    public R asyncUploadImages(){
        nftImagesUploadBiz.asyncProcess();
        return R.success();
    }
}
