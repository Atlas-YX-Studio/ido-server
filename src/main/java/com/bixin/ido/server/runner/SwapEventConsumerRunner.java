package com.bixin.ido.server.runner;

import com.bixin.ido.server.bean.DO.LiquidityPool;
import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.queue.SwapEventBlockingQueue;
import com.bixin.ido.server.enums.StarSwapEventType;
import com.bixin.ido.server.provider.StarSwapDispatcher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        queueMap.entrySet().forEach(entry -> {
            StarSwapEventType type = entry.getKey();
            LinkedBlockingQueue<JsonNode> jsonNodes = entry.getValue();
            JsonNode node = jsonNodes.poll();
            if (Objects.nonNull(node)) {
                swapDispatcher(type, node);
            }
        });
    }

    private void swapDispatcher(StarSwapEventType type, JsonNode node) {
        if (StarSwapEventType.CREATE_PAIR_EVENT == type) {
            LiquidityPool liquidityPool = mapper.convertValue(node, LiquidityPool.class);
            swapDispatcher.dispatch(liquidityPool);
        } else if (StarSwapEventType.SWAP_EVENT == type) {
            SwapUserRecord swapUserRecord = mapper.convertValue(node, SwapUserRecord.class);
            swapDispatcher.dispatch(swapUserRecord);
        } else if (StarSwapEventType.LIQUIDITY_EVENT == type) {
            LiquidityUserRecord liquidityUserRecord = mapper.convertValue(node, LiquidityUserRecord.class);
            swapDispatcher.dispatch(liquidityUserRecord);
        }
    }

}
