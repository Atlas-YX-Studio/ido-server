package com.bixin.ido.service;

import com.bixin.ido.bean.DO.TradingRewardUserDo;

import java.util.List;

/**
 * @interface: TradingRewardUserService
 * @Description:  用户交易挖矿收益表 接口
 * @author: 系统
 * @created: 2021-11-09
 */
public interface ITradingRewardUserService {

    /**
     * @explain: 添加TradingRewardUserDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int insert(TradingRewardUserDo model);

    /**
     * @explain: 删除TradingRewardUserDo对象
     * @param:   id
     * @return:  int
     */
    int deleteById(Long id);

    /**
     * @explain: 修改TradingRewardUserDo对象
     * @param:   model 对象参数
     * @return:  int
     */
    int update(TradingRewardUserDo model);

    /**
     * @explain: 查询TradingRewardUserDo对象
     * @param:   id
     * @return:  TradingRewardUserDo
     */
    TradingRewardUserDo selectById(Long id);

    /**
     * @explain: 查询TradingRewardUserDo对象
     * @param:   model  对象参数
     * @return:  TradingRewardUserDo 对象
     */
    TradingRewardUserDo selectByObject(TradingRewardUserDo model);

    /**
     * @explain: 查询列表
     * @param:  model  对象参数
     * @return: list
     */
    List<TradingRewardUserDo> listByObject(TradingRewardUserDo model);

}
