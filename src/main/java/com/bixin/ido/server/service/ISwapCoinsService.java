package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.SwapCoins;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:55 下午
 */
public interface ISwapCoinsService {


    List<SwapCoins> selectByDDL(SwapCoins coins);


}
