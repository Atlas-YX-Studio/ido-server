package com.bixin.ido.service;

import com.bixin.ido.service.impl.PlatformBuyBackServiceImpl;

import java.util.List;

public interface IPlatformBuyBackService {
    List<PlatformBuyBackServiceImpl.BuyBackOrder> getOrders(Long groupId, String nftType, String currency, String sortRule, int sort, int pageNum, int pageSize);

    PlatformBuyBackServiceImpl.BuyBackOrder getOrder(Long id, Long groupId, String currency);
}
