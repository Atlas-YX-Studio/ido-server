package com.bixin.common.response;

import java.io.Serializable;

import com.bixin.common.code.ErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回实体，使用swagger做文档
 * @NoArgsConstructor ： 生成一个无参数的构造方法
 * @AllArgsContructor 会生成一个包含所有变量
 * @author: xin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseResult<T> implements Serializable {

    //返回code ，0表示成功，>0表示失败,<0系统保留"
    private int code = 0;

    //提示信息
    private String msg = "";

    //详细提示信息
    private String detailMsg = "";

   //返回结果数据
    private T data;

    /** 成功 */
    public static ResponseResult success() {
        return success(null);
    }

    /** 成功 返回数据 */
    public static <T> ResponseResult success(final T data) {
        return build(0, "", "", data);
    }

    /** 失败 返回code和msg */
    public static ResponseResult failure(final int code, final String msg) {
        return failure(code, msg, "");
    }

    /** 失败 返回 ErrorCode */
    public static ResponseResult failure(final ErrorCode errorCode) {
        return failure(errorCode, "");
    }

    /**
     * 失败 返回 ErrorCode 和 错误详情
     */
    public static ResponseResult failure(final ErrorCode errorCode, final String detailMsg) {
        return failure(errorCode.getCode(), errorCode.getMessage(), detailMsg);
    }

    /**
     * 失败 返回 ErrorCode 和 具体数据
     */
    public static <T> ResponseResult failure(final ErrorCode errorCode, final T data) {
        return build(errorCode.getCode(), errorCode.getMessage(), "", data);
    }

    /**
     * 失败 返回 msg
     */
    public static ResponseResult failure(final String msg) {
        return failure(-1, msg, "");
    }

    /**
     * 失败 返回 msg 和 错误详情
     */
    public static ResponseResult failure(final String msg, final String detailMsg) {
        return failure(-1, msg, detailMsg);
    }

    /**
     * 失败 返回 code，msg 和 detailMsg
     */
    public static ResponseResult failure(final int code, final String msg, final String detailMsg) {
        return build(code, msg, detailMsg, null);
    }

    /**
     * 生成返回结果
     */
    public static <T> ResponseResult build(final int code, final String msg, final String detailMsg, final T data) {
        return new ResponseResult<>(code, msg, detailMsg, data);
    }
}