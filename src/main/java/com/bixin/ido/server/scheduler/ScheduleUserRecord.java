package com.bixin.ido.server.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.bean.DO.IdoDxUserRecord;
import com.bixin.ido.server.config.IdoDxStarConfig;
import com.bixin.ido.server.core.redis.RedisCache;
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
import java.util.UUID;

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
    @Resource
    RedisCache redisCache;

    static final String UPDATE_LOCK_KEY = "updateUserTokenAmountTask";
    static final Long EXPIRE_TIME = 4 * 60 * 1000L;
    //定时任务最大执行时间 大约 N 毫秒
    static final long scheduleMaxInterval = 4 * 60 * 1000;
    //只查询项目结束最近 N 天的数据 / 毫秒
    static final long lastIntervalTime = 24 * 60 * 60 * 1000;
    //大于3次更新的记录不再更新
    static final short maxTokenVersion = 2;
    //每次查询 N 条 用户记录
    static final long pageSize = 2000;

    //    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(cron = "20 0/5 * * * ?")
    public void updateUserTokenAmount() {

        Long startTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        String requestId = UUID.randomUUID().toString();

        redisCache.tryGetDistributedLock(
                UPDATE_LOCK_KEY,
                requestId,
                EXPIRE_TIME,
                () -> {
                    List<IdoDxProduct> finishProducts = idoDxProductService.getLastFinishProducts(lastIntervalTime);
                    if (CollectionUtils.isEmpty(finishProducts)) {
//                        log.info("ScheduleUserRecord get last finish products is empty ...");
                        return null;
                    }
                    finishProducts.forEach(p -> {
                        String prdAddress = p.getPledgeAddress();
                        IdoDxUserRecord dxUserRecord = IdoDxUserRecord.builder()
                                .prdAddress(prdAddress)
                                .tokenVersion(maxTokenVersion)
                                .build();
                        Long endTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
                        if ((startTime + scheduleMaxInterval) <= endTime) {
                            return;
                        }
                        long from = 0;
                        for (; ; ) {
                            List<IdoDxUserRecord> userRecords = idoDxUserRecordService.selectALlByPage(dxUserRecord, from, pageSize);
                            if (CollectionUtils.isEmpty(userRecords)) {
//                                log.info("ScheduleUserRecord get user records is empty {}", prdAddress);
                                break;
                            }
                            userRecords.forEach(u -> {
                                try {
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
                                        log.error("ScheduleUserRecord get remote result {}, {}, {}",
                                                prdAddress, u.getUserAddress(), JSON.toJSONString(resp));
                                    }
                                } catch (Exception e) {
                                    log.error("ScheduleUserRecord get remote chain exception {}", u, e);
                                }

                            });

                            Long userEndTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
                            if (userRecords.size() < pageSize
                                    || (startTime + scheduleMaxInterval) <= userEndTime) {
                                break;
                            }
                            from = from + pageSize;
                        }

                    });

                    return null;
                }
        );

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

        return restTemplate.postForEntity(idoDxStarConfig.getResourceUrl(), request, String.class);
    }

}
