package com.bixin.nft.runner;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.bean.dto.NftBidEventtDto;
import com.bixin.nft.bean.dto.NftBuyEventDto;
import com.bixin.nft.core.service.NftEventService;
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

                // FIXME: 2021/8/30 debug
                try {
                    log.info("NftEventSubscriberRunner infos: {}", mapper.writeValueAsString(eventResult));
                    log.info("NftEventSubscriberRunner typeTag: {}", eventResult.getTypeTag());
                    log.info("NftEventSubscriberRunner eventName: {}", getEventName(eventResult.getTypeTag()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                //去重
                if (duplicateEvent(eventResult)) {
                    log.info("NftEventSubscriberRunner duplicate event data {}", eventResult);
                    return;
                }
                String tagString = getEventName(eventResult.getTypeTag());
                // 铸造
                if(NftEventType.NFTMINTEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 铸造");
                }
                // 售卖
                if(NftEventType.NFTSELLEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 售卖");
                }
                // 出价
                if(NftEventType.NFTBIDEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 出价");
                }
                // 购买
                if(NftEventType.NFTBUYEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 购买");
                }
                // 取消
                if(NftEventType.NFTOFFLINEEVENT.getDesc().equals(tagString)){
                    log.info("NftEventSubscriberRunner 取消");
                }

                //todo
                NftBuyEventDto nftBuyEventDto = mapper.convertValue(data, NftBuyEventDto.class);
                NftBidEventtDto nftBidEventtDto = mapper.convertValue(data, NftBidEventtDto.class);

                //todo 入库
                NftEventDo nftEventDo = NftBidEventtDto.of(nftBidEventtDto);
                //nftEventService.insert(nftEventDo);

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

    public static void main(String[] args) {
        NftEventSubscriberRunner nft = new NftEventSubscriberRunner();
        System.out.println(nft.getEventName("type_tag:0x290c7b35320a4dd26f651fd184373fe7::NFTMarket03::NFTSellEvent<0xd30b4de81d71c1793aa4db4763211e63::KikoCat06::KikoCatMeta,"));
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
