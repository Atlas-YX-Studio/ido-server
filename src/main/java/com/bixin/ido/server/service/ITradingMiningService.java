package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.vo.TradingMiningOverviewVO;
import com.bixin.ido.server.bean.vo.TradingPoolVo;

import java.util.List;

public interface ITradingMiningService {
    void currentReward(Long blockId);

    List<TradingPoolVo> poolList(String address);

    TradingMiningOverviewVO market(String address);
}
