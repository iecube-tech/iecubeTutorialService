package com.iecube.iecubetutorial.util.base64;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {
    public static String encodeString(String str) {
        // 把字符串转换为 UTF-8 字节数组
        byte[] jsonBytes = str.getBytes(StandardCharsets.UTF_8);
        // 执行 Base64 编码
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(jsonBytes);
        // 将编码后的字节数组转换为字符串
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String decodeString(String base64String) {
        // 把 Base64 编码的字符串转换为字节数组
        byte[] encodedBytes = base64String.getBytes(StandardCharsets.UTF_8);
        // 进行 Base64 解码
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedBytes);
        // 将解码后的字节数组转换为字符串
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
