package com.iecube.iecubetutorial.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    public static String encryptStringWithSHA256(String input) {
        try {
            // 创建 MessageDigest 实例，使用 SHA-256 算法
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将字符串转换为字节数组，并进行加密
            byte[] encodedHash = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
