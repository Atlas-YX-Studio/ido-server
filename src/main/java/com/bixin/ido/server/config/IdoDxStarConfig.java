package com.bixin.ido.server.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangcheng
 * create          2021-08-12 4:20 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "ido.dx.star")
public class IdoDxStarConfig {

    private String resourceUrl;
    private String moduleName;

}
