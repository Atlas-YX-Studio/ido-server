package com.bixin.ido.server.function;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author zhangcheng
 * create  2021-08-29 11:50 上午
 */
@Data
@Builder
public class CaseFun {

    private boolean hasContinue;

    public <T> CaseFun elseCase(T t, Predicate<T> predicate, Consumer<T> consumer) {
        if (hasContinue && predicate.test(t)) {
            hasContinue = false;
            consumer.accept(t);
        }
        return this;
    }

}
