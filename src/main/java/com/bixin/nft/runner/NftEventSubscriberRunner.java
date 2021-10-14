package com.bixin.nft.runner;

import com.bixin.ido.server.config.StarConfig;
import com.bixin.ido.server.core.factory.NamedThreadFactory;
import com.bixin.ido.server.core.redis.RedisCache;
import com.bixin.ido.server.utils.LocalDateTimeUtil;
import com.bixin.nft.bean.DO.NftEventDo;
import com.bixin.nft.bean.DO.NftGroupDo;
import com.bixin.nft.bean.DO.NftInfoDo;
import com.bixin.nft.bean.DO.TradingRecordDo;
import com.bixin.nft.bean.dto.*;
import com.bixin.nft.core.service.NftEventService;
import com.bixin.nft.core.service.NftGroupService;
import com.bixin.nft.core.service.NftInfoService;
import com.bixin.nft.core.service.TradingRecordService;
import com.bixin.nft.enums.NftBoxType;
import com.bixin.nft.enums.NftEventType;
import com.bixin.nft.enums.TradingRecordDirection;
import com.bixin.nft.enums.TradingRecordState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private TradingRecordService tradingRecordService;

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
                NftGroupDo nftGroupDo = new NftGroupDo();
                NftInfoDo nftInfoDo = new NftInfoDo();

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
                if (NftEventType.NFT_SELL_EVENT.getDesc().equals(tagString)) {
                    // nft售卖
                    handleNftSellEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.NFT_BID_EVENT.getDesc().equals(tagString)) {
                    // nft出价
                    handleNftBidEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.NFT_BUY_EVENT.getDesc().equals(tagString)) {
                    // nft购买
                    handleNftBuyEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.NFT_CHANGE_PRICE_EVENT.getDesc().equals(tagString)) {
                    // nft修改价格
                    handleNftChangePriceEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.NFT_OFFLINE_EVENT.getDesc().equals(tagString)) {
                    // nft取消
                    handleNftOfflineEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.NFT_ACCEPT_BID_EVENT.getDesc().equals(tagString)) {
                    // nft接受报价
                    handleNftAcceptBidEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.NFT_BUY_BACK_SELL_EVENT.getDesc().equals(tagString)) {
                    // nft平台回购卖出
                    handleNftBuyBackSellEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.BOX_OFFERING_SELL_EVENT.getDesc().equals(tagString)) {
                    // 盲盒发售
                    handleBoxOfferingSellEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.BOX_BID_EVENT.getDesc().equals(tagString)) {
                    // 盲盒出价
                    handleBoxBidEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.BOX_BUY_EVENT.getDesc().equals(tagString)) {
                    // 盲盒购买
                    handleBoxBuyEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.BOX_CHANGE_PRICE_EVENT.getDesc().equals(tagString)) {
                    // 盲盒修改价格
                    handleBoxChangePriceEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.BOX_OFFLINE_EVENT.getDesc().equals(tagString)) {
                    // 盲盒取消
                    handleBoxOfflineEvent(data, eventResult.getTypeTag());
                } else if (NftEventType.BOX_ACCEPT_BID_EVENT.getDesc().equals(tagString)) {
                    // 盲盒接受报价
                    handleBoxAcceptBidEvent(data, eventResult.getTypeTag());
                } else {
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

    // nft售卖
    private void handleNftSellEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 售卖NFT");
        NftSellEventtDto dto = mapper.convertValue(data, NftSellEventtDto.class);
        NftEventDo nftEventDo = NftSellEventtDto.of(dto);

        saveNftEvent(nftEventDo, typeTag);
    }

    // nft出价
    private void handleNftBidEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner NFT出价");
        NftBidEventDto dto = mapper.convertValue(data, NftBidEventDto.class);
        NftEventDo nftEventDo = NftBidEventDto.of(dto);

        Pair<NftGroupDo, NftInfoDo> pair = saveNftEvent(nftEventDo, typeTag);
        NftGroupDo nftGroupDo = pair.getLeft();
        NftInfoDo nftInfoDo = pair.getRight();

        // 更新上一个出价人的状态
        if (StringUtils.isNotBlank(dto.getPrev_bidder())) {
            TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getPrev_bidder()).type(NftBoxType.NFT.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
            TradingRecordDo oldRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
            if (ObjectUtils.isEmpty(oldRecordDo)) {
                oldRecordDo = TradingRecordDo.builder()
                        .address(dto.getPrev_bidder())
                        .type(NftBoxType.NFT.getDesc())
                        .refId(dto.getId())
                        .direction(TradingRecordDirection.BUY.name())
                        .boxToken("")
                        .payToken(dto.getPayTokenCodeStr())
                        .state(TradingRecordState.OVER_PRICE.name())
                        .price(dto.getPrev_bid_price())
                        .fee(BigDecimal.ZERO)
                        .finish(Boolean.FALSE)
                        .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .build();
                if (!ObjectUtils.isEmpty(nftGroupDo)) {
                    oldRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                    oldRecordDo.setNftBody(nftGroupDo.getNftBody());
                    oldRecordDo.setGroupId(nftGroupDo.getId());
                }
                if (!ObjectUtils.isEmpty(nftInfoDo)) {
                    oldRecordDo.setIcon(nftInfoDo.getImageLink());
                    oldRecordDo.setName(nftInfoDo.getName());
                }
                tradingRecordService.insert(oldRecordDo);
            } else {
                oldRecordDo.setState(TradingRecordState.OVER_PRICE.name());
                oldRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
                tradingRecordService.update(oldRecordDo);
            }
        }

        // 更新或新增最新出价人记录
        TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBidder()).type(NftBoxType.NFT.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
        TradingRecordDo newRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
        if (ObjectUtils.isEmpty(newRecordDo)) {
            newRecordDo = TradingRecordDo.builder()
                    .address(dto.getBidder())
                    .type(NftBoxType.NFT.getDesc())
                    .refId(dto.getId())
                    .direction(TradingRecordDirection.BUY.name())
                    .boxToken("")
                    .payToken(dto.getPayTokenCodeStr())
                    .state(TradingRecordState.HIGHEST_PRICE.name())
                    .price(dto.getBid_price())
                    .fee(BigDecimal.ZERO)
                    .finish(Boolean.FALSE)
                    .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setGroupId(nftGroupDo.getId());
            }
            if (!ObjectUtils.isEmpty(nftInfoDo)) {
                newRecordDo.setIcon(nftInfoDo.getImageLink());
                newRecordDo.setName(nftInfoDo.getName());
            }
            tradingRecordService.insert(newRecordDo);
        } else {
            newRecordDo.setState(TradingRecordState.HIGHEST_PRICE.name());
            newRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
            newRecordDo.setPrice(dto.getBid_price());
            tradingRecordService.update(newRecordDo);
        }
    }

    // nft购买
    private void handleNftBuyEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 购买NFT");
        NftBuyEventDto dto = mapper.convertValue(data, NftBuyEventDto.class);
        NftEventDo nftEventDo = NftBuyEventDto.of(dto);

        Pair<NftGroupDo, NftInfoDo> pair = saveNftEvent(nftEventDo, typeTag);
        NftGroupDo nftGroupDo = pair.getLeft();
        NftInfoDo nftInfoDo = pair.getRight();
        // 更新owner
        try {
            nftInfoDo.setOwner(nftEventDo.getBider());
            nftInfoDo.setUpdateTime(System.currentTimeMillis());
            nftInfoService.update(nftInfoDo);
        } catch (Exception e) {
            log.error("NftEventSubscriberRunner-setinfo-Ower 发生异常 :",e);
        }

        // 更新上一个出价人的状态
        if (StringUtils.isNotBlank(dto.getPrev_bidder()) && !StringUtils.equalsIgnoreCase(dto.getPrev_bidder(), dto.getBuyer())) {
            TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getPrev_bidder()).type(NftBoxType.NFT.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
            TradingRecordDo oldRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
            if (ObjectUtils.isEmpty(oldRecordDo)) {
                oldRecordDo = TradingRecordDo.builder()
                        .address(dto.getPrev_bidder())
                        .type(NftBoxType.NFT.getDesc())
                        .refId(dto.getId())
                        .direction(TradingRecordDirection.BUY.name())
                        .boxToken("")
                        .payToken(dto.getPayTokenCodeStr())
                        .state(TradingRecordState.OVER_PRICE.name())
                        .price(dto.getPrev_bid_price())
                        .fee(BigDecimal.ZERO)
                        .finish(Boolean.TRUE)
                        .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .build();
                if (!ObjectUtils.isEmpty(nftGroupDo)) {
                    oldRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                    oldRecordDo.setNftBody(nftGroupDo.getNftBody());
                    oldRecordDo.setGroupId(nftGroupDo.getId());
                }
                if (!ObjectUtils.isEmpty(nftInfoDo)) {
                    oldRecordDo.setIcon(nftInfoDo.getImageLink());
                    oldRecordDo.setName(nftInfoDo.getName());
                }
                tradingRecordService.insert(oldRecordDo);
            } else {
                oldRecordDo.setState(TradingRecordState.OVER_PRICE.name());
                oldRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
                oldRecordDo.setFinish(Boolean.TRUE);
                tradingRecordService.update(oldRecordDo);
            }
        }

        // 更新或新增最新出价人记录
        TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBuyer()).type(NftBoxType.NFT.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
        TradingRecordDo newRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
        if (ObjectUtils.isEmpty(newRecordDo)) {
            newRecordDo = TradingRecordDo.builder()
                    .address(dto.getBuyer())
                    .type(NftBoxType.NFT.getDesc())
                    .refId(dto.getId())
                    .direction(TradingRecordDirection.BUY.name())
                    .boxToken("")
                    .payToken(dto.getPayTokenCodeStr())
                    .state(TradingRecordState.ONE_PRICE.name())
                    .price(dto.getFinal_price())
                    .fee(BigDecimal.ZERO)
                    .finish(Boolean.TRUE)
                    .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setGroupId(nftGroupDo.getId());
            }
            if (!ObjectUtils.isEmpty(nftInfoDo)) {
                newRecordDo.setIcon(nftInfoDo.getImageLink());
                newRecordDo.setName(nftInfoDo.getName());
            }
            tradingRecordService.insert(newRecordDo);
        } else {
            newRecordDo.setState(TradingRecordState.HIGHEST_PRICE.name());
            newRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
            newRecordDo.setFinish(Boolean.TRUE);
            newRecordDo.setPrice(dto.getFinal_price());
            tradingRecordService.update(newRecordDo);
        }


        // 增加出售人出售记录
        TradingRecordDo sellRecordDo = TradingRecordDo.builder()
                .address(dto.getSeller())
                .type(NftBoxType.NFT.getDesc())
                .refId(dto.getId())
                .direction(TradingRecordDirection.SELL.name())
                .boxToken("")
                .payToken(dto.getPayTokenCodeStr())
                .state(TradingRecordState.DONE.name())
                .price(dto.getFinal_price())
                .fee(dto.getCreator_fee().add(dto.getPlatform_fee()))
                .finish(Boolean.TRUE)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .build();
        if (!ObjectUtils.isEmpty(nftGroupDo)) {
            sellRecordDo.setNftMeta(nftGroupDo.getNftMeta());
            sellRecordDo.setNftBody(nftGroupDo.getNftBody());
            sellRecordDo.setGroupId(nftGroupDo.getId());
        }
        if (!ObjectUtils.isEmpty(nftInfoDo)) {
            sellRecordDo.setIcon(nftInfoDo.getImageLink());
            sellRecordDo.setName(nftInfoDo.getName());
        }
        tradingRecordService.insert(sellRecordDo);
    }

    // nft取消
    private void handleNftOfflineEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 取消NFT");
        NftOffLineEventDto dto = mapper.convertValue(data, NftOffLineEventDto.class);
        NftEventDo nftEventDo = NftOffLineEventDto.of(dto);

        Pair<NftGroupDo, NftInfoDo> pair = saveNftEvent(nftEventDo, typeTag);
        NftGroupDo nftGroupDo = pair.getLeft();
        NftInfoDo nftInfoDo = pair.getRight();

        // 更新上一个出价人的状态
        if (StringUtils.isNotBlank(dto.getBidder())) {
            TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBidder()).type(NftBoxType.NFT.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
            TradingRecordDo oldRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
            if (ObjectUtils.isEmpty(oldRecordDo)) {
                oldRecordDo = TradingRecordDo.builder()
                        .address(dto.getBidder())
                        .type(NftBoxType.NFT.getDesc())
                        .refId(dto.getId())
                        .direction(TradingRecordDirection.BUY.name())
                        .boxToken("")
                        .payToken(dto.getPayTokenCodeStr())
                        .state(TradingRecordState.SELL_CANCEL.name())
                        .price(dto.getBid_price())
                        .fee(BigDecimal.ZERO)
                        .finish(Boolean.TRUE)
                        .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .build();
                if (!ObjectUtils.isEmpty(nftGroupDo)) {
                    oldRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                    oldRecordDo.setNftBody(nftGroupDo.getNftBody());
                    oldRecordDo.setGroupId(nftGroupDo.getId());
                }
                if (!ObjectUtils.isEmpty(nftInfoDo)) {
                    oldRecordDo.setIcon(nftInfoDo.getImageLink());
                    oldRecordDo.setName(nftInfoDo.getName());
                }
                tradingRecordService.insert(oldRecordDo);
            } else {
                oldRecordDo.setState(TradingRecordState.OVER_PRICE.name());
                oldRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
                oldRecordDo.setFinish(Boolean.TRUE);
                tradingRecordService.update(oldRecordDo);
            }
        }
    }

    // nft修改价格
    private void handleNftChangePriceEvent(JsonNode data, String typeTag) {
        log.info("NftEventSubscriberRunner 修改NFT价格");
        NftChangePriceEventDto dto = mapper.convertValue(data, NftChangePriceEventDto.class);
        NftEventDo nftEventDo = NftChangePriceEventDto.of(dto);

        saveNftEvent(nftEventDo, typeTag);
    }

    // nft接受报价
    private void handleNftAcceptBidEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 接受NFT报价");
        NftAcceptBidEventDto dto = mapper.convertValue(data, NftAcceptBidEventDto.class);
        NftEventDo nftEventDo = NftAcceptBidEventDto.of(dto);

        Pair<NftGroupDo, NftInfoDo> pair = saveNftEvent(nftEventDo, typeTag);
        NftGroupDo nftGroupDo = pair.getLeft();
        NftInfoDo nftInfoDo = pair.getRight();
        // 更新owner
        try {
            nftInfoDo.setOwner(nftEventDo.getBider());
            nftInfoDo.setUpdateTime(System.currentTimeMillis());
            nftInfoService.update(nftInfoDo);
        } catch (Exception e) {
            log.error("NftEventSubscriberRunner-setinfo-Ower 发生异常 :",e);
        }

        // 更新或新增最新出价人记录
        TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBidder()).type(NftBoxType.NFT.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
        TradingRecordDo newRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
        if (ObjectUtils.isEmpty(newRecordDo)) {
            newRecordDo = TradingRecordDo.builder()
                    .address(dto.getBidder())
                    .type(NftBoxType.NFT.getDesc())
                    .refId(dto.getId())
                    .direction(TradingRecordDirection.BUY.name())
                    .boxToken("")
                    .payToken(dto.getPayTokenCodeStr())
                    .state(TradingRecordState.ACCEPT_PRICE.name())
                    .price(dto.getFinal_price())
                    .fee(BigDecimal.ZERO)
                    .finish(Boolean.TRUE)
                    .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setGroupId(nftGroupDo.getId());
            }
            if (!ObjectUtils.isEmpty(nftInfoDo)) {
                newRecordDo.setIcon(nftInfoDo.getImageLink());
                newRecordDo.setName(nftInfoDo.getName());
            }
            tradingRecordService.insert(newRecordDo);
        } else {
            newRecordDo.setState(TradingRecordState.HIGHEST_PRICE.name());
            newRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
            newRecordDo.setFinish(Boolean.TRUE);
            tradingRecordService.update(newRecordDo);
        }


        // 增加出售人出售记录
        TradingRecordDo sellRecordDo = TradingRecordDo.builder()
                .address(dto.getSeller())
                .type(NftBoxType.NFT.getDesc())
                .refId(dto.getId())
                .direction(TradingRecordDirection.SELL.name())
                .boxToken("")
                .payToken(dto.getPayTokenCodeStr())
                .state(TradingRecordState.DONE.name())
                .price(dto.getFinal_price())
                .fee(dto.getCreator_fee().add(dto.getPlatform_fee()))
                .finish(Boolean.TRUE)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .build();
        if (!ObjectUtils.isEmpty(nftGroupDo)) {
            sellRecordDo.setNftMeta(nftGroupDo.getNftMeta());
            sellRecordDo.setNftBody(nftGroupDo.getNftBody());
            sellRecordDo.setGroupId(nftGroupDo.getId());
        }
        if (!ObjectUtils.isEmpty(nftInfoDo)) {
            sellRecordDo.setIcon(nftInfoDo.getImageLink());
            sellRecordDo.setName(nftInfoDo.getName());
        }
        tradingRecordService.insert(sellRecordDo);
    }

    // nft平台回购卖出
    private void handleNftBuyBackSellEvent(JsonNode data, String typeTag) {
        log.info("NftEventSubscriberRunner NFT回购卖出");
        NFTBuyBackSellEventDto dto = mapper.convertValue(data, NFTBuyBackSellEventDto.class);
        NftEventDo nftEventDo = NFTBuyBackSellEventDto.of(dto);

        Pair<NftGroupDo, NftInfoDo> pair = saveNftEvent(nftEventDo, typeTag);
        NftGroupDo nftGroupDo = pair.getLeft();
        NftInfoDo nftInfoDo = pair.getRight();
        // 更新owner
        try {
            nftInfoDo.setOwner(nftEventDo.getBider());
            nftInfoDo.setUpdateTime(System.currentTimeMillis());
            nftInfoService.update(nftInfoDo);
        } catch (Exception e){
            log.error("NftEventSubscriberRunner-setinfo-Ower 发生异常 :",e);
        }

        // 增加出售人出售记录
        TradingRecordDo sellRecordDo = TradingRecordDo.builder()
                .address(dto.getSeller())
                .type(NftBoxType.NFT.getDesc())
                .refId(dto.getId())
                .direction(TradingRecordDirection.SELL.name())
                .boxToken("")
                .payToken(dto.getPayTokenCodeStr())
                .state(TradingRecordState.DONE.name())
                .price(dto.getFinal_price())
                .fee(BigDecimal.ZERO)
                .finish(Boolean.TRUE)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .build();
        if (!ObjectUtils.isEmpty(nftGroupDo)) {
            sellRecordDo.setNftMeta(nftGroupDo.getNftMeta());
            sellRecordDo.setNftBody(nftGroupDo.getNftBody());
            sellRecordDo.setGroupId(nftGroupDo.getId());
        }
        if (!ObjectUtils.isEmpty(nftInfoDo)) {
            sellRecordDo.setIcon(nftInfoDo.getImageLink());
            sellRecordDo.setName(nftInfoDo.getName());
        }
        tradingRecordService.insert(sellRecordDo);
    }


    /**
     * 保存event
     * @param nftEventDo
     * @param typeTag
     */
    private Pair<NftGroupDo, NftInfoDo> saveNftEvent(NftEventDo nftEventDo, String typeTag) {
        String meta = getMeta(typeTag);
        String body = getBody(typeTag);
        NftGroupDo nftGroupParm = NftGroupDo.builder().nftMeta(meta).nftBody(body).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);
        NftInfoDo nftInfoDo = null;
        if (!ObjectUtils.isEmpty(nftGroupDo)) {
            NftInfoDo NftInfoParm = NftInfoDo.builder().groupId(nftGroupDo.getId()).nftId(nftEventDo.getNftId()).build();
            nftInfoDo = nftInfoService.selectByObject(NftInfoParm);
        }

        // set group & info id
        if (ObjectUtils.isEmpty(nftGroupDo)) {
            log.error("NftEventSubscriberRunner group 不存在，meta = {}, bogy = {}", meta, body);
        } else if (ObjectUtils.isEmpty(nftInfoDo)) {
            log.error("NftEventSubscriberRunner nftInfo 不存在，groupId = {}, nftId = {}", nftGroupDo.getId(), nftEventDo.getNftId());
        } else {
            nftEventDo.setGroupId(nftGroupDo.getId());
            nftEventDo.setInfoId(nftInfoDo.getId());
        }

        nftEventService.insert(nftEventDo);
        return Pair.of(nftGroupDo, nftInfoDo);
    }

    // box平台发售卖出
    private void handleBoxOfferingSellEvent(JsonNode data, String typeTag) {
        log.info("NftEventSubscriberRunner 购买首发盲盒");
        BoxOfferingSellEventDto dto = mapper.convertValue(data, BoxOfferingSellEventDto.class);
        NftEventDo nftEventDo = BoxOfferingSellEventDto.of(dto);

        NftGroupDo nftGroupParm = NftGroupDo.builder().boxToken(dto.getBoxTokenCodeStr()).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);

        nftEventService.insert(nftEventDo);

        // 新增购买记录
        TradingRecordDo newRecordDo = TradingRecordDo.builder()
                .address(dto.getBuyer())
                .type(NftBoxType.BOX.getDesc())
                .refId(0L)
                .direction(TradingRecordDirection.BUY.name())
                .boxToken(dto.getBoxTokenCodeStr())
                .payToken(dto.getPayTokenCodeStr())
                .state(TradingRecordState.ONE_PRICE.name())
                .price(dto.getTotal_price().divide(dto.getQuantity(), RoundingMode.DOWN))
                .fee(BigDecimal.ZERO)
                .finish(Boolean.TRUE)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                newRecordDo.setName(nftGroupDo.getSeriesName());
                newRecordDo.setGroupId(nftGroupDo.getId());
            }
        for (int i=0; i<dto.getQuantity().intValue(); i++) {
            newRecordDo.setId(null);
            tradingRecordService.insert(newRecordDo);
        }

    }

    // box出价
    private void handleBoxBidEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 盲盒出价");
        BoxBidEventDto dto = mapper.convertValue(data, BoxBidEventDto.class);
        NftEventDo nftEventDo = BoxBidEventDto.of(dto);

        NftGroupDo nftGroupParm = NftGroupDo.builder().boxToken(dto.getBoxTokenCodeStr()).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);

        nftEventService.insert(nftEventDo);

        // 更新上一个出价人的状态
        if (StringUtils.isNotBlank(dto.getPrev_bidder())) {
            TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getPrev_bidder()).type(NftBoxType.BOX.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
            TradingRecordDo oldRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
            if (ObjectUtils.isEmpty(oldRecordDo)) {
                oldRecordDo = TradingRecordDo.builder()
                        .address(dto.getPrev_bidder())
                        .type(NftBoxType.BOX.getDesc())
                        .refId(dto.getId())
                        .direction(TradingRecordDirection.BUY.name())
                        .boxToken(dto.getBoxTokenCodeStr())
                        .payToken(dto.getPayTokenCodeStr())
                        .state(TradingRecordState.OVER_PRICE.name())
                        .price(dto.getPrev_bid_price())
                        .fee(BigDecimal.ZERO)
                        .finish(Boolean.FALSE)
                        .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .build();
                if (!ObjectUtils.isEmpty(nftGroupDo)) {
                    oldRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                    oldRecordDo.setNftBody(nftGroupDo.getNftBody());
                    oldRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                    oldRecordDo.setName(nftGroupDo.getSeriesName());
                    oldRecordDo.setGroupId(nftGroupDo.getId());
                }
                tradingRecordService.insert(oldRecordDo);
            } else {
                oldRecordDo.setState(TradingRecordState.OVER_PRICE.name());
                oldRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
                tradingRecordService.update(oldRecordDo);
            }
        }

        // 更新或新增最新出价人记录
        TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBidder()).type(NftBoxType.BOX.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
        TradingRecordDo newRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
        if (ObjectUtils.isEmpty(newRecordDo)) {
            newRecordDo = TradingRecordDo.builder()
                    .address(dto.getBidder())
                    .type(NftBoxType.BOX.getDesc())
                    .refId(dto.getId())
                    .direction(TradingRecordDirection.BUY.name())
                    .boxToken(dto.getBoxTokenCodeStr())
                    .payToken(dto.getPayTokenCodeStr())
                    .state(TradingRecordState.HIGHEST_PRICE.name())
                    .price(dto.getBid_price())
                    .fee(BigDecimal.ZERO)
                    .finish(Boolean.FALSE)
                    .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                newRecordDo.setName(nftGroupDo.getSeriesName());
                newRecordDo.setGroupId(nftGroupDo.getId());
            }
            tradingRecordService.insert(newRecordDo);
        } else {
            newRecordDo.setState(TradingRecordState.HIGHEST_PRICE.name());
            newRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
            newRecordDo.setPrice(dto.getBid_price());
            tradingRecordService.update(newRecordDo);
        }
    }

    // box购买
    private void handleBoxBuyEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 购买盲盒");
        BoxBuyEventDto dto = mapper.convertValue(data, BoxBuyEventDto.class);
        NftEventDo nftEventDo = BoxBuyEventDto.of(dto);

        NftGroupDo nftGroupParm = NftGroupDo.builder().boxToken(dto.getBoxTokenCodeStr()).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);

        nftEventService.insert(nftEventDo);

        // 更新上一个出价人的状态
        if (StringUtils.isNotBlank(dto.getPrev_bidder()) && !StringUtils.equalsIgnoreCase(dto.getPrev_bidder(), dto.getBuyer())) {
            TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getPrev_bidder()).type(NftBoxType.BOX.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
            TradingRecordDo oldRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
            if (ObjectUtils.isEmpty(oldRecordDo)) {
                oldRecordDo = TradingRecordDo.builder()
                        .address(dto.getPrev_bidder())
                        .type(NftBoxType.BOX.getDesc())
                        .refId(dto.getId())
                        .direction(TradingRecordDirection.BUY.name())
                        .boxToken(dto.getBoxTokenCodeStr())
                        .payToken(dto.getPayTokenCodeStr())
                        .state(TradingRecordState.OVER_PRICE.name())
                        .price(dto.getPrev_bid_price())
                        .fee(BigDecimal.ZERO)
                        .finish(Boolean.TRUE)
                        .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .build();
                if (!ObjectUtils.isEmpty(nftGroupDo)) {
                    oldRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                    oldRecordDo.setNftBody(nftGroupDo.getNftBody());
                    oldRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                    oldRecordDo.setName(nftGroupDo.getSeriesName());
                    oldRecordDo.setGroupId(nftGroupDo.getId());
                }
                tradingRecordService.insert(oldRecordDo);
            } else {
                oldRecordDo.setState(TradingRecordState.OVER_PRICE.name());
                oldRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
                oldRecordDo.setFinish(Boolean.TRUE);
                tradingRecordService.update(oldRecordDo);
            }
        }

        // 更新或新增最新出价人记录
        TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBuyer()).type(NftBoxType.BOX.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
        TradingRecordDo newRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
        if (ObjectUtils.isEmpty(newRecordDo)) {
            newRecordDo = TradingRecordDo.builder()
                    .address(dto.getBuyer())
                    .type(NftBoxType.BOX.getDesc())
                    .refId(dto.getId())
                    .direction(TradingRecordDirection.BUY.name())
                    .boxToken(dto.getBoxTokenCodeStr())
                    .payToken(dto.getPayTokenCodeStr())
                    .state(TradingRecordState.ONE_PRICE.name())
                    .price(dto.getFinal_price())
                    .fee(BigDecimal.ZERO)
                    .finish(Boolean.TRUE)
                    .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                newRecordDo.setName(nftGroupDo.getSeriesName());
                newRecordDo.setGroupId(nftGroupDo.getId());
            }
            tradingRecordService.insert(newRecordDo);
        } else {
            newRecordDo.setState(TradingRecordState.HIGHEST_PRICE.name());
            newRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
            newRecordDo.setFinish(Boolean.TRUE);
            newRecordDo.setPrice(dto.getFinal_price());
            tradingRecordService.update(newRecordDo);
        }


        // 增加出售人出售记录
        TradingRecordDo sellRecordDo = TradingRecordDo.builder()
                .address(dto.getSeller())
                .type(NftBoxType.BOX.getDesc())
                .refId(dto.getId())
                .direction(TradingRecordDirection.SELL.name())
                .boxToken(dto.getBoxTokenCodeStr())
                .payToken(dto.getPayTokenCodeStr())
                .state(TradingRecordState.DONE.name())
                .price(dto.getFinal_price())
                .fee(dto.getCreator_fee().add(dto.getPlatform_fee()))
                .finish(Boolean.TRUE)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .build();
        if (!ObjectUtils.isEmpty(nftGroupDo)) {
            sellRecordDo.setNftMeta(nftGroupDo.getNftMeta());
            sellRecordDo.setNftBody(nftGroupDo.getNftBody());
            sellRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
            sellRecordDo.setName(nftGroupDo.getSeriesName());
            sellRecordDo.setGroupId(nftGroupDo.getId());
        }
        tradingRecordService.insert(sellRecordDo);
    }

    // box取消
    private void handleBoxOfflineEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 取消盲盒");
        BoxOffLineEventDto dto = mapper.convertValue(data, BoxOffLineEventDto.class);
        NftEventDo nftEventDo = BoxOffLineEventDto.of(dto);

        NftGroupDo nftGroupParm = NftGroupDo.builder().boxToken(dto.getBoxTokenCodeStr()).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);

        nftEventService.insert(nftEventDo);

        // 更新上一个出价人的状态
        if (StringUtils.isNotBlank(dto.getBidder())) {
            TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBidder()).type(NftBoxType.BOX.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
            TradingRecordDo oldRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
            if (ObjectUtils.isEmpty(oldRecordDo)) {
                oldRecordDo = TradingRecordDo.builder()
                        .address(dto.getBidder())
                        .type(NftBoxType.BOX.getDesc())
                        .refId(dto.getId())
                        .direction(TradingRecordDirection.BUY.name())
                        .boxToken(dto.getBoxTokenCodeStr())
                        .payToken(dto.getPayTokenCodeStr())
                        .state(TradingRecordState.SELL_CANCEL.name())
                        .price(dto.getBid_price())
                        .fee(BigDecimal.ZERO)
                        .finish(Boolean.TRUE)
                        .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                        .build();
                if (!ObjectUtils.isEmpty(nftGroupDo)) {
                    oldRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                    oldRecordDo.setNftBody(nftGroupDo.getNftBody());
                    oldRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                    oldRecordDo.setName(nftGroupDo.getSeriesName());
                    oldRecordDo.setGroupId(nftGroupDo.getId());
                }
                tradingRecordService.insert(oldRecordDo);
            } else {
                oldRecordDo.setState(TradingRecordState.OVER_PRICE.name());
                oldRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
                oldRecordDo.setFinish(Boolean.TRUE);
                tradingRecordService.update(oldRecordDo);
            }
        }
    }

    // box修改价格
    private void handleBoxChangePriceEvent(JsonNode data, String typeTag) {
        log.info("NftEventSubscriberRunner 修改盲盒价格");
        BoxChangePriceEventDto dto = mapper.convertValue(data, BoxChangePriceEventDto.class);
        NftEventDo nftEventDo = BoxChangePriceEventDto.of(dto);

        nftEventService.insert(nftEventDo);
    }

    // box接受报价
    private void handleBoxAcceptBidEvent(JsonNode data,String typeTag) {
        log.info("NftEventSubscriberRunner 接受盲盒报价");
        BoxAcceptBidEventDto dto = mapper.convertValue(data, BoxAcceptBidEventDto.class);
        NftEventDo nftEventDo = BoxAcceptBidEventDto.of(dto);

        NftGroupDo nftGroupParm = NftGroupDo.builder().boxToken(dto.getBoxTokenCodeStr()).build();
        NftGroupDo nftGroupDo = nftGroupService.selectByObject(nftGroupParm);

        nftEventService.insert(nftEventDo);

        // 更新或新增最新出价人记录
        TradingRecordDo tradingRecordParam = TradingRecordDo.builder().address(dto.getBidder()).type(NftBoxType.BOX.getDesc()).refId(dto.getId()).finish(Boolean.FALSE).build();
        TradingRecordDo newRecordDo = tradingRecordService.selectByObject(tradingRecordParam);
        if (ObjectUtils.isEmpty(newRecordDo)) {
            newRecordDo = TradingRecordDo.builder()
                    .address(dto.getBidder())
                    .type(NftBoxType.BOX.getDesc())
                    .refId(dto.getId())
                    .direction(TradingRecordDirection.BUY.name())
                    .boxToken(dto.getBoxTokenCodeStr())
                    .payToken(dto.getPayTokenCodeStr())
                    .state(TradingRecordState.ACCEPT_PRICE.name())
                    .price(dto.getFinal_price())
                    .fee(BigDecimal.ZERO)
                    .finish(Boolean.TRUE)
                    .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                    .build();
            if (!ObjectUtils.isEmpty(nftGroupDo)) {
                newRecordDo.setNftMeta(nftGroupDo.getNftMeta());
                newRecordDo.setNftBody(nftGroupDo.getNftBody());
                newRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
                newRecordDo.setName(nftGroupDo.getSeriesName());
            }
            tradingRecordService.insert(newRecordDo);
        } else {
            newRecordDo.setState(TradingRecordState.HIGHEST_PRICE.name());
            newRecordDo.setUpdateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()));
            newRecordDo.setFinish(Boolean.TRUE);
            tradingRecordService.update(newRecordDo);
        }


        // 增加出售人出售记录
        TradingRecordDo sellRecordDo = TradingRecordDo.builder()
                .address(dto.getSeller())
                .type(NftBoxType.BOX.getDesc())
                .refId(dto.getId())
                .direction(TradingRecordDirection.SELL.name())
                .boxToken(dto.getBoxTokenCodeStr())
                .payToken(dto.getPayTokenCodeStr())
                .state(TradingRecordState.DONE.name())
                .price(dto.getFinal_price())
                .fee(dto.getCreator_fee().add(dto.getPlatform_fee()))
                .finish(Boolean.TRUE)
                .createTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .updateTime(LocalDateTimeUtil.getMilliByTime(LocalDateTime.now()))
                .build();
        if (!ObjectUtils.isEmpty(nftGroupDo)) {
            sellRecordDo.setNftMeta(nftGroupDo.getNftMeta());
            sellRecordDo.setNftBody(nftGroupDo.getNftBody());
            sellRecordDo.setIcon(nftGroupDo.getBoxTokenLogo());
            sellRecordDo.setName(nftGroupDo.getSeriesName());
        }
        tradingRecordService.insert(sellRecordDo);
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
