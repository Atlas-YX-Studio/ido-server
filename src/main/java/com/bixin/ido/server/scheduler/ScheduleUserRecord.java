package com.bixin.ido.server.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.bean.DO.IdoDxUserRecord;
import com.bixin.ido.server.config.IdoDxStarConfig;
import com.bixin.ido.server.service.IIdoDxProductService;
import com.bixin.ido.server.service.IIdoDxUserRecordService;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    //    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "5 0/10 * * * ?")
    public void updateProduct4Processing() {
        //只查询项目结束最近一天的数据
        List<IdoDxProduct> finishProducts = idoDxProductService.getLastFinishProducts(intervalTIme);
        if (CollectionUtils.isEmpty(finishProducts)) {
            log.info("ScheduleUserRecord get last finish products is empty ...");
        }
        finishProducts.forEach(p -> {
            String prdAddress = p.getPledgeAddress();
            IdoDxUserRecord dxUserRecord = IdoDxUserRecord.builder()
                    .prdAddress(prdAddress)
                    .tokenVersion(maxTokenVersion)
                    .build();
            //大于3次更新的记录不再更新
            List<IdoDxUserRecord> userRecords = idoDxUserRecordService.getUserRecord(dxUserRecord);
            if (CollectionUtils.isEmpty(userRecords)) {
                log.info("ScheduleUserRecord get user records is empty {}", prdAddress);
                return;
            }
            userRecords.forEach(u -> {
                ResponseEntity<String> resp = getPostResp(u.getUserAddress(), prdAddress);
                if (resp.getStatusCode() == HttpStatus.OK) {
                    Map<String, Object> respMap = JSON.parseObject(resp.getBody(), new TypeReference<>() {
                    });
                    @SuppressWarnings("unchecked")
                    Map<String, Object> result = (Map<String, Object>) respMap.get("result");
                    @SuppressWarnings("unchecked")
                    List<JSONArray> values = (List<JSONArray>) result.get("value");
                    values.stream().forEach(rs -> {
                        Object[] stcResult = rs.toArray();
                        if ("stc_staking_amount".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> pledgeMap = (Map<String, Object>) stcResult[1];
                            long tokenAmount = (long) pledgeMap.get("U128");

                            u.setTokenVersion((short) (u.getTokenVersion() + 1));
                            u.setTokenAmount(BigDecimal.valueOf(tokenAmount));
                            u.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));

                            idoDxUserRecordService.updateUserRecord(u);
                        }
                    });

                } else {
                    log.error("ScheduleUserRecord get remote resp {}, {}, {}",
                            prdAddress, u.getUserAddress(), JSON.toJSONString(resp));
                }
            });
        });
    }


    private ResponseEntity<String> getPostResp(String userAddress, String prdAddress) {
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
