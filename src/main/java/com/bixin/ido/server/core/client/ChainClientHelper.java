package com.bixin.ido.server.core.client;

import com.beust.jcommander.internal.Maps;
import com.bixin.ido.server.bean.DO.IdoDxProduct;
import com.bixin.ido.server.config.StarConfig;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangcheng
 * create  2021-08-30 4:08 下午
 */
@Component
public class ChainClientHelper {

    @Resource
    RestTemplate restTemplate;
    @Resource
    StarConfig idoStarConfig;

    /**
     * 获取链上交易信息
     *
     * @param addressArray
     * @return
     */
    public MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> getPostResp(List<String> addressArray) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "101");
        map.put("jsonrpc", "2.0");
        map.put("method", "contract.get_resource");
        map.put("params", addressArray);
//        map.put("params", Arrays.asList("0xdc35f49d71d697d01ebc63bf4dea3f04","0x64c66296d98d6ab08579b14487157e05::Offering::Staking<0x1::STC::STC,0x99a287696c35e978c19249400c616c6a::DummyToken1::USDT,0x99a287696c35e978c19249400c616c6a::DummyToken1::GEM>"));
//        map.put("params", new String[]{"0xdc35f49d71d697d01ebc63bf4dea3f04", "0x64c66296d98d6ab08579b14487157e05::Offering::Staking<0x1::STC::STC,0x99a287696c35e978c19249400c616c6a::DummyToken1::USDT,0x99a287696c35e978c19249400c616c6a::DummyToken1::GEM>"});

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(idoStarConfig.getDx().getResourceUrl(), request, String.class);

        return new MutableTriple<>(response, idoStarConfig.getDx().getResourceUrl(), request);
    }

    public MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> getPostListResp(List<Object> addressArray) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> map = new HashMap<>();
        map.put("id", "101");
        map.put("jsonrpc", "2.0");
        map.put("method", "state.list_resource");
        map.put("params", addressArray);
//        map.put("params", Arrays.asList("0xdc35f49d71d697d01ebc63bf4dea3f04","0x64c66296d98d6ab08579b14487157e05::Offering::Staking<0x1::STC::STC,0x99a287696c35e978c19249400c616c6a::DummyToken1::USDT,0x99a287696c35e978c19249400c616c6a::DummyToken1::GEM>"));
//        map.put("params", new String[]{"0xdc35f49d71d697d01ebc63bf4dea3f04", "0x64c66296d98d6ab08579b14487157e05::Offering::Staking<0x1::STC::STC,0x99a287696c35e978c19249400c616c6a::DummyToken1::USDT,0x99a287696c35e978c19249400c616c6a::DummyToken1::GEM>"});

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(idoStarConfig.getDx().getResourceUrl(), request, String.class);

        return new MutableTriple<>(response, idoStarConfig.getDx().getResourceUrl(), request);
    }

    public MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> getStakingResp(String userAddress, IdoDxProduct product) {
        List<String> addressArray = Arrays.asList(userAddress, idoStarConfig.getDx().getModuleName() + "::Staking<" +
                product.getPledgeAddress() + "," + product.getPayAddress() + "," + product.getAssignAddress() + ">");
        return getPostResp(addressArray);
    }

    public MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> getLPResp(String tokenA, String tokenB) {
        List<String> addressArray = Arrays.asList(idoStarConfig.getSwap().getContractAddress(), idoStarConfig.getSwap().getLpPoolResourceName() + "<" + tokenA + "," + tokenB + ">");
        return getPostResp(addressArray);
    }

    public MutableTriple<ResponseEntity<String>, String, HttpEntity<Map<String, Object>>> getAllLPResp() {
        List<Object> addressArray = Arrays.asList(idoStarConfig.getSwap().getContractAddress(), Maps.newHashMap("decode", true));
        return getPostListResp(addressArray);
    }
}
