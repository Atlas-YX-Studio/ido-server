package com.bixin.nft.biz;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.utils.Base64Util;
import com.bixin.ido.server.utils.FileOperateUtil;
import com.bixin.ido.server.utils.JacksonUtil;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.core.service.NftInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangcheng
 * create   2021/9/27
 */
@Slf4j
@Service
public class NftImagesUploadBiz {

    @Resource
    NftInfoService nftInfoService;
    @Resource
    private StarConfig starConfig;

    ThreadPoolExecutor poolExecutor;

    AtomicBoolean hasRun = new AtomicBoolean(false);
    static final long pageSize = 100;

    private String imagePrefix;
    private String imageBasePath;

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("nftImageUpload-", true));
        imagePrefix = starConfig.getNft().getImagePrefix();
        imageBasePath = starConfig.getNft().getImageBasePath();
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("NftImagesUploadBiz poolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("NftImagesUploadBiz InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }


    public void asyncProcess() {
        poolExecutor.execute(this::run);
    }

    public void run() {
        if (!hasRun.compareAndSet(false, true)) {
            log.warn("nft_cloudflare upload images is running ...");
            return;
        }
        log.info("nft_cloudflare start upload images ...");
        FileOperateUtil.newFolder(imageBasePath);

        long pageNum = 1;
        List<NftInfoDo> nftInfoDos = null;
        for (; ; ) {
            try {
                nftInfoDos = nftInfoService.selectByPage(false, pageNum, pageSize, "id", "asc");
                if (CollectionUtils.isEmpty(nftInfoDos)) {
                    log.error("nft_cloudflare info data is empty");
                    break;
                }
                nftInfoDos.forEach(this::uploadImage);
            } catch (Exception e) {
                log.error("nft_cloudflare exception", e);
            }
            if (CollectionUtils.isEmpty(nftInfoDos) || nftInfoDos.size() < pageSize) {
                break;
            }
            pageNum++;
        }

        hasRun.set(false);
        FileOperateUtil.delAllFile(imageBasePath);
    }

    /**
     * 上传图片并更新image_link
     * @param nftInfoDo
     * @return
     */
    public boolean uploadImage(NftInfoDo nftInfoDo) {
        String imageLink = nftInfoDo.getImageLink();
        if (StringUtils.isNoneEmpty(imageLink) && imageLink.startsWith(imagePrefix)) {
            log.info("nft_cloudflare info of image link is upload id:{}", nftInfoDo.getId());
            return true;
        }

        MutablePair<String, String> pair = splitImage(nftInfoDo.getImageData());
        String imageSuffix = pair.getLeft();
        String imageData = pair.getRight();
        if (StringUtils.isEmpty(imageSuffix) || StringUtils.isEmpty(imageData)) {
            log.error("nft_cloudflare info convert image data error id:{}", nftInfoDo.getId());
            return false;
        }
        String imagePath = imageBasePath + nftInfoDo.getId()
                + "_" + LocalDateTimeUtil.getMilliByTime(LocalDateTime.now())
                + "." + imageSuffix;
        Base64Util.Base64ToImage(imageData, imagePath);

        String[] cmds = new String[]{"curl", "-X", "POST", "-F", "file=@" + imagePath, "-H", "Authorization: Bearer 5_crM9D0ZQEjTmJm6P_J9CjAgxU06AKt0ZB-xeAb", "https://api.cloudflare.com/client/v4/accounts/06d48301c855502bf143d5a4c5d3a982/images/v1"};
        try {
            ProcessBuilder process = new ProcessBuilder(cmds);
            Process doStart = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(doStart.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            if (StringUtils.isEmpty(builder.toString())) {
                log.error("nft_cloudflare info upload result is empty");
                return false;
            }
            Map map = JacksonUtil.readValue(builder.toString(), Map.class);
            if (CollectionUtils.isEmpty(map)) {
                log.error("nft_cloudflare info get upload result is error {}", builder.toString());
                return false;
            }
            boolean success = (boolean) map.get("success");
            if (success) {
                Map result = (Map) map.get("result");
                ArrayList<String> variants = (ArrayList<String>) result.get("variants");
                String newImageLink = variants.get(0);
                nftInfoDo.setImageLink(newImageLink);
                nftInfoService.update(nftInfoDo);
            }
            FileOperateUtil.delFile(imagePath);
            return true;
        } catch (IOException e) {
            log.error("nft_cloudflare upload image exceptionß", e);
            return false;
        }
    }

    private MutablePair<String, String> splitImage(String data) {
        String regex = "^data:img\\/([A-Za-z]+);base64,(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            if (matcher.groupCount() == 2) {
                return new MutablePair<>(matcher.group(1), matcher.group(2));
            }
        }
        return new MutablePair<>("", "");
    }

}
