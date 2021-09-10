package com.bixin.ido.server.runner;

import com.bixin.ido.server.bean.dto.LiquidityEventDto;
import com.bixin.ido.server.bean.dto.SwapEventDto;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.queue.SwapEventBlockingQueue;
import com.bixin.ido.server.enums.StarSwapEventType;
import com.bixin.ido.server.function.CaseFun;
import com.bixin.ido.server.provider.StarSwapDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;

/**
 * @author zhangcheng
 * create  2021-08-26 6:37 下午
 */
@Slf4j
@Component
public class SwapEventConsumerRunner implements ApplicationRunner {

    @Resource
    StarConfig starConfig;
    @Resource
    StarSwapDispatcher swapDispatcher;

    ThreadPoolExecutor poolExecutor;
    ObjectMapper mapper = new ObjectMapper();
    static int parkMilliSeconds = 2000;

    Map<StarSwapEventType, Consumer> dispatcherMap = new HashMap<>();

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(starConfig.getRunner().getSwapConsumer().getCoreThreads(),
                starConfig.getRunner().getSwapConsumer().getMaxThreads(),
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("SwapEventConsumer-", true));
    }

    @PreDestroy
    public void destroy() {
        try {
            if (Objects.isNull(poolExecutor)) {
                return;
            }
            poolExecutor.shutdown();
            poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
            log.info("SwapEventConsumerRunner ThreadPoolExecutor stopped");
        } catch (InterruptedException ex) {
            log.error("SwapEventConsumerRunner InterruptedException: ", ex);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        poolExecutor.execute(() -> process(args));
    }

    public void process(ApplicationArguments args) {
        Map<StarSwapEventType, LinkedBlockingQueue<JsonNode>> queueMap = SwapEventBlockingQueue.queueMap;
        for (; ; ) {
            queueMap.entrySet().forEach(entry -> {
                StarSwapEventType type = entry.getKey();
                LinkedBlockingQueue<JsonNode> jsonNodes = entry.getValue();
                JsonNode node = jsonNodes.poll();
                if (Objects.nonNull(node)) {
                    swapDispatcher(type, node);
                }
            });
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(parkMilliSeconds));
        }
    }

    private void swapDispatcher(StarSwapEventType type, JsonNode node) {
        CaseFun.builder().hasContinue(true).build()
                .elseCase(type, value -> StarSwapEventType.SWAP_EVENT == value, value -> {
                    SwapEventDto swapEventDto = mapper.convertValue(node, SwapEventDto.class);
                    swapDispatcher.dispatch(SwapEventDto.of(swapEventDto));
                })
                .elseCase(type, value -> StarSwapEventType.LIQUIDITY_EVENT == value, value -> {
                    LiquidityEventDto liquidityEventDto = mapper.convertValue(node, LiquidityEventDto.class);
                    swapDispatcher.dispatch(LiquidityEventDto.of(liquidityEventDto));
                });
    }

}
