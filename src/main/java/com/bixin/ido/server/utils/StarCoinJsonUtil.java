package com.bixin.ido.server.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StarCoinJsonUtil {

    public static Object parseValueObj(Object obj) {
        @SuppressWarnings("unchecked")
        Map<String, Object> valueMap = (Map<String, Object>) obj;
//        @SuppressWarnings("unchecked")
        Object target = valueMap.get("value");
        return target;
    }

    public static List<JSONArray> parseStructObj(Object obj) {
        @SuppressWarnings("unchecked")
        Map<String, Object> valueMap = (Map<String, Object>) obj;
        @SuppressWarnings("unchecked")
        Map<String, Object> structMap = (Map<String, Object>) valueMap.get("Struct");
        @SuppressWarnings("unchecked")
        List<JSONArray> arr = (List<JSONArray>) structMap.get("value");
        return arr;
    }

    public static List<JSONObject> parseVectorObj(Object obj) {
        @SuppressWarnings("unchecked")
        Map<String, Object> valueMap = (Map<String, Object>) obj;
        @SuppressWarnings("unchecked")
        List<JSONObject> arr = (List<JSONObject>) valueMap.get("Vector");
        return arr;
    }

    public static List<JSONArray> parseRpcResult(ResponseEntity<String> resp) {
        Map<String, Object> respMap = JSON.parseObject(resp.getBody(), new TypeReference<>() {
        });
        if (!respMap.containsKey("result")) {
            return Collections.EMPTY_LIST;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) respMap.get("result");
        if (!result.containsKey("value")) {
            return Collections.EMPTY_LIST;
        }
        @SuppressWarnings("unchecked")
        List<JSONArray> values = (List<JSONArray>) result.get("value");
        return values;
    }

}
