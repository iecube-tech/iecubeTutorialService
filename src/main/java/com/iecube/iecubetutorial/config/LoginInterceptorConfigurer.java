package com.iecube.iecubetutorial.config;

import com.iecube.iecubetutorial.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoginInterceptorConfigurer implements WebMvcConfigurer {

    private AuthInterceptor authInterceptor;

    @Autowired
    public void InterceptorConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器注册
        //配置白名单： List集合
        // addPathPatterns("表示要拦截的url是什么").excludePathPatterns("list集合 表示白名单")
        List<String> patterns = new ArrayList<>();
        patterns.add("/sms/code/send");
        patterns.add("/invitation/code/apply");
        patterns.add("/account/register");
        patterns.add("/account/hasregister");
        patterns.add("/account/login");
        patterns.add("/swagger-ui/**");
        patterns.add("/v3/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(patterns);
    }
}
