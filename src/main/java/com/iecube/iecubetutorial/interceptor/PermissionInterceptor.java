package com.iecube.iecubetutorial.interceptor;

import com.iecube.iecubetutorial.Auth.ApiPermissions;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 请求路径权限拦截器
 * 系统设定 管理员和用户
 * 管理员角色分为
 * - SUPER：/sm/s/**
 * - ADMIN：/sm/m/**
 * - OPERATOR：/sm/o/**
 * 用户角色分为
 *  - USER_M：/su/m
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
        if(role == null || role.isEmpty()){
            System.out.println("n");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限");
            return false;
        }
        Long accountId = ThreadLocalUtil.getAccountId();
        String phone = ThreadLocalUtil.getPhone();
        log.info("[REQUEST] {},{},{},{},{},{}", userType, role, accountId, phone, method, requestPath);
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if(checkApiPermissions(handlerMethod)){
            return true;
        }else{
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限");
            return false;
        }
    }

    /**
     * 检查ApiPermissions权限
     */
    private boolean checkApiPermissions(HandlerMethod handlerMethod) {
        // 获取方法上的注解
        ApiPermissions methodAnnotation = handlerMethod.getMethodAnnotation(ApiPermissions.class);
        // 获取类上的注解
        ApiPermissions classAnnotation = handlerMethod.getBeanType().getAnnotation(ApiPermissions.class);
        // 优先使用方法注解，如果没有则使用类注解
        ApiPermissions annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        if (annotation != null) {
            String currentUserRole = ThreadLocalUtil.getRole();
            // 获取允许的用户类型列表
            List<String> allowedUserTypes = Arrays.asList(annotation.value());
            // 如果没有配置用户类型，则检查角色配置
            if (allowedUserTypes.isEmpty()) {
                // 角色检查会在下面的checkRequireRoles方法中处理
                return true;
            }
            // 检查当前用户类型是否在允许列表中
            return allowedUserTypes.contains(currentUserRole);
        }
        return true;
    }
}
