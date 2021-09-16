package com.bixin.ido.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.starcoin.bean.TypeObj;


public class TypeArgsUtil {

    public static TypeObj parseTypeObj(String typeAddress) {
        String[] subStr = StringUtils.split(typeAddress, "::");
        if (subStr.length != 3) {
            throw new IllegalArgumentException("Type address illegal.");
        }
        return TypeObj.builder().moduleAddress(subStr[0]).moduleName(subStr[1]).name(subStr[2]).build();
    }

}
