package com.iecube.iecubetutorial.model_admin.user.service.impl;

import com.iecube.iecubetutorial.config.JwtUtil;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.exception.PhoneUnavailableException;
import com.iecube.iecubetutorial.exception.UpdateException;
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

import java.time.Instant;
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
        String cacheKey = "TUTORIAL:CODE:ADMIN:" + phone;
        if(smsService.sendLoginSms(phone, code)){
            redisService.set(cacheKey, code, CodeExpire);
        }
    }

    @Override
    public TokenDto Login(ALoginQo ALoginQo) {
        String cacheKey = "TUTORIAL:CODE:ADMIN:" + ALoginQo.getPhone();
        String cacheCode = redisService.get(cacheKey);
        if (cacheCode == null || !cacheCode.equals(ALoginQo.getCode())) {
            throw new AuthException("验证码错误或已过期");
        }
        AUser aUser = aUserMapper.getUserByPhone(ALoginQo.getPhone());
        if (aUser == null) {
            throw new AuthException("管理员不存在");
        }
        Map<String, String> tokens = tokenService.generateTokenPair(UserType, ALoginQo.getPhone(), null, aUser.getRole());
        redisService.delete(cacheKey);
        return new TokenDto(tokens.get("accessToken"), tokens.get("refreshToken"), aUser);
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
        AUser aUser = aUserMapper.getUserByPhone(phone);
        return new TokenDto(newTokens.get("accessToken"), newTokens.get("refreshToken"), aUser);
    }

    @Override
    public AUser CreateUser(AUserQo aUserQo, String operator) {
        AUser existUser = aUserMapper.getUserByPhone(aUserQo.getPhone());
        if(existUser!=null && existUser.getRemoved().equals(0)){
            throw new PhoneUnavailableException("电话号码已存在");
        }
        if(existUser!=null && existUser.getRemoved().equals(1)){
            existUser.setName(aUserQo.getName());
            existUser.setRole(aUserQo.getRole());
            existUser.setRemoved(0);
            int res = aUserMapper.updateUser(existUser);
            if(res!=1){
                throw new UpdateException("更新数据异常");
            }
            return existUser;
        }
        AUser aUser = new AUser();
        aUser.setName(aUserQo.getName());
        aUser.setPhone(aUserQo.getPhone());
        aUser.setRole(aUserQo.getRole());
        aUser.setStatus(UserStatus.ENABLED.getStatus());
        aUser.setRemoved(0);
        aUser.setCreator(operator);
        aUser.setCreateTime(Instant.now());
        aUser.setLastOperator(operator);
        aUser.setLastOperateTime(Instant.now());
        int res = aUserMapper.addUser(aUser);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return aUser;
    }

    @Override
    public AUser UpdateUser(AUserQo aUserQo, String operator) {
        AUser user = this.getUserByPhone(aUserQo.getPhone());
        if(user==null){
            throw new UpdateException("请求的数据不存在");
        }
        user.setName(aUserQo.getName());
        user.setRole(aUserQo.getRole());
        user.setLastOperator(operator);
        user.setLastOperateTime(Instant.now());
        int res = aUserMapper.updateUser(user);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return user;
    }

    @Override
    public AUser getUserByPhone(String phone) {
        return aUserMapper.getUserByPhone(phone);
    }

    @Override
    public List<AUser> deleteUser(AUserQo aUserQo, String operator) {
        AUser user = this.getUserByPhone(aUserQo.getPhone());
        user.setLastOperator(operator);
        user.setLastOperateTime(Instant.now());
        int res = aUserMapper.deleteUser(user.getPhone(), ThreadLocalUtil.getPhone(), Instant.now());
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return this.GetAllUsers();
    }

    @Override
    public AUser disableUser(AUserQo aUserQo, String operator) {
        AUser user = this.getUserByPhone(aUserQo.getPhone());
        user.setStatus(UserStatus.DISABLED.getStatus());
        user.setLastOperator(operator);
        user.setLastOperateTime(Instant.now());
        int res = aUserMapper.updateUser(user);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return user;
    }

    @Override
    public AUser enableUser(AUserQo aUserQo, String operator) {
        AUser user = this.getUserByPhone(aUserQo.getPhone());
        user.setStatus(UserStatus.ENABLED.getStatus());
        user.setLastOperator(operator);
        user.setLastOperateTime(Instant.now());
        int res = aUserMapper.updateUser(user);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return user;
    }

    @Override
    public List<AUser> GetAllUsers() {
        return aUserMapper.getAllUsers();
    }

    @Override
    public List<AUser> GetAdminUsers() {
        return aUserMapper.getAdminUsers();
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
