package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.IdoSwapCoins;
import com.bixin.ido.server.core.mapper.SwapCoinsMapper;
import com.bixin.ido.server.core.wrapDDL.SwapCoinsDDL;
import com.bixin.ido.server.service.ISwapCoinsService;
import com.bixin.ido.server.utils.CaseUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:57 下午
 */
@Service
public class SwapCoinImpl implements ISwapCoinsService {

    @Resource
    SwapCoinsMapper swapCoinsMapper;

    @Override
    public List<IdoSwapCoins> selectByDDL(IdoSwapCoins coins) {
        SwapCoinsDDL coinsDDL = new SwapCoinsDDL();
        SwapCoinsDDL.Criteria criteria = coinsDDL.createCriteria();

        CaseUtil.buildNoneValue(coins.getShortName(), name -> criteria.andShortNameEqualTo(coins.getShortName()));

        coinsDDL.setOrderByClause("weight, id desc");

        return swapCoinsMapper.selectByDDL(coinsDDL);
    }
}
