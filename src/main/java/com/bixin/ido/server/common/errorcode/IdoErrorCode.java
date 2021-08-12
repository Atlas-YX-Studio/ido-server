package com.bixin.ido.server.common.errorcode;

import com.bixin.ido.server.common.code.ErrorCode;

/**
 * @Description: 业务错误码，错误信息在messages中配置
 * @author: 系统
 */
public enum IdoErrorCode implements ErrorCode {

    /**
     * 系统个错误
     */
    SYSTEM_ERROR(100000, "system error"),

    /**
     * 参数错误
     */
    PARAMS_ERROR(100001, "param error"),

    /**
     * 对象为空
     */
    OBJECT_IS_EMPTY(100002, "object is null"),

    /**
     * 查询结果为空
     */
    QUERY_RESULT_IS_EMPTY(100003, "query result is empty"),

    /**
     * 数据验证失败
     */
    DATA_BIND_VALIDATION_FAILURE(100004, "data bind validation failure"),

    /**
     * 数据绑定失败
     */
    DATA_VALIDATION_FAILURE(100005, "data validation failure"),

    ;

    private final int code;

    private final String message;

    IdoErrorCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return "[" + this.getCode() + "]" + this.getMessage();
    }
}
