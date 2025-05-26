package com.iecube.iecubetutorial.util.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.iecube.iecubetutorial.model.user.exception.AuthException;
import com.iecube.iecubetutorial.util.exception.SystemException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 网络层工具类
 *
 * @author panghaoyue
 */
@Slf4j
public class AuthUtils {

    public static final String ACCESS_TOKE_KEY = "x-access-token";

    private static final String USER_REDIS_KEY_PIX = "tutorial_USER_";
    private static final String USER_TOKEN_REDIS_KEY_PIX = "tutorial_USER_TOKEN_";

    private static final ThreadLocal<CurrentUser> LOCAL_USER = new ThreadLocal<>();


    public AuthUtils() {
    }

    public String createToken(Long userId, String phone) {
        HashMap<String,Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("phone", phone);
        return new JwtUtil().createToken(claims);
    }

    public static void rm(StringRedisTemplate redisTemplate) {
        redisTemplate.delete(getUserRedisKey(getCurrentUserId(), getCurrentUserPhone()));
        redisTemplate.delete(getUserTokenRedisKey(getCurrentUserId(), getCurrentUserPhone()));
    }

    public static void cache(CurrentUser currentUser, String token, StringRedisTemplate redisTemplate) {
        try {
            redisTemplate.opsForValue()
                    .set(
                            getUserRedisKey(currentUser.getId(), currentUser.getPhone()),
                            new ObjectMapper().writeValueAsString(currentUser),
                            48,
                            TimeUnit.HOURS
                    );
        } catch (JsonProcessingException e) {
            log.error("转JSON失败", e);
            throw new SystemException();
        }
        redisTemplate.opsForValue()
                .set(
                        getUserTokenRedisKey(currentUser.getId(), currentUser.getPhone()),
                        token,
                        48,
                        TimeUnit.HOURS
                );
    }

    public static void setCurrentUser(Long id, String phone, StringRedisTemplate redisTemplate) {
        CurrentUser user = getAuthInfo(id, phone, redisTemplate);
        LOCAL_USER.set(user);
    }

    public static CurrentUser getAuthInfo(Long id, String phone, StringRedisTemplate redisTemplate) {
        String userJson = redisTemplate.opsForValue().get(getUserRedisKey(id,phone));
        try {
            return new ObjectMapper().readValue(userJson, CurrentUser.class);
        } catch (IOException e) {
            log.error("解析JSON失败", e);
            throw new SystemException();
        }
    }

    public static boolean authed(String token, StringRedisTemplate redisTemplate) {
        Long id;
        String phone;
        try {
            id = Long.valueOf((Integer) new JwtUtil().getClaims(token).get("id"));
            phone = (String) new JwtUtil().getClaims(token).get("phone");
        } catch (Exception e) {
            log.warn("登录过期");
            return false;
        }
        String t = redisTemplate.opsForValue().get(getUserTokenRedisKey(id,phone));
        if (t != null && t.equals(token)) {
            setCurrentUser(id, phone, redisTemplate);
            flushExpireTime(redisTemplate);
            return true;
        }
        return false;
    }

    public static void flushExpireTime(StringRedisTemplate redisTemplate) {
        if(redisTemplate.getExpire(getUserRedisKey(getCurrentUserId(), getCurrentUserPhone()))< (60*60*24)
                | redisTemplate.getExpire(getUserTokenRedisKey(getCurrentUserId(), getCurrentUserPhone()))<(60*60*24)){
            redisTemplate.expire(getUserRedisKey(getCurrentUserId(), getCurrentUserPhone()), 48, TimeUnit.HOURS);
            redisTemplate.expire(getUserTokenRedisKey(getCurrentUserId(), getCurrentUserPhone()), 48, TimeUnit.HOURS);
        }
    }

    public static CurrentUser getCurrentUser() {
        CurrentUser userDTO = LOCAL_USER.get();
        if (userDTO == null) {
            throw new AuthException("认证不通过");
        }
        return userDTO;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static String getCurrentUserPhone(){
        return getCurrentUser().getPhone();
    }

    private static String getUserRedisKey(Long userId, String phone) {
        return (USER_REDIS_KEY_PIX + userId.toString() + "_" + phone).toUpperCase();
    }

    private static String getUserTokenRedisKey(Long userId, String phone) {
        return (USER_TOKEN_REDIS_KEY_PIX + userId.toString() + "__" + phone).toUpperCase();
    }
}
