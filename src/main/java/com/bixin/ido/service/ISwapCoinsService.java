package com.bixin.ido.service;

import com.bixin.ido.bean.DO.SwapCoins;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:55 下午
 */
public interface ISwapCoinsService {


    List<SwapCoins> selectByDDL(SwapCoins coins);

    List<SwapCoins> getALlByPage(int from, int offset);
}
