package com.bixin.common.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 数据拷贝接口
 */
public class BeanCopyUtil extends BeanUtils {

    /**
     * 集合数据的拷贝
     *
     * @param sources 数据源类
     * @param target  数据源类
     * @return 结果列表
     * @author Zhihong Miao
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Supplier<T> target) {
        if (CollectionUtils.isEmpty(sources)) {
            return Lists.newArrayList();
        }
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.get();
            copyProperties(source, t);
            list.add(t);
        }
        return list;
    }

    /**
     * 集合数据的拷贝
     *
     * @param sources 数据源类
     * @param target  数据源类
     * @return 结果列表
     * @author Zhihong Miao
     */
    public static <S, T> List<T> copyListProperties(List<S> sources, Function<S, T> target) {
        if (CollectionUtils.isEmpty(sources)) {
            return Lists.newArrayList();
        }
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T t = target.apply(source);
            copyProperties(source, t);
            list.add(t);
        }
        return list;
    }

    /**
     * 数据拷贝
     */
    public static <S, T> T copyProperties(S source, Supplier<T> target) {
        T t = target.get();
        copyProperties(source, t);
        return t;
    }
}
