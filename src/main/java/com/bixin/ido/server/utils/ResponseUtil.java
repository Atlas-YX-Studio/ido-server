package com.bixin.ido.server.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
public class ResponseUtil {

    public static <T> void setBody(HttpServletResponse response, T data) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (PrintWriter writer = response.getWriter()) {
            writer.print(JSON.toJSONString(data));
        } catch (Exception e) {
            log.error("response setBody error", e);
        }
    }

}
