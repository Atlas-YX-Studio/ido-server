package com.bixin.ido.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.bixin.common.utils.BigDecimalUtil;
import com.bixin.ido.core.client.ChainClientHelper;
import com.bixin.ido.bean.DO.IdoDxProduct;
import com.bixin.ido.bean.DO.IdoDxUserRecord;
import com.bixin.core.redis.RedisCache;
import com.bixin.ido.service.IDxProductService;
import com.bixin.ido.service.IDxUserRecordService;
import com.bixin.common.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private IDxProductService idoDxProductService;
    @Resource
    private IDxUserRecordService idoDxUserRecordService;
    @Resource
    private ChainClientHelper chainClientHelper;
    @Resource
    private RedisCache redisCache;

    static final String UPDATE_LOCK_KEY = "updateUserTokenAmountTask";
    static final Long EXPIRE_TIME = 4 * 60 * 1000L;
    //如果未获取到锁，则持续等待【xx】时间ms后不在尝试获取
    static final Long LOCK_EXPIRE_TIME = 0L;
    //定时任务最大执行时间 大约 N 毫秒
    static final long scheduleMaxInterval = 4 * 60 * 1000;
    //只查询项目结束最近 N 天的数据 / 毫秒
    static final long lastIntervalTime = 24 * 60 * 60 * 1000;
    //大于3次更新的记录不再更新
    static final short maxTokenVersion = 100;
    //每次查询 N 条 用户记录
    static final long pageSize = 2000;

    //    @Scheduled(cron = "0/15 * * * * ?")
    @Scheduled(cron = "20 0/5 * * * ?")
    public void updateUserTokenAmount() {

        Long startTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        String requestId = UUID.randomUUID().toString();

        redisCache.tryGetDistributedLock(
                UPDATE_LOCK_KEY,
                requestId,
                EXPIRE_TIME,
                LOCK_EXPIRE_TIME,
                () -> {
                    List<IdoDxProduct> finishProducts = idoDxProductService.getLastFinishProducts(lastIntervalTime);
                    if (CollectionUtils.isEmpty(finishProducts)) {
                        log.info("ScheduleUserRecord get last finish products is empty ...");
                        return null;
                    }
                    finishProducts.forEach(p -> {
                        String prdAddress = p.getAssignAddress();
                        IdoDxUserRecord dxUserRecord = IdoDxUserRecord.builder()
                                .prdAddress(prdAddress)
                                .tokenVersion(maxTokenVersion)
                                .build();
                        Long endTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
                        if ((startTime + scheduleMaxInterval) <= endTime) {
                            log.info("ScheduleUserRecordeUserRecord is run expired {}", p);
                            return;
                        }
                        long from = 0;
                        for (; ; ) {
                            List<IdoDxUserRecord> userRecords = idoDxUserRecordService.selectALlByPage(dxUserRecord, from, pageSize);
                            if (CollectionUtils.isEmpty(userRecords)) {
                                log.info("ScheduleUserRecordeUserRecord get user records is empty {}", prdAddress);
                                break;
                            }
                            userRecords.forEach(u -> {
                                try {
                                    MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> triple = chainClientHelper.getStakingResp(u.getUserAddress(), p);
                                    ResponseEntity<String> resp = triple.getLeft();
                                    String url = triple.getMiddle();
                                    HttpEntity<Map<String, Object>> httpEntity = triple.getRight();

                                    if (resp.getStatusCode() == HttpStatus.OK) {
                                        Map<String, Object> respMap = JSON.parseObject(resp.getBody(), new TypeReference<>() {
                                        });
                                        if (!respMap.containsKey("result")) {
                                            log.error("ScheduleUserRecord result is empty {}, {}, {}",
                                                    JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                                            return;
                                        }
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> result = (Map<String, Object>) respMap.get("result");
                                        if (!result.containsKey("value")) {
                                            log.error("ScheduleUserRecord result value is empty {}, {}, {}",
                                                    JSON.toJSONString(resp), url, JSON.toJSONString(httpEntity));
                                            return;
                                        }
                                        @SuppressWarnings("unchecked")
                                        List<JSONArray> values = (List<JSONArray>) result.get("value");
                                        values.stream().forEach(rs -> {
                                            Object[] stcResult = rs.toArray();
                                            if ("staking_token_amount".equalsIgnoreCase(String.valueOf(stcResult[0]))) {
                                                @SuppressWarnings("unchecked")
                                                Map<String, Object> pledgeMap = (Map<String, Object>) stcResult[1];
                                                String tokenAmount = (String) pledgeMap.get("U128");

                                                u.setTokenVersion((short) (u.getTokenVersion() + 1));
                                                // todo 币种精度
                                                u.setTokenAmount(BigDecimalUtil.removePrecision(new BigDecimal(tokenAmount), 9));
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
                                log.info("ScheduleUserRecordeUserRecord is run expired or end {}", p);
                                break;
                            }
                            from = from + pageSize;
                        }

                    });

                    return null;
                }
        );

    }


}
