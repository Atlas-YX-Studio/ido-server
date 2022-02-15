package com.bixin.core.client;

import com.bixin.common.config.StarConfig;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author zhangcheng
 * create  2022/1/13
 */
@Component
public class HttpClientHelper {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private StarConfig starConfig;


    public MutableTriple<ResponseEntity<String>, String, HttpEntity<String>> getCreateImgResp(String param) {
        return getPostResp(starConfig.getNft().getCompositeUrl(), param);
    }

    private <T> MutableTriple<ResponseEntity<String>, String, HttpEntity<T>> getPostResp(String requestUrl, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<T> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, request, String.class);

        return new MutableTriple<>(response, requestUrl, request);
    }

}
