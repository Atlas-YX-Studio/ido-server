package com.bixin.nft.scheduler;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.utils.JacksonUtil;
import com.bixin.nft.bean.dto.ChainResourceDto;
import com.bixin.nft.core.service.ContractService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangcheng
 * create   2021/9/17
 */
@Slf4j
@Component
public class ScheduleNftMarket {

    @Resource
    ContractService contractService;
    @Resource
    private StarConfig starConfig;

    ObjectMapper mapper = new ObjectMapper();

    static final String separator = "::";
    static final String boxSuffix = separator + "NFTMarket" + separator + "BoxSelling";
    static final String nftSuffix = separator + "NFTMarket" + separator + "NFTSelling";


    //    @Scheduled(cron = "20 0/5 * * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void getNftMarketList() {
        String resource = contractService.getResource(starConfig.getNft().getMarket());
        ChainResourceDto chainResourceDto = JacksonUtil.readValue(resource, new TypeReference<ChainResourceDto>() {
        });
        if (Objects.isNull(chainResourceDto) || Objects.isNull(chainResourceDto.getResult())
                || Objects.isNull(chainResourceDto.getResult().getResources())) {
            log.warn("ScheduleNftMarket get chain resource is empty {}", chainResourceDto);
        }

        String nftKeyPrefix = starConfig.getNft().getMarket() + nftSuffix;
        String boxKeyPrefix = starConfig.getNft().getMarket() + boxSuffix;

        chainResourceDto.getResult().getResources().forEach((key, value) -> {
            MutableTriple<String, String, String> triple = getTokens(key);
            if (key.startsWith(nftKeyPrefix)) {

            } else if (key.startsWith(boxKeyPrefix)) {

            }

        });


    }


    private MutableTriple<String, String, String> getTokens(String tokens) {
        String regex = "\\<(.*)\\>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tokens);
        while (matcher.find()) {
            String[] tokenArray = matcher.group(0).replaceAll("<|>", "").split(",");
            if (tokenArray.length == 3) {
                return new MutableTriple<>(tokenArray[0], tokenArray[1], tokenArray[2]);
            } else if (tokenArray.length == 2) {
                return new MutableTriple<>(tokenArray[0], tokenArray[1], "");
            }
        }
        return new MutableTriple<>("", "", "");
    }


}
