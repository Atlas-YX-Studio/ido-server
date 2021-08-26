package com.bixin.ido.server.runner;

import com.bixin.ido.server.bean.DO.LiquidityUserRecord;
import com.bixin.ido.server.bean.DO.SwapUserRecord;
import com.bixin.ido.server.config.IdoStarConfig;
import com.bixin.ido.server.enums.StarSwapEventType;
import com.bixin.ido.server.provider.StarSwapDispatcher;
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

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Supplier;

/**
 * @author zhangcheng
 * create          2021-08-24 4:24 下午
 */
@Slf4j
@Component
public class IdoSwapEventRunner implements ApplicationRunner {

    @Resource
    IdoStarConfig idoStarConfig;
    @Resource
    StarSwapDispatcher starSwapDispatcher;

    AtomicLong atomicSum = new AtomicLong(0);
    static final long initTime = 2000L;
    static final long initIntervalTime = 5000L;
    static final long maxIntervalTime = 60 * 1000L;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String[] sourceArgs = 0 == args.getSourceArgs().length ? new String[]{""} : args.getSourceArgs();
        log.info("IdoSwapEventRunner start running [{}]", sourceArgs);
        try {
            WebSocketService service = new WebSocketService("ws://" + idoStarConfig.getSwap().getWebsocketHost() + ":" + idoStarConfig.getSwap().getWebsocketPort(), true);
            service.connect();
            StarcoinSubscriber subscriber = new StarcoinSubscriber(service);
            EventFilter eventFilter = new EventFilter(0, idoStarConfig.getSwap().getWebsocketContractAddress());
            Flowable<EventNotification> flowableTxns = subscriber.newTxnSendRecvEventNotifications(eventFilter);

            flowableTxns.blockingIterable().forEach(b -> {
                EventNotificationResult eventResult = b.getParams().getResult();
                StarSwapEventType eventType = StarSwapEventType.of(eventResult.getTypeTag());
                //分发事件
                consumerEventElse(
                        () -> StarSwapEventType.SWAP_EVENT == eventType,
                        SwapUserRecord.builder().id(11L).build()
                ).consumerEventElse(
                        () -> StarSwapEventType.LIQUIDITY_EVENT == eventType,
                        LiquidityUserRecord.builder().id(22L).build()
                );
            });

        } catch (Throwable e) {
            long duration = initTime + (atomicSum.incrementAndGet() - 1) * initIntervalTime;
            duration = Math.min(duration, maxIntervalTime);
            log.error("IdoSwapEventRunner run exception sum{}, next retry {}", atomicSum.get(), duration, e);
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(duration));
            DefaultApplicationArguments applicationArguments = new DefaultApplicationArguments("retry " + atomicSum.get());
            this.run(applicationArguments);
        }

    }

    private <T> IdoSwapEventRunner consumerEventElse(Supplier<Boolean> supplier, T t) {
        if (supplier.get()) {
            starSwapDispatcher.dispatch(t);
        }
        return this;
    }


}
