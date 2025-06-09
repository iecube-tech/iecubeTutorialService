package com.iecube.iecubetutorial.model_admin.user.service.impl;

import com.iecube.iecubetutorial.config.JwtUtil;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.exception.PhoneUnavailableException;
import com.iecube.iecubetutorial.model.sms.service.SmsService;
import com.iecube.iecubetutorial.model_admin.user.enmu.UserStatus;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_admin.user.mapper.AUserMapper;
import com.iecube.iecubetutorial.model_admin.user.qo.ALoginQo;
import com.iecube.iecubetutorial.model_admin.user.qo.AUserQo;
import com.iecube.iecubetutorial.model_admin.user.service.AUserService;
import com.iecube.iecubetutorial.redis.RedisService;
import com.iecube.iecubetutorial.token.TokenDto;
import com.iecube.iecubetutorial.token.TokenService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class AUserServiceImpl implements AUserService {
    @Autowired
    private AUserMapper aUserMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${redis.code-expire}")
    private Long CodeExpire;

    private final static int CODE_LENGTH=6;

    private final static String UserType="ADMIN";

    @Override
    public void sendVCode(String phone) {
        AUser aUser = aUserMapper.getUserByPhone(phone);
        if(aUser==null || aUser.getRemoved().equals(1) || aUser.getStatus().equals(UserStatus.DISABLED.getStatus())){
          throw new PhoneUnavailableException("该手机号不可用");
        }
        String code = generateCode();
        String cacheKey = "CODE:ADMIN:" + phone;
        if(smsService.sendLoginSms(phone, code)){
            redisService.set(cacheKey, code, CodeExpire);
        }
    }

    @Override
    public TokenDto Login(ALoginQo loginQo) {
        String cacheKey = "CODE:ADMIN:" + loginQo.getPhone();
        String cacheCode = redisService.get(cacheKey);
        if (cacheCode == null || !cacheCode.equals(loginQo.getCode())) {
            throw new AuthException("验证码错误或已过期");
        }
        AUser aUser = aUserMapper.getUserByPhone(loginQo.getPhone());
        if (aUser == null) {
            throw new AuthException("管理员不存在");
        }
        Map<String, String> tokens = tokenService.generateTokenPair(UserType, loginQo.getPhone(), null, aUser.getRole());
        redisService.delete(cacheKey);
        return new TokenDto(tokens.get("accessToken"), tokens.get("refreshToken"));
    }

    @Override
    public TokenDto refreshToken(String refreshToken) {
        // 解析Refresh Token获取基本信息
        Claims claims = jwtUtil.parseToken(refreshToken);
        String phone = (String) claims.get("phone");
        String userType = (String) claims.get("userType");
        String role = (String) claims.get("role");
        Long accountId = (Long) claims.get("accountId");
        // 验证Refresh Token有效性
        if (!tokenService.validateRefreshToken(userType, phone, refreshToken)) {
            throw new AuthException("刷新令牌无效或已过期");
        }
        // 生成新的Token对
        Map<String, String> newTokens = tokenService.refreshToken(userType,phone,accountId,role);
        return new TokenDto(newTokens.get("accessToken"), newTokens.get("refreshToken"));
    }

    @Override
    public AUser CreateUser(AUserQo aUserQo, String operator) {

        return null;
    }

    @Override
    public AUser UpdateUser(AUser user, String operator) {
        return null;
    }

    @Override
    public AUser getUserByPhone(String phone) {
        return null;
    }

    @Override
    public AUser deleteUser(AUser user, String operator) {
        return null;
    }

    @Override
    public List<AUser> GetAllUsers() {
        return List.of();
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
