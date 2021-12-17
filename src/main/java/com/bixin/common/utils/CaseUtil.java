package com.bixin.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author zhangcheng
 * create  2021-08-26 3:11 下午
 */
public class CaseUtil {

    //只为【testAndDo】方法使用

    public static <T> void buildNoneValue(T t, Consumer<T> consumer) {
        if (Objects.nonNull(t)) {
            consumer.accept(t);
        }
    }

    public static void buildNoneValue(Number number, Consumer<Number> consumer) {
        if (Objects.nonNull(number) && number.toString().compareTo("0") > 0) {
            consumer.accept(number);
        }
    }

    public static void buildNoneValue(String t, Consumer<String> consumer) {
        if (StringUtils.isNotBlank(t)) {
            consumer.accept(t);
        }
    }

}
