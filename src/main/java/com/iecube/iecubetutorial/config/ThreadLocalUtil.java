package com.iecube.iecubetutorial.config;

import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.model_admin.user.enmu.AUserRole;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    public static void echo() {
        System.out.println(THREAD_LOCAL);
    }

    // 快捷方法
    public static String getPhone() {
        if(get("phone") == null){
            log.error("当前用户手机号为空");
            throw new AuthException("没有权限");
        }
        return (String) get("phone");
    }

    public static String getUserType() {
        return (String) get("userType");
    }

    public static Long getAccountId() {
        List<String > managerList = new ArrayList<>();
        managerList.add(AUserRole.OPERATOR.getRole());
        managerList.add(AUserRole.ADMIN.getRole());
        managerList.add(AUserRole.SUPER.getRole());
        if(get("accountId") == null && !managerList.contains(getUserType())){
            log.error("当前账户ID号为空");
            throw new AuthException("没有权限");
        }
        if(get("accountId") == null){
            return null;
        }
        return Long.valueOf((Integer) get("accountId"));
    }

    public static String getRole() {
        return (String) get("role");
    }
}