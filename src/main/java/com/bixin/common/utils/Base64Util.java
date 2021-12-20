package com.bixin.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import java.io.*;

/**
 * @author zhangcheng
 * create   2021/9/27
 */
@Slf4j
public class Base64Util {

    /**
     * 本地图片转换成base64字符串
     *
     * @param imgFile 图片本地路径
     */
    public static String ImageToBase64(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);

            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            log.error("ImageToBase64 exception", e);
        }
        // 对字节数组Base64编码
        return new Base64().encodeAsString(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * base64字符串转换成图片
     *
     * @param imgStr      base64字符串
     * @param imgFilePath 图片存放路径
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) { // 对字节数组字符串进行Base64解码并生成图片
        if (StringUtils.isEmpty(imgStr)) // 图像数据为空
            return false;
        try {
            // Base64解码
            byte[] b = new Base64().decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            log.error("Base64ToImage exception", e);
            return false;
        }

    }

}
