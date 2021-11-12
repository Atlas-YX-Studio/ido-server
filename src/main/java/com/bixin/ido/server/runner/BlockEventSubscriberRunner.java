package com.bixin.ido.server.runner;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.redis.RedisCache;
<<<<<<< HEAD
=======
import com.bixin.ido.server.service.ITradingMiningService;
>>>>>>> c0d1f3c80114c9ef73676cd582dc8ae73da841f8
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
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
public class BlockEventSubscriberRunner implements ApplicationRunner {

    @Resource
    StarConfig idoStarConfig;
    @Resource
    RedisCache redisCache;

    AtomicLong atomicSum = new AtomicLong(0);
    static final long initTime = 2000L;
    static final long initIntervalTime = 5000L;
    static final long maxIntervalTime = 60 * 1000L;
    //滤重过期时间 默认20分钟
    static final long duplicateExpiredTime = 20 * 60;

    static final String separator = "::";
    ObjectMapper mapper = new ObjectMapper();

    ThreadPoolExecutor poolExecutor;

<<<<<<< HEAD
=======
    @Resource
    private ITradingMiningService tradingMiningService;

>>>>>>> c0d1f3c80114c9ef73676cd582dc8ae73da841f8
    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("BlockEventSubscriberRunner-", true));
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("BlockEventSubscriberRunner poolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("BlockEventSubscriberRunner InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(ApplicationArguments args){
        poolExecutor.execute(() -> process(args));
    }

    public void process(ApplicationArguments args) {
        String[] sourceArgs = 0 == args.getSourceArgs().length ? new String[]{""} : args.getSourceArgs();
        log.info("BlockEventSubscriberRunner start running [{}]", sourceArgs);
        try {

            WebSocketService service = new WebSocketService("ws://" + idoStarConfig.getSwap().getWebsocketHost() + ":" + idoStarConfig.getSwap().getWebsocketPort(), true);
            service.connect();
            StarcoinSubscriber subscriber = new StarcoinSubscriber(service);
            EventFilter eventFilter = new EventFilter(Collections.singletonList("0x1"));
            Flowable<EventNotification> notificationFlowable = subscriber.newTxnSendRecvEventNotifications(eventFilter);
            notificationFlowable.blockingIterable().forEach(b -> {
                EventNotificationResult eventResult = b.getParams().getResult();
                JsonNode data = eventResult.getData();
                // 添加日志
                try {
                    log.info("BlockEventSubscriberRunner infos: {}", mapper.writeValueAsString(eventResult));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                //去重
                if (duplicateEvent(eventResult)) {
                    log.info("BlockEventSubscriberRunner duplicate event data {}", eventResult);
                    return;
                }
                String tagString = getEventName(eventResult.getTypeTag());
                if ("NewBlockEvent".equals(tagString)) {
                    // 新块产生事件
                    log.info("BlockEventSubscriberRunner duplicate event data {}", data);
<<<<<<< HEAD
=======
                    // FIXME: 2021/11/8 区块id
                    tradingMiningService.currentReward(12L);
>>>>>>> c0d1f3c80114c9ef73676cd582dc8ae73da841f8
//                    handleBlockSellEvent(data, eventResult.getTypeTag());
                } else {
                    log.error("BlockEventSubscriberRunner blockEventDo 为空");
                }
            });

        } catch (Exception e) {
            long duration = initTime + (atomicSum.incrementAndGet() - 1) * initIntervalTime;
            duration = Math.min(duration, maxIntervalTime);
            log.error("BlockEventSubscriberRunner run exception count {}, next retry {}, params {}",
                    atomicSum.get(), duration, idoStarConfig.getSwap(), e);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(duration));
            DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments("retry " + atomicSum.get());
            this.process(applicationArguments);
        }
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
            log.error("BlockEventSubscriberRunner exception ", e);
        }
        log.info("BlockEventSubscriberRunner duplicate event redis key {}", key);
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
