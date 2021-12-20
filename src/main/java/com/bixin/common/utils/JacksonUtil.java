package com.bixin.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangcheng
 * create   2021/9/17
 */
@Slf4j
public final class JacksonUtil {

    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 使用泛型方法，把json字符串转换为相应的JavaBean对象。
     * (1)转换为普通JavaBean：readValue(json,Student.class)
     * (2)转换为List,如List<Student>,将第二个参数传递为Student
     * [].class.然后使用Arrays.asList();方法把得到的数组转换为特定类型的List
     *
     * @param jsonStr
     * @param valueType
     * @return
     */
    public static <T> T readValue(String jsonStr, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonStr, valueType);
        } catch (Exception e) {
            log.error("jackson readvalue exeption {}", e.getMessage(), e);
        }

        return null;
    }

    /**
     * json数组转List
     *
     * @param jsonStr
     * @param valueTypeRef
     * @return
     */
    public static <T> T readValue(String jsonStr, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(jsonStr, valueTypeRef);
        } catch (Exception e) {
            log.error("jackson readvalue exeption {}", e.getMessage(), e);
        }

        return null;
    }

    public static <T> T readByteValue(byte[] src, TypeReference<T> valueType) {
        try {
            return objectMapper.readValue(src, valueType);
        } catch (Exception e) {
            log.error("jackson readByteValue exeption {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 把JavaBean转换为json字符串
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("jackson toJSon exeption {}", e.getMessage(), e);
        }

        return null;
    }

    /**
     * 把JavaBean转换为byte[]
     *
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object) {

        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (Exception e) {
            log.error("jackson toBytes exeption {}", e.getMessage(), e);
        }
        return null;
    }

}