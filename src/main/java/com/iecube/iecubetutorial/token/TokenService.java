package com.iecube.iecubetutorial.token;

import com.iecube.iecubetutorial.config.JwtUtil;
import com.iecube.iecubetutorial.redis.RedisService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${jwt.access-expiration}")
    private long accessExpiration; // 访问令牌有效期

    @Value("${redis.token-expire}")
    private long tokenExpire; // redis访问令牌有效期

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration; // 刷新令牌有效期

    @Value("${redis.refresh-token-expire}")
    private long refreshTokenExpire; // Redis中刷新令牌有效期

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisService redisService;

    /**
     * 生成用户端Token对（访问令牌+刷新令牌）
     */
    public Map<String, String> generateTokenPair(String userType, String phone, Long accountId, String role) {
        Map<String, String> tokens = new HashMap<>();

        // 生成访问令牌
        String accessToken = generateAccessToken(userType,phone, accountId, role);
        tokens.put("accessToken", accessToken);

        // 生成刷新令牌
        String refreshToken = generateRefreshToken(userType,phone, accountId, role);
        tokens.put("refreshToken", refreshToken);

        // 存储访问令牌到Redis
//        storeAccessToken(userType, phone, accessToken);

        // 存储刷新令牌到Redis
        storeRefreshToken(userType, phone, refreshToken);

        return tokens;
    }

    /**
     * 生成用户访问令牌
     */
    private String generateAccessToken(String userType, String phone, Long accountId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        claims.put("accountId", accountId);
        claims.put("role", role);
        claims.put("userType", userType);
        claims.put("tokenType", "ACCESS");
        return jwtUtil.generateToken("ACCESS", claims, accessExpiration);
    }

    /**
     * 生成刷新令牌
     */
    private String generateRefreshToken(String userType, String phone, Long accountId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("phone", phone);
        claims.put("accountId", accountId);
        claims.put("role", role);
        claims.put("userType", userType);
        claims.put("tokenType", "REFRESH");
        // 使用UUID确保刷新令牌的唯一性
        String tokenId = UUID.randomUUID().toString();
        claims.put("tokenId", tokenId);
        return jwtUtil.generateToken("REFRESH", claims, refreshExpiration);
    }

    /**
     * 存储访问令牌到Redis
     */
    private void storeAccessToken(String userType, String phone, String refreshToken) {
        String key = userType+"_ACCESS_TOKEN:" + phone;
        redisService.set(key, refreshToken, tokenExpire);
    }

    /**
     * 存储刷新令牌到Redis
     */
    private void storeRefreshToken(String userType, String phone, String refreshToken) {
        String key = userType+"_REFRESH_TOKEN:" + phone;
        redisService.set(key, refreshToken, refreshTokenExpire);
    }

    /**
     * 验证刷新令牌有效性
     */
    public boolean validateRefreshToken(String userType, String phone, String refreshToken) {
        String key = userType+"_REFRESH_TOKEN:" + phone;
        String storedToken = redisService.get(key);

        // 检查Redis中是否存在且匹配
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            return false;
        }

        // 检查JWT有效性
        try {
            Claims claims = jwtUtil.parseToken(refreshToken);
            return "REFRESH".equals(claims.get("tokenType")) &&
                    phone.equals(claims.get("phone"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     */
    public Map<String, String> refreshToken(String userType, String phone, Long accountId, String role) {
        return generateTokenPair(userType, phone,accountId,role);
    }
}