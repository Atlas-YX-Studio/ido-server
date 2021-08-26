package com.bixin.ido.server.utils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author zhangcheng
 * create  2021-08-26 3:11 下午
 */
public class CaseUtil {

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

}
