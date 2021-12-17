package com.bixin.common.response;

import com.bixin.common.code.IdoErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author zhangcheng
 * create 2021-07-09 3:19 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    private int code;
    private String msg;
    private long timeStamp;
    private T data;

    public static R success() {
        return success(null);
    }

    public static <T> R success(T data) {
        return R.builder()
                //200
                .code(HttpStatus.OK.value())
                //ok
                .msg(HttpStatus.OK.getReasonPhrase())
                .timeStamp(System.currentTimeMillis())
                .data(data)
                .build();
    }

    public static R failed() {
        return failed(10002, "FAILED");
    }

    public static R failed(String errorMsg) {
        return failed(10002, errorMsg);
    }

    public static R failed(IdoErrorCode idoErrorCode) {
        return failed(idoErrorCode.getCode(), idoErrorCode.getMessage());
    }

    public static R failed(int errorCode, String errorMsg) {
        return R.builder()
                .code(errorCode)
                //failed
                .msg(errorMsg)
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .build();
    }


}
