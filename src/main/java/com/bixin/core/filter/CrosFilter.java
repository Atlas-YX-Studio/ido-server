package com.bixin.core.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author renjian
 * @Date 2022/3/21 15:46
 * 跨域filter，允许Domain:https://kikoverse.com  允许所有header
 */
@Slf4j
@WebFilter("/*")
@Component
public class CrosFilter implements Filter {
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String allowDomain = "https://kikoverse.com";
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        log.info("domaincontanter:" + allowDomain);
        String originHeader = ((HttpServletRequest) servletRequest).getHeader("Origin");
        if (allowDomain.equals(originHeader)) {
            setAccessControl(response, originHeader);
        }

        if(((HttpServletRequest)servletRequest).getMethod().equals("OPTIONS")) {
            response.setStatus(200);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void setAccessControl(HttpServletResponse res,String origin) {
        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        res.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        res.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "0");
    }

    public void destroy() {
    }
}
