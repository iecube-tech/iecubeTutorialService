package com.iecube.iecubetutorial.config;

import com.iecube.iecubetutorial.interceptor.AuthInterceptor;
import com.iecube.iecubetutorial.interceptor.PermissionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoginInterceptorConfigurer implements WebMvcConfigurer {

    private AuthInterceptor authInterceptor;
    private PermissionInterceptor permissionInterceptor;

    @Autowired
    public void InterceptorConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Autowired
    public void PermissionInterceptor(PermissionInterceptor permissionInterceptor) {
        this.permissionInterceptor = permissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器注册
        //配置白名单： List集合
        // addPathPatterns("表示要拦截的url是什么").excludePathPatterns("list集合 表示白名单")
        List<String> patterns = new ArrayList<>();
        patterns.add("/swagger-ui/**");
        patterns.add("/v3/**");
        patterns.add("/auth/login");
        patterns.add("/auth/refresh");
        patterns.add("/su/auth/login");
        patterns.add("/su/auth/refresh");
        patterns.add("/file");
//        patterns.add("/collection/{id}");
//        patterns.add("/project/detail");
//        patterns.add("/project/edit");

        List<String> noPermissionList = new ArrayList<>();
        noPermissionList.add("/su/auth/relogin");


        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(patterns);
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**").excludePathPatterns(patterns).excludePathPatterns(noPermissionList);
    }
}
