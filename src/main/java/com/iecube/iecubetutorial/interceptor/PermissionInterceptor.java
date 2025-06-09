package com.iecube.iecubetutorial.interceptor;

import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求路径权限拦截器
 * 系统设定 管理员和用户
 * 管理员分为
 * - SUPER：/sm/s/**
 * - ADMIN：/sm/m/**
 * - OPERATOR：/sm/o/**
 * 用户分为
 *  - ADMIN：/su/a
 *  - USER：/su/u
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        // 获取用户类型
        String userType = ThreadLocalUtil.getUserType();
        String role = ThreadLocalUtil.getRole();
        Long accountId = ThreadLocalUtil.getAccountId();
        String phone = ThreadLocalUtil.getPhone();
        log.info("[REQUEST] {},{},{},{},{},{}", userType, role, accountId, phone, method, requestPath);
        // 权限校验：用户类型和API路径匹配
        if ("USER".equals(userType) && !requestPath.startsWith("/su")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限");
            return false;
        }
        if ("ADMIN".equals(userType) && !requestPath.startsWith("/sm")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限");
            return false;
        }
        return true;
    }
}
