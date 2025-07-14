package com.iecube.iecubetutorial.interceptor;

import com.iecube.iecubetutorial.config.JwtUtil;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.redis.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

/**
 * 认证拦截器 返回的401
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisService redisService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.warn("ip:{} ==> {}", request.getHeader("X-Forwarded-For"), request.getHeader("User-Agent"));

        // token 验证 要验证这个token的fresh token在不在Redis中， 如果redis中没有这个token的refresh的token  则没有登录
//        System.out.println(request.getRequestURI());
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("缺少有效的认证凭证");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少有效的认证凭证");
            return false;
        }
        token = token.substring(7);
        Claims claims = null;
        try{
//            System.out.println("222");
            claims = jwtUtil.parseToken(token);
//            System.out.println("333");
            String phone = (String) claims.get("phone");
            String userType = (String) claims.get("userType");
            String storedToken = redisService.get( "TUTORIAL_"+userType+"_REFRESH_TOKEN:" + phone);
            if (storedToken==null && !request.getRequestURI().equals("/su/auth/relogin")) {
                log.warn("没有找到存储的对应的token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
                return false;
            }
            // 验证Token有效性后，检查是否即将过期
            Date expiration = claims.getExpiration();
            long remainingTime = expiration.getTime() - System.currentTimeMillis();

            // 如果Token将在5分钟内过期，添加刷新提示
            if (remainingTime < 300000) { // 5分钟
                response.setHeader("Token-Status", "NEAR_EXPIRATION");
            }

            if(claims.get("userType").equals("USER")){
                // 用户端完整的登录校验
                if(claims.get("accountId") == null && !request.getRequestURI().equals("/su/auth/relogin")){
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
                    return false;
                }
            }

            // 存储用户上下文
            ThreadLocalUtil.set("phone", phone);
            ThreadLocalUtil.set("userType", claims.get("userType"));
            ThreadLocalUtil.set("accountId", claims.get("accountId"));
            ThreadLocalUtil.set("role", claims.get("role"));
        }catch (Exception e){
            log.warn("登录验证异常: {}", e.getMessage());
            if(e instanceof ExpiredJwtException){
                response.setHeader("Token-Status", "EXPIRATION");
            }
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "请重新登录");
            return false;
        }
        return true;
    }
}
