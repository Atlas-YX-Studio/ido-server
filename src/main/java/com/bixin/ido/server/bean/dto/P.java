package com.bixin.ido.server.bean.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author zhangcheng
 * create  2021-08-26 3:18 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class P<T> {

    private int code;
    private String msg;
    private long timeStamp;
    //是否有下一页
    private boolean hasNext;
    private T data;

    public static P success(boolean hasNext) {
        return success(null, hasNext);
    }

    public static <T> P success(T data, boolean hasNext) {
        return P.builder()
                //200
                .code(HttpStatus.OK.value())
                //ok
                .msg(HttpStatus.OK.getReasonPhrase())
                .timeStamp(System.currentTimeMillis())
                .data(data)
                .hasNext(hasNext)
                .build();
    }

    public static P failed() {
        return P.builder()
                //10002Ø
                .code(10002)
                //failed
                .msg("FAILED")
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .hasNext(false)
                .build();
    }

    public static P failed(String errorMsg) {
        return P.builder()
                //10002
                .code(10002)
                //failed
                .msg(errorMsg)
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .hasNext(false)
                .build();
    }

    public static P failed(int errorCode, String errorMsg) {
        return P.builder()
                .code(errorCode)
                //failed
                .msg(errorMsg)
                .timeStamp(System.currentTimeMillis())
                .data(null)
                .hasNext(false)
                .build();
    }


}
