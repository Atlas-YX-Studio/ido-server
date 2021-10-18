package com.bixin.nft.controller;

import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.common.exception.IdoException;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.biz.NftImagesUploadBiz;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * @author zhangcheng
 * create   2021/9/27
 */
@Slf4j
@RestController
@RequestMapping(NFT_REQUEST_PATH_PREFIX + "/image")
public class NftImageController {

    @Resource
    private NftImagesUploadBiz nftImagesUploadBiz;
    @Resource
    private NftInfoService nftInfoService;
    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private RedisCache redisCache;
    @Resource
    private StarConfig starConfig;


    @GetMapping("/group/{id}")
    public void getGroupImage(@PathVariable("id") Long id, HttpServletResponse response) {
        String url = (String) redisCache.getValue(CommonConstant.IMAGE_GROUP_URL_PREFIX_KEY + id);
        if (StringUtils.isBlank(url)) {
            NftGroupDo nftGroupDo = nftGroupService.selectById(id);
            if (nftGroupDo == null) {
                throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
            }
            url = nftGroupDo.getNftTypeImageLink();
            if (StringUtils.isNoneEmpty(url) && url.startsWith(starConfig.getNft().getImagePrefix())) {
                if (!nftImagesUploadBiz.uploadImage(nftGroupDo)) {
                    log.error("nft图片上传失败, id:{}", id);
                    throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
                }
                url = nftGroupDo.getNftTypeImageLink();
            }
            redisCache.setValue(CommonConstant.IMAGE_GROUP_URL_PREFIX_KEY + id, url, 1, TimeUnit.HOURS);
        }
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
    }

    @GetMapping("/info/{id}")
    public void getInfoImage(@PathVariable("id") Long id, HttpServletResponse response) {
        String url = (String) redisCache.getValue(CommonConstant.IMAGE_INFO_URL_PREFIX_KEY + id);
        if (StringUtils.isBlank(url)) {
            NftInfoDo nftInfoDo = nftInfoService.selectById(id);
            if (nftInfoDo == null) {
                throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
            }
            url = nftInfoDo.getImageLink();
            if (StringUtils.isNoneEmpty(url) && url.startsWith(starConfig.getNft().getImagePrefix())) {
                if (!nftImagesUploadBiz.uploadImage(nftInfoDo)) {
                    log.error("nft图片上传失败, id:{}", id);
                    throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
                }
                url = nftInfoDo.getImageLink();
            }
            redisCache.setValue(CommonConstant.IMAGE_INFO_URL_PREFIX_KEY + id, url, 1, TimeUnit.HOURS);
        }
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
    }

    @GetMapping("/upload2cdn")
    public R asyncUploadImages(){
        nftImagesUploadBiz.asyncProcess();
        return R.success();
    }
}
