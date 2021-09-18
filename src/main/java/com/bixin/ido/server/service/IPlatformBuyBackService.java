package com.bixin.ido.server.service;

import com.bixin.ido.server.service.impl.PlatformBuyBackServiceImpl;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IPlatformBuyBackService {
    List<PlatformBuyBackServiceImpl.BuyBackOrder> getOrders(Long groupId, String currency, int sort, long pageSize, long nextId);

}
