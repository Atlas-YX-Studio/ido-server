package com.bixin.ido.server.runner;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
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

    ThreadPoolExecutor poolExecutor;

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

    }

}
