package com.bixin.ido.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangcheng
 * create 2021-08-06 6:18 下午
 */
@Getter
@AllArgsConstructor
public enum ProductState {

    INIT(1, "init"),

    PROCESSING(2, "processing"),

    FINISH(3, "finish");

    private int code;
    private String desc;

    public static ProductState of(int code) {
        switch (code) {
            case 1:
                return INIT;
            case 2:
                return PROCESSING;
            case 3:
                return FINISH;
            default:
                return null;
        }
    }

}
