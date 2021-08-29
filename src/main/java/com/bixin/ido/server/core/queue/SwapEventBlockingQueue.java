package com.bixin.ido.server.core.queue;

import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.enums.StarSwapEventType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zhangcheng
 * create  2021-08-26 6:18 下午
 */
@Slf4j
@Component
public class SwapEventBlockingQueue {

    //订阅swap事件 队列
    public static Map<StarSwapEventType, LinkedBlockingQueue<JsonNode>> queueMap = new HashMap<>() {{
        put(StarSwapEventType.SWAP_EVENT, new LinkedBlockingQueue<>(CommonConstant.SWAP_EVENT_QUEUE_SIZE));
        put(StarSwapEventType.LIQUIDITY_EVENT, new LinkedBlockingQueue<>(CommonConstant.SWAP_EVENT_QUEUE_SIZE));
        put(StarSwapEventType.CREATE_PAIR_EVENT, new LinkedBlockingQueue<>(CommonConstant.SWAP_EVENT_QUEUE_SIZE));
    }};

}
