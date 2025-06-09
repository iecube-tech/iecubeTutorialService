package com.iecube.iecubetutorial.config;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        THREAD_LOCAL.get().put(key, value);
    }

    public static Object get(String key) {
        return THREAD_LOCAL.get().get(key);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    // 快捷方法
    public static String getPhone() {
        return (String) get("phone");
    }

    public static String getUserType() {
        return (String) get("userType");
    }

    public static Long getAccountId() {
        return (Long) get("accountId");
    }

    public static String getRole() {
        return (String) get("role");
    }
}