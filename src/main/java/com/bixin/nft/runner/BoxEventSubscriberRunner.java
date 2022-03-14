package com.bixin.nft.runner;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bixin.common.config.StarConfig;
import com.bixin.common.factory.NamedThreadFactory;
import com.bixin.common.utils.LocalDateTimeUtil;
import com.bixin.core.redis.RedisCache;
import com.bixin.nft.bean.DO.NftCompositeCard;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.dto.BoxOpenEventDto;
import com.bixin.nft.bean.dto.NFTResolveEventDto;
import com.bixin.nft.bean.dto.NftMintEventtDto;
import com.bixin.nft.common.enums.CardState;
import com.bixin.nft.common.enums.NftEventType;
import com.bixin.nft.common.enums.NftInfoState;
import com.bixin.nft.common.enums.NftType;
import com.bixin.nft.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
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
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
public class BoxEventSubscriberRunner implements ApplicationRunner {

    @Resource
    StarConfig idoStarConfig;
    @Resource
    RedisCache redisCache;
    @Resource
    NftEventService nftEventService;
    @Resource
    NftGroupService nftGroupService;
    @Resource
    NftInfoService nftInfoService;
    @Resource
    NftCompositeCardService nftCompositeCardService;
    @Resource
    NftMetareverseService nftMetareverseService;


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
                new LinkedBlockingQueue<>(), new NamedThreadFactory("BoxEventSubscriberRunner-", true));
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("BoxEventSubscriberRunner poolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("BoxEventSubscriberRunner InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        poolExecutor.execute(() -> process(args));
    }

    public void process(ApplicationArguments args) {
        String[] sourceArgs = 0 == args.getSourceArgs().length ? new String[]{""} : args.getSourceArgs();
        log.info("BoxEventSubscriberRunner start running [{}]", sourceArgs);
        try {

            WebSocketService service = new WebSocketService("ws://" + idoStarConfig.getNft().getWebsocketHost() + ":" + idoStarConfig.getNft().getWebsocketPort(), true);
            service.connect();
            StarcoinSubscriber subscriber = new StarcoinSubscriber(service);
            EventFilter eventFilter = new EventFilter(Collections.singletonList(idoStarConfig.getNft().getCatadd()));
            Flowable<EventNotification> notificationFlowable = subscriber.newTxnSendRecvEventNotifications(eventFilter);
            notificationFlowable.blockingIterable().forEach(b -> {
                EventNotificationResult eventResult = b.getParams().getResult();
                JsonNode data = eventResult.getData();
                // 添加日志
                try {
                    log.info("BoxEventSubscriberRunner infos: {}", mapper.writeValueAsString(eventResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //去重
                if (duplicateEvent(eventResult)) {
                    log.info("BoxEventSubscriberRunner duplicate event data {}", eventResult);
                    return;
                }
                String tagString = getEventName(eventResult.getTypeTag());
                NftEventDo nftEventDo = null;
                // 开盲盒
                if (NftEventType.BOX_OPEN_EVENT.getDesc().equals(tagString)) {
                    log.info("BoxEventSubscriberRunner 铸造");
                    BoxOpenEventDto dto = mapper.convertValue(data, BoxOpenEventDto.class);
                    nftEventDo = BoxOpenEventDto.of(dto);
                }
                // 铸造
                if (NftEventType.NFT_MINT_EVENT.getDesc().equals(tagString)) {
                    log.info("BoxEventSubscriberRunner 铸造");
                    NftMintEventtDto dto = mapper.convertValue(data, NftMintEventtDto.class);
                    nftEventDo = NftMintEventtDto.of(dto);
                }
                // 分解
                if (NftEventType.NFT_RESOLVE_EVENT.getDesc().equals(tagString)) {
                    log.info("BoxEventSubscriberRunner 分解");
                    NFTResolveEventDto dto = mapper.convertValue(data, NFTResolveEventDto.class);
                    resolveNftEvent(dto, eventResult.getTypeTag(), tagString);
                }
                if (!ObjectUtils.isEmpty(nftEventDo)) {
                    setGroupIdAndInfoId(nftEventDo, eventResult.getTypeTag(), tagString);
                    nftEventService.insert(nftEventDo);
                } else {
                    log.error("BoxEventSubscriberRunner nftEventDo 为空");
                }
            });

        } catch (Exception e) {
            long duration = initTime + (atomicSum.incrementAndGet() - 1) * initIntervalTime;
            duration = Math.min(duration, maxIntervalTime);
            log.error("BoxEventSubscriberRunner run exception count {}, next retry {}, params {}",
                    atomicSum.get(), duration, idoStarConfig.getSwap(), e);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(duration));
            DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments("retry " + atomicSum.get());
            this.process(applicationArguments);
        }
    }

    private void resolveNftEvent(NFTResolveEventDto dto, String typeTag, String eventType) {
        String meta = getMeta(typeTag);
        String body = getBody(typeTag);
        NftGroupDo nftGroupParm = NftGroupDo.builder().nftMeta(meta).nftBody(body).build();
        NftGroupDo nftGroupDo = nftGroupService.selectMulByObject(nftGroupParm).get(0);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            log.error("BoxEventSubscriberRunner group 不存在，meta = {}, bogy = {}", meta, body);
            return;
        }
        NftInfoDo infoParam = NftInfoDo.builder()
                .nftId(dto.getId())
                .owner(dto.getOwner())
                .groupId(nftGroupDo.getId())
                .type(NftType.COMPOSITE_CARD.getType())
                .build();
        log.info("BoxEventSubscriberRunner get resolve nft param {}", infoParam);
        NftInfoDo infoDo = nftInfoService.selectByObject(infoParam);
        if (Objects.isNull(infoDo)) {
            log.error("BoxEventSubscriberRunner get resolve nft is empty {}", infoParam);
            return;
        }
        log.info("BoxEventSubscriberRunner get resolve nft info {},{}", infoDo.getId(), infoDo.getGroupId());

        List<NftCompositeCard> compositeCards = nftMetareverseService.getCompositeCard(infoDo.getId());
        if (CollectionUtils.isEmpty(compositeCards)) {
            log.error("BoxEventSubscriberRunner get resolve card is empty {}", infoDo.getId());
            return;
        }
        log.info("BoxEventSubscriberRunner get resolve card info {},{}", infoDo.getId(), compositeCards);

        Long currentTime = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        nftInfoService.update(NftInfoDo.builder()
                .id(infoDo.getId())
                .created(false)
                .state(NftInfoState.INVALID.getDesc())
                .updateTime(currentTime)
                .build());

        NftCompositeCard card = compositeCards.get(compositeCards.size() - 1);
        nftCompositeCardService.update(
                NftCompositeCard.builder()
                        .state(CardState.card_split_success.getCode())
                        .build(),
                Wrappers.<NftCompositeCard>lambdaUpdate()
                        .eq(NftCompositeCard::getInfoId, card.getId())
        );
    }

    // 设置 infoId 和 groupId
    private void setGroupIdAndInfoId(NftEventDo nftEventDo, String typeTag, String eventType) {
        String meta = getMeta(typeTag);
        String body = getBody(typeTag);
        NftGroupDo nftGroupParm = NftGroupDo.builder().nftMeta(meta).nftBody(body).build();
        log.info("BoxEventSubscriberRunner group info，meta = {}, bogy = {}, {}", meta, body, eventType);

        NftGroupDo nftGroupDo = nftGroupService.selectMulByObject(nftGroupParm).get(0);
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            log.error("BoxEventSubscriberRunner group 不存在，meta = {}, bogy = {}, {}", meta, body, eventType);
            return;
        }
        NftInfoDo nftInfoParm = NftInfoDo.builder()
                .groupId(nftGroupDo.getId())
                .nftId(nftEventDo.getNftId()).build();
        log.info("BoxEventSubscriberRunner nftInfo :{},{}", eventType, nftInfoParm);

        NftInfoDo nftInfoDo = nftInfoService.selectByObject(nftInfoParm);
        if (ObjectUtils.isEmpty(nftInfoDo)) {
            log.error("BoxEventSubscriberRunner nftInfo 不存在，groupId = {}, nftId = {}", nftGroupDo.getId(), nftEventDo.getNftId());
            return;
        }
        nftEventDo.setInfoId(nftInfoDo.getId());
        nftEventDo.setGroupId(nftGroupDo.getId());

        try {
            if (NftEventType.BOX_OPEN_EVENT.getDesc().equals(eventType)) {
                nftInfoDo.setOwner(nftEventDo.getCreator());
                nftInfoDo.setUpdateTime(System.currentTimeMillis());
                nftInfoService.update(nftInfoDo);
            }
        } catch (Exception e) {
            log.error("BoxEventSubscriberRunner-setinfo-Ower 发生异常 :", e);
        }

        try {
            if (NftEventType.NFT_MINT_EVENT.getDesc().equals(eventType)) {
                log.info("BoxEventSubscriberRunner nftCardInfo :{},{},{}", eventType, nftInfoParm, nftInfoDo.getId());
                nftCompositeCardService.update(
                        NftCompositeCard.builder()
                                .state(CardState.card_combining_success.getCode())
                                .build(),
                        Wrappers.<NftCompositeCard>lambdaUpdate()
                                .eq(NftCompositeCard::getInfoId, nftInfoDo.getId())
                );
                log.info("BoxEventSubscriberRunner update state success {}", nftInfoDo.getId());
            }
        } catch (Exception e) {
            log.error("BoxEventSubscriberRunner update nft_composite_card exception", e);
        }

        return;
    }

    private String getMeta(String typeTag) {
        return typeTag.split("<")[1].split(">")[0].split(",")[0].trim();
    }

    private String getBody(String typeTag) {
        return typeTag.split("<")[1].split(">")[0].split(",")[1].trim();
    }

    private String getEventName(String typeTag) {
        return typeTag.split(separator)[2].split("<")[0];
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
            log.error("BoxEventSubscriberRunner exception ", e);
        }
        log.info("BoxEventSubscriberRunner duplicate event redis key {}", key);
        if (Objects.isNull(key)) {
            return true;
        }
        Long now = LocalDateTimeUtil.getMilliByTime(LocalDateTime.now());
        return !redisCache.tryGetDistributedLock(
                key, UUID.randomUUID().toString().replaceAll("-", "") + now,
                duplicateExpiredTime
        );
    }

}
