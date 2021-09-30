package com.bixin.ido.server.common.exception;

import lombok.Getter;

@Getter
public class SequenceException extends RuntimeException {
    /**
     * 构造函数
     */
    public SequenceException() {
        super();
    }

    /**
     * 信息
     *
     * @param msg
     */
    public SequenceException(final String msg) {
        super(msg);
    }
}
