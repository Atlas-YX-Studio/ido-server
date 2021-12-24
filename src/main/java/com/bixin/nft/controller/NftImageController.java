package com.bixin.nft.controller;

import com.bixin.common.response.R;
import com.bixin.common.code.IdoErrorCode;
import com.bixin.common.exception.IdoException;
import com.bixin.common.config.StarConfig;
import com.bixin.common.constants.CommonConstant;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.biz.NftImagesUploadBiz;
import com.bixin.nft.service.NftGroupService;
import com.bixin.nft.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.bixin.common.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

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
    public R getGroupImage(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        String url = (String) redisCache.getValue(CommonConstant.IMAGE_GROUP_URL_PREFIX_KEY + id);
        if (StringUtils.isBlank(url)) {
            NftGroupDo nftGroupDo = nftGroupService.selectById(id);
            if (nftGroupDo == null) {
                throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
            }
            url = nftGroupDo.getNftTypeImageLink();
            if (StringUtils.isBlank(url)) {
                throw new IdoException(IdoErrorCode.IMAGE_UPLOAD_FAILURE);
            }
            redisCache.setValue(CommonConstant.IMAGE_GROUP_URL_PREFIX_KEY + id, url, 1, TimeUnit.HOURS);
        }
        try {
            if (StringUtils.equalsIgnoreCase(request.getHeader(CommonConstant.HTTP_X_REQUESTED_WITH),
                    CommonConstant.HTTP_AJAX_REQUEST_HEADER)) {
                return R.success(url);
            }
            response.sendRedirect(url);
            return R.success();
        } catch (IOException e) {
            throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
        }
    }

    @GetMapping("/info/{id}.json")
    public String getImage() {

        return "{\n" +
                "    \"attributes\": [\n" +
                "        {\n" +
                "            \"trait_type\": \"base\",\n" +
                "            \"value\": \"narwhal\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"trait_type\": \"eyes\",\n" +
                "            \"value\": \"sleepy\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"trait_type\": \"mouth\",\n" +
                "            \"value\": \"cute\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"trait_type\": \"level\",\n" +
                "            \"value\": 4\n" +
                "        },\n" +
                "        {\n" +
                "            \"trait_type\": \"stamina\",\n" +
                "            \"value\": 90.2\n" +
                "        },\n" +
                "        {\n" +
                "            \"trait_type\": \"personality\",\n" +
                "            \"value\": \"boring\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"display_type\": \"boost_number\",\n" +
                "            \"trait_type\": \"aqua_power\",\n" +
                "            \"value\": 10\n" +
                "        },\n" +
                "        {\n" +
                "            \"display_type\": \"boost_percentage\",\n" +
                "            \"trait_type\": \"stamina_increase\",\n" +
                "            \"value\": 5\n" +
                "        },\n" +
                "        {\n" +
                "            \"display_type\": \"number\",\n" +
                "            \"trait_type\": \"generation\",\n" +
                "            \"value\": 1\n" +
                "        }\n" +
                "    ],\n" +
                "    \"description\": \"Friendly OpenSea Creature that enjoys long swims in the ocean.\",\n" +
                "    \"external_url\": \"https://example.com/?token_id=3\",\n" +
                "    \"image\": \"https://storage.googleapis.com/opensea-prod.appspot.com/creature/3.png\",\n" +
                "    \"name\": \"Dave Starbelly\"\n" +
                "}";
    }

    @GetMapping("/info/{id}")
    public R getInfoImage(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        String url = (String) redisCache.getValue(CommonConstant.IMAGE_INFO_URL_PREFIX_KEY + id);
        if (StringUtils.isBlank(url)) {
            NftInfoDo nftInfoDo = nftInfoService.selectById(id);
            if (nftInfoDo == null) {
                throw new IdoException(IdoErrorCode.DATA_NOT_EXIST);
            }
            url = nftInfoDo.getImageLink();
            if (StringUtils.isBlank(url)) {
                throw new IdoException(IdoErrorCode.IMAGE_UPLOAD_FAILURE);
            }
            redisCache.setValue(CommonConstant.IMAGE_INFO_URL_PREFIX_KEY + id, url, 1, TimeUnit.HOURS);
        }
        try {
            if (StringUtils.equalsIgnoreCase(request.getHeader(CommonConstant.HTTP_X_REQUESTED_WITH),
                    CommonConstant.HTTP_AJAX_REQUEST_HEADER)) {
                return R.success(url);
            }
            response.sendRedirect(url);
            return R.success();
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
