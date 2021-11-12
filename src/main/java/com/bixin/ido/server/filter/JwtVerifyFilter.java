package com.bixin.ido.server.filter;


import com.bixin.ido.server.bean.vo.wrap.R;
import com.bixin.ido.server.common.errorcode.IdoErrorCode;
import com.bixin.ido.server.constants.CommonConstant;
import com.bixin.ido.server.utils.JwtUtil;
import com.bixin.ido.server.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.bixin.ido.server.constants.PathConstant.NFT_REQUEST_PATH_PREFIX;

/**
 * JWT验签
 */
@Slf4j
@WebFilter("/*")
@Component
public class JwtVerifyFilter implements Filter {

    @Value("${spring.profiles.active}")
    private String env;

    private static List<String> pathWhitelist = List.of(
            NFT_REQUEST_PATH_PREFIX + "/image/group",
            NFT_REQUEST_PATH_PREFIX + "/image/info",
            NFT_REQUEST_PATH_PREFIX + "/contract"
            );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 非生产环境不验证
        if (!StringUtils.equalsIgnoreCase(env, "prod")) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String uri = httpServletRequest.getRequestURI();
        // 获取图片不需要验证token
        if (StringUtils.startsWithAny(uri, pathWhitelist.toArray(new CharSequence[0]))) {
            chain.doFilter(request, response);
            return;
        }
        String token = httpServletRequest.getHeader(CommonConstant.HTTP_X_TOKEN);
        if (JwtUtil.decode(token)) {
            // 验证成功
            chain.doFilter(request, response);
        } else {
            // 验证失败
            log.info("请求失败, request:{}, token:{}, ", httpServletRequest.getRequestURI(), token);
            ResponseUtil.setBody((HttpServletResponse) response, R.failed(IdoErrorCode.TOKEN_VERIFY_FAILURE));
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
