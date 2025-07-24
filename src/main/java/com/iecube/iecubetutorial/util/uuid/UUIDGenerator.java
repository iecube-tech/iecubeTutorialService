package com.iecube.iecubetutorial.util.uuid;

import java.util.UUID;

public class UUIDGenerator {
    public static String generateUUID() {
        // 生成标准UUID
        UUID uuid = UUID.randomUUID();
        // 移除UUID中的横线，符合MySQL的binary(16)存储格式
        return uuid.toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(generateUUID());
    }
}