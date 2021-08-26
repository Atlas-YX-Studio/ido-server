package com.bixin.ido.server.provider;

/**
 * @author zhangcheng
 * create          2021-08-24 5:40 下午
 */
public interface IStarSwapProvider<T> {

    void dispatcher(T t);

}
