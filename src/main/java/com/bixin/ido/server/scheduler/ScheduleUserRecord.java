package com.bixin.ido.server.scheduler;

import com.alibaba.fastjson.JSON;
import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.bean.DO.IdoDxUserRecord;
import com.bixin.ido.server.config.IdoDxStarConfig;
import com.bixin.ido.server.service.IIdoDxProductService;
import com.bixin.ido.server.service.IIdoDxUserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create          2021-08-12 2:36 下午
 */
@Slf4j
@Component
public class ScheduleUserRecord {


    @Resource
    IIdoDxProductService idoDxProductService;
    @Resource
    IIdoDxUserRecordService idoDxUserRecordService;
    @Resource
    IdoDxStarConfig idoDxStarConfig;
    @Resource
    RestTemplate restTemplate;

    static final long intervalTIme = 24 * 60 * 60 * 1000;
    static final short maxTokenVersion = 3;

//    @Scheduled(cron = "5 0/10 * * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void updateProduct4Processing() {

        ResponseEntity<String> resp2 = getPostResp("0xb9c17e73f4d6eac8c88ba0f296fe4ce5", "0xd800a4813e2f3ef20f9f541004dbd189::DummyToken::DUMMY");


        //只查询项目结束最近一天的数据
        List<IdoDxProduct> finishProducts = idoDxProductService.getLastFinishProducts(intervalTIme);
        if (CollectionUtils.isEmpty(finishProducts)) {
            log.info("product is not finish");
        }
        finishProducts.forEach(p -> {
            String prdAddress = p.getAddress();
            IdoDxUserRecord dxUserRecord = IdoDxUserRecord.builder()
                    .prdAddress(prdAddress)
                    .tokenVersion(maxTokenVersion)
                    .build();
            //大于3次更新的记录不再更新
            List<IdoDxUserRecord> userRecords = idoDxUserRecordService.getUserRecord(dxUserRecord);
            if (CollectionUtils.isEmpty(userRecords)) {
                return;
            }
            userRecords.forEach(u -> {
                ResponseEntity<String> resp = getPostResp(u.getUserAddress(), prdAddress);
                if (resp.getStatusCode() == HttpStatus.OK) {

                } else {
                    log.error("schedule get resp {} {} {}", u.getPrdAddress(), prdAddress, JSON.toJSONString(resp));
                }

            });


        });


    }


    private ResponseEntity<String> getPostResp(String userAddress, String prdAddress) {
//        String[] addressArray = {userAddress, idoDxStarConfig.getModuleName() + "::Staking<" + prdAddress + ">"};
        List<String> addressArray = Arrays.asList(userAddress, idoDxStarConfig.getModuleName() + "::Staking<" + prdAddress + ">");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("id", "101");
        map.add("jsonrpc", "2.0");
        map.add("method", "contract.get_resource");
        map.add("params", addressArray);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Map> mapResponseEntity = restTemplate.postForEntity(idoDxStarConfig.getResourceUrl(), request, Map.class);
        return restTemplate.postForEntity(idoDxStarConfig.getResourceUrl(), request, String.class);

    }

}
