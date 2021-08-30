package com.bixin.ido.server.runner;

import com.alibaba.fastjson.JSON;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.queue.SwapEventBlockingQueue;
import com.bixin.ido.server.enums.StarSwapEventType;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zhangcheng
 * create          2021-08-24 4:24 下午
 */
@Slf4j
@Component
public class SwapEventSubscriberRunner implements ApplicationRunner {

    @Resource
    StarConfig idoStarConfig;

    AtomicLong atomicSum = new AtomicLong(0);
    static final long initTime = 2000L;
    static final long initIntervalTime = 5000L;
    static final long maxIntervalTime = 60 * 1000L;

    static final String separator = "::";

    ThreadPoolExecutor poolExecutor;

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("SwapEventSubscriber-", true));
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("SwapEventSubscriberRunner ThreadPoolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("SwapEventSubscriberRunner InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        poolExecutor.execute(() -> process(args));
    }

    public void process(ApplicationArguments args) {
        String[] sourceArgs = 0 == args.getSourceArgs().length ? new String[]{""} : args.getSourceArgs();
        log.info("IdoSwapEventRunner start running [{}]", sourceArgs);
        try {
            WebSocketService service = new WebSocketService("ws://" + idoStarConfig.getSwap().getWebsocketHost() + ":" + idoStarConfig.getSwap().getWebsocketPort(), true);
            service.connect();
            StarcoinSubscriber subscriber = new StarcoinSubscriber(service);
            EventFilter eventFilter = new EventFilter(0, idoStarConfig.getSwap().getWebsocketContractAddress());
            Flowable<EventNotification> flowableTxns = subscriber.newTxnSendRecvEventNotifications(eventFilter);

            Map<StarSwapEventType, LinkedBlockingQueue<JsonNode>> queueMap = SwapEventBlockingQueue.queueMap;

            flowableTxns.blockingIterable().forEach(b -> {
                EventNotificationResult eventResult = b.getParams().getResult();
                StarSwapEventType eventType = StarSwapEventType.of(getEventName(eventResult.getTypeTag()));
                JsonNode data = eventResult.getData();

                log.info("SwapEventSubscriberRunner infos: {}", JSON.toJSONString(eventResult));

                if (Objects.isNull(eventType) || Objects.isNull(data)) {
                    return;
                }
                queueMap.get(eventType).offer(data);
            });

        } catch (Throwable e) {
            long duration = initTime + (atomicSum.incrementAndGet() - 1) * initIntervalTime;
            duration = Math.min(duration, maxIntervalTime);
            log.error("IdoSwapEventRunner run exception count {}, next retry {}", atomicSum.get(), duration, e);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(duration));
            DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments("retry " + atomicSum.get());
            this.process(applicationArguments);
        }
    }

    private String getEventName(String typeTag) {
        return typeTag.split(separator)[2];
    }

}
