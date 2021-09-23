package com.bixin.ido.server.utils;

import org.apache.commons.lang3.StringUtils;
import org.starcoin.bean.TypeObj;


public class TypeArgsUtil {

    /**
     * 截取合约类型参数
     * 0xd30b4de81d71c1793aa4db4763211e63::KikoCat::KikoCatBox ->
     * {moduleAddress:0xd30b4de81d71c1793aa4db4763211e63, moduleName:KikoCat, name:KikoCatBox}
     *
     * @param typeAddress
     * @return
     */
    public static TypeObj parseTypeObj(String typeAddress) {
        String[] subStr = StringUtils.split(typeAddress, "::");
        if (subStr.length != 3) {
            throw new IllegalArgumentException("Type address illegal.");
        }
        // 补齐32位
        String moduleAddress = "0x" + StringUtils.leftPad(subStr[0].substring("0x".length()), 32, "0");
        return TypeObj.builder().moduleAddress(moduleAddress).moduleName(subStr[1]).name(subStr[2]).build();
    }

}
