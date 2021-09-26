package com.bixin.nft.runner;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.dto.*;
import com.bixin.nft.core.service.NftEventService;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import com.bixin.nft.enums.NftEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.stereotype.Component;
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
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Component
public class NftEventSubscriberRunner implements ApplicationRunner {

    @Resource
    StarConfig idoStarConfig;
    @Resource
    RedisCache redisCache;

    @Autowired
    private NftEventService nftEventService;

    @Autowired
    private NftGroupService nftGroupService;

    @Autowired
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
                new LinkedBlockingQueue<>(), new NamedThreadFactory("NftEventSubscriberRunner-", true));
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("NftEventSubscriberRunner poolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("NftEventSubscriberRunner InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(ApplicationArguments args){
        poolExecutor.execute(() -> process(args));
    }

    public void process(ApplicationArguments args) {
        String[] sourceArgs = 0 == args.getSourceArgs().length ? new String[]{""} : args.getSourceArgs();
        log.info("NftEventSubscriberRunner start running [{}]", sourceArgs);
        try {

            WebSocketService service = new WebSocketService("ws://" + idoStarConfig.getNft().getWebsocketHost() + ":" + idoStarConfig.getNft().getWebsocketPort(), true);
            service.connect();
            StarcoinSubscriber subscriber = new StarcoinSubscriber(service);
            EventFilter eventFilter = new EventFilter(Collections.singletonList(idoStarConfig.getNft().getMarket()));
            Flowable<EventNotification> notificationFlowable = subscriber.newTxnSendRecvEventNotifications(eventFilter);
            notificationFlowable.blockingIterable().forEach(b -> {
                EventNotificationResult eventResult = b.getParams().getResult();
                JsonNode data = eventResult.getData();
                // 添加日志
                try {
                    log.info("NftEventSubscriberRunner infos: {}", mapper.writeValueAsString(eventResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //去重
                if (duplicateEvent(eventResult)) {
                    log.info("NftEventSubscriberRunner duplicate event data {}", eventResult);
                    return;
                }
                String tagString = getEventName(eventResult.getTypeTag());
                NftEventDo nftEventDo = null;
                // 售卖
                if(NftEventType.NFTSELLEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 售卖");
                    NftSellEventtDto dto = mapper.convertValue(data, NftSellEventtDto.class);
                    nftEventDo = NftSellEventtDto.of(dto,NftEventType.NFTSELLEVENT.getDesc());
                }
                // 出价
                if(NftEventType.NFTBIDEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 出价");
                    NftBidEventtDto dto = mapper.convertValue(data, NftBidEventtDto.class);
                    nftEventDo = NftBidEventtDto.of(dto,NftEventType.NFTBIDEVENT.getDesc());
                }
                // 购买
                if(NftEventType.NFTBUYEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 购买");
                    NftBuyEventDto dto = mapper.convertValue(data, NftBuyEventDto.class);
                    nftEventDo = NftBuyEventDto.of(dto,NftEventType.NFTBUYEVENT.getDesc());
                }
                // 取消
                if(NftEventType.NFTOFFLINEEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 取消");
                    NftOffLineEventtDto dto = mapper.convertValue(data, NftOffLineEventtDto.class);
                    nftEventDo = NftOffLineEventtDto.of(dto,NftEventType.NFTOFFLINEEVENT.getDesc());
                }
                if(!ObjectUtils.isEmpty(nftEventDo)){
                    setGroupIdAndInfoId(nftEventDo,eventResult.getTypeTag(),tagString);
                    nftEventService.insert(nftEventDo);
                }else{
                    log.error("NftEventSubscriberRunner nftEventDo 为空");
                }
            });

        } catch (Exception e) {
            long duration = initTime + (atomicSum.incrementAndGet() - 1) * initIntervalTime;
            duration = Math.min(duration, maxIntervalTime);
            log.error("NftEventSubscriberRunner run exception count {}, next retry {}, params {}",
                    atomicSum.get(), duration, idoStarConfig.getSwap(), e);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(duration));
            DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments("retry " + atomicSum.get());
            this.process(applicationArguments);
        }
    }


    // 设置 infoId 和 groupId
    // nft 一定都有 meta 和 body
    private void setGroupIdAndInfoId(NftEventDo nftEventDo,String typeTag,String eventType) {
        String meta = getMeta(typeTag);
        String body = getBody(typeTag);
        NftGroupDo nftGroupParm = NftGroupDo.builder().nftMeta(meta).nftBody(body).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);
        if(ObjectUtils.isEmpty(nftGroupDo)){
            log.error("NftEventSubscriberRunner group 不存在，meta = {}, bogy = {}",meta,body);
            return ;
        }
        NftInfoDo NftInfoParm = NftInfoDo.builder().groupId(nftGroupDo.getId()).nftId(nftEventDo.getNftId()).build();
        NftInfoDo nftInfoDo = nftInfoService.selectByObject(NftInfoParm);
        if(ObjectUtils.isEmpty(nftInfoDo)){
            log.error("NftEventSubscriberRunner nftInfo 不存在，groupId = {}, nftId = {}",nftGroupDo.getId(),nftEventDo.getNftId());
            return ;
        }
        nftEventDo.setInfoId(nftInfoDo.getId());
        nftEventDo.setGroupId(nftGroupDo.getId());
        try {
            if(NftEventType.NFTBUYEVENT.getDesc().equals(eventType)){
                nftInfoDo.setOwner(nftEventDo.getBider());
                nftInfoDo.setUpdateTime(System.currentTimeMillis());
                nftInfoService.update(nftInfoDo);
            }
        }catch (Exception e){
            log.error("NftEventSubscriberRunner-setinfo-Ower 发生异常 :",e);
        }
        return ;
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
            log.error("NftEventSubscriberRunner exception ", e);
        }
        log.info("NftEventSubscriberRunner duplicate event redis key {}", key);
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
