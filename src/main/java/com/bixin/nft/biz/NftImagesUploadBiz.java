package com.bixin.nft.biz;

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
import org.springframework.web.client.RestTemplate;

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
    RestTemplate restTemplate;

    ThreadPoolExecutor poolExecutor;

    AtomicBoolean hasRun = new AtomicBoolean(false);
    static final long pageSize = 100;
    static final String imagePrefix = "https://imagedelivery.net";
    //    static final String imageBasePath = "/data/bixin-ido-server/images/nft/";
    static final String imageBasePath = "/Users/bixin/Documents/pics/nft/";


    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("nftImageUpload-", true));
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

    private void run() {
        if (hasRun.compareAndSet(false, true)) {
            long pageNum = 1;
            List<NftInfoDo> nftInfoDos = null;
            for (; ; ) {
                try {
                    nftInfoDos = nftInfoService.selectByPage(pageNum, pageSize, "id", "asc");
                    if (CollectionUtils.isEmpty(nftInfoDos)) {
                        log.error("nft_cloudflare info data is empty");
                        break;
                    }
                    nftInfoDos.forEach(p -> {
                        String imageLink = p.getImageLink();
                        if (StringUtils.isNoneEmpty(imageLink) && imageLink.startsWith(imagePrefix)) {
                            log.info("nft_cloudflare info of image link is upload id:{}", p.getId());
                            return;
                        }

                        MutablePair<String, String> pair = splitImage(p.getImageData());
                        String imageSuffix = pair.getLeft();
                        String imageData = pair.getRight();
                        if (StringUtils.isEmpty(imageSuffix) || StringUtils.isEmpty(imageData)) {
                            log.error("nft_cloudflare info convert image data error id:{}", p.getId());
                            return;
                        }
                        String imagePath = imageBasePath + p.getId()
                                + "_" + LocalDateTimeUtil.getMilliByTime(LocalDateTime.now())
                                + "." + imageSuffix;
                        FileOperateUtil.newFolder(imageBasePath);
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
                                return;
                            }
                            Map map = JacksonUtil.readValue(builder.toString(), Map.class);
                            if (CollectionUtils.isEmpty(map)) {
                                log.error("nft_cloudflare info get upload result is error {}", builder.toString());
                                return;
                            }
                            boolean success = (boolean) map.get("success");
                            if (success) {
                                Map result = (Map) map.get("result");
                                ArrayList<String> variants = (ArrayList<String>) result.get("variants");
                                String newImageLink = variants.get(0);
                                NftInfoDo newNftInfo = NftInfoDo.builder()
                                        .id(p.getId())
                                        .imageLink(newImageLink)
                                        .build();
                                nftInfoService.update(newNftInfo);
                            }

                        } catch (IOException e) {
                            log.error("", e);
                        }

                    });

                } catch (Exception e) {
                    log.error("nft_cloudflare exception", e);
                }
                if (CollectionUtils.isEmpty(nftInfoDos) || nftInfoDos.size() < pageSize) {
                    break;
                }
                pageNum++;

            }
            FileOperateUtil.delAllFile(imageBasePath);
        }

    }

    private void upload(String filePath) {


    }

    private MutablePair<String, String> splitImage(String data) {
        String regex = "^data:image\\/([A-Za-z]+);base64,(.*)";
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
