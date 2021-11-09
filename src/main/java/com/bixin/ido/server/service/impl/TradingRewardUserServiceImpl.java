package com.bixin.ido.server.service.impl;

import com.bixin.ido.server.bean.DO.TradingRewardUserDo;
import com.bixin.ido.server.core.mapper.TradingRewardUserMapper;
import com.bixin.ido.server.service.TradingRewardUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @class: TradingRewardUserServiceImpl
 * @Description:  用户交易挖矿收益表 接口实现
 * @author: 系统
 * @created: 2021-11-09
 */
@Slf4j
@Service
public class TradingRewardUserServiceImpl implements TradingRewardUserService {

    @Autowired
    private TradingRewardUserMapper tradingRewardUserMapper;

    /**
     * @explain: 添加TradingRewardUserDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int insert(TradingRewardUserDo model) {
        return tradingRewardUserMapper.insert(model);
    }

    /**
     * @explain: 删除TradingRewardUserDo对象
     * @param:   id
     * @return:  int
     */
    @Override
    public int deleteById(Long id) {
        return tradingRewardUserMapper.deleteByPrimaryKey(id);
    }

    /**
     * @explain: 修改TradingRewardUserDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    @Override
    public int update(TradingRewardUserDo model) {
        return tradingRewardUserMapper.updateByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询TradingRewardUserDo对象
     * @param:   id
     * @return:  TradingRewardUserDo
     */
    @Override
    public TradingRewardUserDo selectById(Long id) {
        return tradingRewardUserMapper.selectByPrimaryKey(id);
    }

    /**
     * @explain: 查询TradingRewardUserDo对象
     * @param:   model 对象参数
     * @return:  TradingRewardUserDo 对象
     */
    @Override
    public TradingRewardUserDo selectByObject(TradingRewardUserDo model) {
        return tradingRewardUserMapper.selectByPrimaryKeySelective(model);
    }

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    @Override
    public List<TradingRewardUserDo> listByObject(TradingRewardUserDo model) {
        return tradingRewardUserMapper.selectByPrimaryKeySelectiveList(model);
    }

}
