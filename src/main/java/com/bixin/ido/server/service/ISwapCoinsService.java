package com.bixin.ido.server.service;

import com.bixin.ido.server.bean.DO.IdoSwapCoins;

import java.util.List;

/**
 * @author zhangcheng
 * create  2021-08-26 2:55 下午
 */
public interface ISwapCoinsService {


    List<IdoSwapCoins> selectByDDL(IdoSwapCoins coins);


}
