package com.bixin.nft.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangcheng
 * create   2021/9/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainResourceDto {
    private String jsonrpc;
    private String id;
    private ChainResource result = new ChainResource();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChainResource {
        private Map<String, Object> resources = new HashMap<>();
    }

}
