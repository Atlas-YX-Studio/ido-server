package com.bixin.common.exception;

import com.bixin.common.code.IdoErrorCode;
import lombok.Getter;

/**
 * @Description: 业务异常类
 * @author: 系统
 */
@Getter
public class IdoException extends BizException {

    private int code;

    private String message;

    public IdoException() {
        super();
    }

    public IdoException(final IdoErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public IdoException(final IdoErrorCode errorCode, final Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}
