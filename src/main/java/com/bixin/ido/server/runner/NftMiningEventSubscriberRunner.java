package com.bixin.ido.server.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bixin.ido.server.bean.dto.NftStakeEventDto;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.entity.NftMiningRecord;
import com.bixin.ido.server.entity.NftMiningUsers;
import com.bixin.ido.server.entity.NftStakingUsers;
import com.bixin.ido.server.enums.NFTMiningEventType;
import com.bixin.ido.server.service.NftMiningRecordService;
import com.bixin.ido.server.service.NftMiningUsersService;
import com.bixin.ido.server.service.NftStakingUsersService;
import com.bixin.ido.server.utils.ApplicationContextUtils;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.starcoin.api.StarcoinSubscriber;
import org.starcoin.bean.EventFilter;
import org.starcoin.bean.EventNotification;
import org.starcoin.bean.EventNotificationResult;
import org.web3j.protocol.websocket.WebSocketService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
public class NftMiningEventSubscriberRunner implements ApplicationRunner {

    @Resource
    private StarConfig idoStarConfig;
    @Resource
    private RedisCache redisCache;
    @Resource
    private NftMiningRecordService nftMiningRecordService;
    @Resource
    private NftMiningUsersService nftMiningUsersService;
    @Resource
    private NftStakingUsersService nftStakingUsersService;
    @Resource
    private NftGroupService nftGroupService;
    @Resource
    private NftInfoService nftInfoService;

    AtomicLong atomicSum = new AtomicLong(0);
    static final long initTime = 2000L;
    static final long initIntervalTime = 5000L;
    static final long maxIntervalTime = 60 * 1000L;
    //滤重过期时间 默认20分钟
    static final long duplicateExpiredTime = 20 * 60;

    static final String separator = "::";
    ObjectMapper mapper = new ObjectMapper();

