package com.bixin.ido.server.scheduler;

import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.core.queue.SwapEventBlockingQueue;
import com.bixin.ido.server.enums.StarSwapEventType;
import com.bixin.ido.server.function.CaseFun;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Predicate;

/**
 * @author zhangcheng
 * create  2021-08-29 10:45 上午
 */
@Slf4j
@Component
public class ScheduleQueueMonitor {

    /**
     * 优先判断是否小于队列容量的【万分之一、千分之一】，再判断是否大于队列容量的【90%，80%，70%】
     **/
    static int line_70 = 0;
    static int line_80 = 0;
    static int line_90 = 0;
    static int line_001 = 0;
    static int line_0001 = 0;

    @PostConstruct
    public void init() {
        line_70 = buildLineX("0.7");
        line_80 = buildLineX("0.8");
        line_90 = buildLineX("0.9");

        line_001 = buildLineX("0.001");
        line_0001 = buildLineX("0.0001");
    }


    //    @Scheduled(cron = "0/15 * * * * ?")
    @Scheduled(cron = "0/10 * * * * ?")
    public void monitorSwapEventQueue() {
        Map<StarSwapEventType, LinkedBlockingQueue<JsonNode>> queueMap = SwapEventBlockingQueue.queueMap;
        queueMap.entrySet().forEach(entry -> {
            StarSwapEventType eventType = entry.getKey();
            LinkedBlockingQueue<JsonNode> jsonNodes = entry.getValue();
            /**
             * 优先判断是否小于队列容量的【万分之一、千分之一】，再判断是否大于队列容量的【90%，80%，70%】
             **/
            CaseFun caseFun = CaseFun.builder().hasContinue(true).build();
            if (!smaller(caseFun, eventType, jsonNodes.size()).isHasContinue()) {
                greater(caseFun, eventType, jsonNodes.size());
            }

        });

    }

    private CaseFun greater(CaseFun caseFun, StarSwapEventType type, int size) {
        return caseFun.elseCase(size, value -> predicate_line_90.test(value), value -> log.warn("event queue size is greater line 90 {} {}", type, value))
                .elseCase(size, value -> predicate_line_80.test(value), value -> log.warn("event queue size is greater line 80 {} {}", type, value))
                .elseCase(size, value -> predicate_line_70.test(value), value -> log.warn("event queue size is greater line 70 {} {}", type, value));

    }

    private CaseFun smaller(CaseFun caseFun, StarSwapEventType type, int size) {
        return caseFun.elseCase(size, value -> predicate_line_0001.test(value), value -> log.info("event queue size is smaller line 0001 {} {}", type, value))
                .elseCase(size, value -> predicate_line_001.test(value), value -> log.info("event queue size is smaller line 001 {} {}", type, value));
    }

    Predicate<Integer> predicate_line_90 = value -> value > line_90;
    Predicate<Integer> predicate_line_80 = value -> value > line_80;
    Predicate<Integer> predicate_line_70 = value -> value > line_70;

    Predicate<Integer> predicate_line_001 = value -> value < line_001;
    Predicate<Integer> predicate_line_0001 = value -> value < line_0001;


    private int buildLineX(String score) {
        return new BigDecimal(CommonConstant.SWAP_EVENT_QUEUE_SIZE).multiply(new BigDecimal(score)).intValue();
    }

}