    ThreadPoolExecutor poolExecutor;

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("NftMiningEventSubscriberRunner-", true));
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("NftMiningEventSubscriberRunner poolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("NftMiningEventSubscriberRunner InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        poolExecutor.execute(() -> process(args));
    }


    public void process(ApplicationArguments args) {
        String[] sourceArgs = 0 == args.getSourceArgs().length ? new String[]{""} : args.getSourceArgs();
        log.info("NftMiningEventSubscriberRunner start running [{}]", sourceArgs);
        try {

            WebSocketService service = new WebSocketService("ws://" + idoStarConfig.getSwap().getWebsocketHost() + ":" + idoStarConfig.getSwap().getWebsocketPort(), true);
            service.connect();
            StarcoinSubscriber subscriber = new StarcoinSubscriber(service);
            EventFilter eventFilter = new EventFilter(Collections.singletonList(idoStarConfig.getNft().getMarket()));
            Flowable<EventNotification> notificationFlowable = subscriber.newTxnSendRecvEventNotifications(eventFilter);
            notificationFlowable.blockingIterable().forEach(b -> {
                EventNotificationResult eventResult = b.getParams().getResult();
                JsonNode data = eventResult.getData();
                // 添加日志
                try {
                    log.info("NftMiningEventSubscriberRunner infos: {}", mapper.writeValueAsString(eventResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //去重
                if (duplicateEvent(eventResult)) {
                    log.info("NftMiningEventSubscriberRunner duplicate event data {}", eventResult);
                    return;
                }
                // 处理事件
                try {
                    handleNftStakeEvent(data, eventResult.getTypeTag(), eventResult.getEventSeqNumber());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            long duration = initTime + (atomicSum.incrementAndGet() - 1) * initIntervalTime;
            duration = Math.min(duration, maxIntervalTime);
            log.error("NftMiningEventSubscriberRunner run exception count {}, next retry {}, params {}",
                    atomicSum.get(), duration, idoStarConfig.getSwap(), e);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(duration));
            DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments("retry " + atomicSum.get());
            this.process(applicationArguments);
        }
    }

    public void handleNftStakeEvent(JsonNode data, String typeTag, String seqNumber) {
        log.info("NftMiningEventSubscriberRunner 质押NFT");
        NftStakeEventDto dto = mapper.convertValue(data, NftStakeEventDto.class);
        String meta = getMeta(typeTag);
        String body = getBody(typeTag);
        NftGroupDo nftGroupParam = NftGroupDo.builder().nftMeta(meta).nftBody(body).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParam);
        if (nftGroupDo == null) {
            log.error("NftMiningEventSubscriberRunner group 不存在，meta = {}, bogy = {}", meta, body);
            return;
        }
        NftInfoDo NftInfoParam = NftInfoDo.builder().groupId(nftGroupDo.getId()).nftId(dto.getNftId()).build();
        NftInfoDo nftInfoDo = nftInfoService.selectByObject(NftInfoParam);
        if (nftInfoDo == null) {
            log.error("NftMiningEventSubscriberRunner nftInfo 不存在，groupId = {}, nftId = {}", nftGroupDo.getId(), dto.getNftId());
            return;
        }
        // insert record
        String tagString = getEventName(typeTag);
        NftMiningRecord nftMiningRecord = NftMiningRecord.builder()
                .type(tagString)
                .groupId(nftGroupDo.getId())
                .seqNumber(seqNumber)
                .sender(dto.getSender())
                .infoId(nftInfoDo.getId())
                .order(dto.getOrder())
                .createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis())
                .build();
        nftMiningRecordService.save(nftMiningRecord);
        NftMiningEventSubscriberRunner runner = ApplicationContextUtils.getBean(this.getClass());
        if (NFTMiningEventType.STAKE_EVENT.getDesc().equals(tagString)) {
            // 质押事件
            runner.nftStake(nftMiningRecord);
        } else if (NFTMiningEventType.UNSTAKE_EVENT.getDesc().equals(tagString)) {
            // 解押事件
            runner.nftUnstake(nftMiningRecord);
        }
    }

    /**
     * 更新NFT质押表 更新质押分数
     * @param nftMiningRecord
     */
    @Transactional
    public void nftStake(NftMiningRecord nftMiningRecord) {
        LambdaQueryWrapper<NftStakingUsers> wrapper = Wrappers.<NftStakingUsers>lambdaQuery()
                .eq(NftStakingUsers::getAddress, nftMiningRecord.getSender())
                .eq(NftStakingUsers::getInfoId, nftMiningRecord.getInfoId());
        NftStakingUsers nftStakingUsers = nftStakingUsersService.getOne(wrapper, false);
        if (nftStakingUsers != null) {
            log.info("NftMiningEventSubscriberRunner nft质押已存在，recordId = {}，infoId = {}", nftMiningRecord.getId(), nftMiningRecord.getInfoId());
            return;
        }
        // 当前order是否存在其他nft
        wrapper = Wrappers.<NftStakingUsers>lambdaQuery()
                .eq(NftStakingUsers::getAddress, nftMiningRecord.getSender())
                .eq(NftStakingUsers::getOrder, nftMiningRecord.getOrder());
        nftStakingUsers = nftStakingUsersService.getOne(wrapper, false);
        if (nftStakingUsers != null) {
            // 解押已存在nft
            LambdaUpdateWrapper<NftMiningUsers> updateWrapper = Wrappers.<NftMiningUsers>lambdaUpdate()
                    .setSql("score = score - " + nftStakingUsers.getScore());
            nftMiningUsersService.update(updateWrapper);
            nftStakingUsersService.removeById(nftStakingUsers.getId());
        }
        // 质押nft
        nftStakingUsers = NftStakingUsers.builder()
                .address(nftMiningRecord.getSender())
                .infoId(nftMiningRecord.getInfoId())
                .order(nftMiningRecord.getOrder())
                .createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis())
                .build();
        nftStakingUsersService.save(nftStakingUsers);
        LambdaUpdateWrapper<NftMiningUsers> updateWrapper = Wrappers.<NftMiningUsers>lambdaUpdate()
                .setSql("score = score + " + nftStakingUsers.getScore());
        nftMiningUsersService.update(updateWrapper);
    }

    /**
     * 更新NFT质押表 更新质押分数
     * @param nftMiningRecord
     */
    @Transactional
    public void nftUnstake(NftMiningRecord nftMiningRecord) {
        LambdaQueryWrapper<NftStakingUsers> wrapper = Wrappers.<NftStakingUsers>lambdaQuery()
                .eq(NftStakingUsers::getAddress, nftMiningRecord.getSender())
                .eq(NftStakingUsers::getInfoId, nftMiningRecord.getInfoId());
        NftStakingUsers nftStakingUsers = nftStakingUsersService.getOne(wrapper, false);
        if (nftStakingUsers == null) {
            log.info("NftMiningEventSubscriberRunner nft已解押，recordId = {}，infoId = {}", nftMiningRecord.getId(), nftMiningRecord.getInfoId());
            return;
        }
        // 解押nft
        LambdaUpdateWrapper<NftMiningUsers> updateWrapper = Wrappers.<NftMiningUsers>lambdaUpdate()
                .setSql("score = score - " + nftStakingUsers.getScore());
        nftMiningUsersService.update(updateWrapper);
        nftStakingUsersService.removeById(nftStakingUsers.getId());
    }

    private String getEventName(String typeTag) {
        return typeTag.split(separator)[2].split("<")[0];
    }

    private String getMeta(String typeTag) {
        return typeTag.split("<")[1].split(">")[0].split(",")[0].trim();
    }

    private String getBody(String typeTag) {
        return typeTag.split("<")[1].split(">")[0].split(",")[1].trim();
    }


    /**
     * false 不存在
     * true 已存在
     *
     * @param eventResult
     * @return
     */
    public boolean duplicateEvent(EventNotificationResult eventResult) {
        String typeTag = eventResult.getTypeTag();
        String seqNumber = eventResult.getEventSeqNumber();
        String key = null;
        try {
            key = URLEncoder.encode(typeTag, "utf8") + seqNumber;
        } catch (UnsupportedEncodingException e) {
            log.error("NftMiningEventSubscriberRunner exception ", e);
        }
        log.info("NftMiningEventSubscriberRunner duplicate event redis key {}", key);
        if (Objects.isNull(key)) {
            return true;
        }
        Long now = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        return !redisCache.tryGetDistributedLock(
                key,
                UUID.randomUUID().toString().replaceAll("-", "") + now,
                duplicateExpiredTime
        );
    }

}
