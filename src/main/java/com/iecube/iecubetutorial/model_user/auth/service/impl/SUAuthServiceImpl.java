package com.iecube.iecubetutorial.model_user.auth.service.impl;

import com.iecube.iecubetutorial.config.JwtUtil;
import com.iecube.iecubetutorial.config.ThreadLocalUtil;
import com.iecube.iecubetutorial.exception.AuthException;
import com.iecube.iecubetutorial.exception.PhoneUnavailableException;
import com.iecube.iecubetutorial.model.sms.service.SmsService;
import com.iecube.iecubetutorial.model_admin.user.enmu.UserStatus;
import com.iecube.iecubetutorial.model_admin.user.entity.AUser;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.auth.dto.AuthDto;
import com.iecube.iecubetutorial.model_user.auth.service.SUAuthService;
import com.iecube.iecubetutorial.model_user.organization_sec.entity.OrgSec;
import com.iecube.iecubetutorial.model_user.organization_sec.service.OrgSecService;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import com.iecube.iecubetutorial.model_user.organization_top.service.OrgTopService;
import com.iecube.iecubetutorial.model_user.user.entity.UUser;
import com.iecube.iecubetutorial.model_user.user.service.UUserService;
import com.iecube.iecubetutorial.redis.RedisService;
import com.iecube.iecubetutorial.token.TokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class SUAuthServiceImpl implements SUAuthService {

    private static final int CODE_LENGTH=6;

    private final static String UserType="USER";

    @Value("${redis.code-expire}")
    private Long CodeExpire;

    @Autowired
    private UUserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrgTopService orgTopService;

    @Autowired
    private OrgSecService orgSecService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void sendCode(String phone) {
        UUser user = userService.getUserByPhone(phone);
        if(user==null || user.getRemoved().equals(1) || user.getStatus().equals(UserStatus.DISABLED.getStatus())){
            throw new PhoneUnavailableException("该手机号不可用");
        }
        List<Account> accountList = accountService.getAccountListByUser(phone);
        if(accountList==null || accountList.isEmpty()){
            throw new PhoneUnavailableException("账户不可用");
        }
        String code = generateCode();
        String cacheKey = "TUTORIAL:CODE:USER:" + phone;
        if(smsService.sendLoginSms(phone, code)){
            redisService.set(cacheKey, code, CodeExpire);
        }
    }

    @Override
    public AuthDto login(String phone, String code){
        String cacheKey = "TUTORIAL:CODE:USER:" + phone;
        String cacheCode = redisService.get(cacheKey);
        if (cacheCode == null || !cacheCode.equals(code)) {
            throw new AuthException("验证码错误或已过期");
        }
        redisService.delete(cacheKey);
        UUser user = userService.getUserByPhone(phone);
        List<Account> accountList = accountService.getAccountListByUser(phone);
        if(accountList.size()==1){
            // 只有一个组织， 返回token
            Account account = accountList.get(0);
            if(account == null || account.getRemoved().equals(1) || account.getStatus().equals(UserStatus.DISABLED.getStatus())){
                throw new PhoneUnavailableException("账户不可用");
            }
            OrgSec orgSec = orgSecService.getById(account.getOSecId());
            if(orgSec==null || orgSec.getRemoved().equals(1) || orgSec.getStatus().equals(UserStatus.DISABLED.getStatus())){
                throw new PhoneUnavailableException("组织账户不可用");
            }
            OrgTop orgTop = orgTopService.getById(orgSec.getPId());
            orgSec.setOrgTop(orgTop);
            // 生成token
            Map<String, String> tokens = tokenService.generateTokenPair(UserType,phone, account.getId(), account.getRole());
            AuthDto authDto = new AuthDto();
            authDto.setLogin(true);
            authDto.setUser(user);
            authDto.setOrgSec(orgSec);
            authDto.setAccount(account);
            authDto.setAccessToken(tokens.get("accessToken"));
            authDto.setRefreshToken(tokens.get("refreshToken"));
            return authDto;
        }
        else {
            // 返回组织列表  选择组织列表重新登录
            List<Long> ids = new ArrayList<>();
            accountList.forEach(account -> {
                ids.add(account.getOSecId());
            });
            List<OrgSec> orgSecList = orgSecService.batchGet(ids);
            orgSecList.forEach(orgSec -> {
                orgSec.setOrgTop(orgTopService.getById(orgSec.getPId()));
            });
            AuthDto authDto = new AuthDto();
            authDto.setLogin(false);
            authDto.setUser(user);
            authDto.setAccessToken(tokenService.generateAccessToken(UserType,phone,null, null));
            authDto.setOrgSecList(orgSecList);
            return authDto;
        }
    }

    @Override
    public AuthDto reLogin(Long orgSecId){
        OrgSec orgSec = orgSecService.getById(orgSecId);
        if(orgSec==null || orgSec.getRemoved().equals(1) || orgSec.getStatus().equals(UserStatus.DISABLED.getStatus())){
            throw new PhoneUnavailableException("组织账户不可用");
        }
        OrgTop orgTop = orgTopService.getById(orgSec.getPId());
        orgSec.setOrgTop(orgTop);
        String phone = ThreadLocalUtil.getPhone();
        Account account = accountService.getAccount(phone, orgSecId);
        if(account == null || account.getRemoved().equals(1) || account.getStatus().equals(UserStatus.DISABLED.getStatus())){
            throw new PhoneUnavailableException("账户不可用");
        }
        UUser user = userService.getUserByPhone(phone);
        // 生成token
        Map<String, String> tokens = tokenService.generateTokenPair(UserType,phone, account.getId(), account.getRole());
        AuthDto authDto = new AuthDto();
        authDto.setLogin(true);
        authDto.setUser(user);
        authDto.setOrgSec(orgSec);
        authDto.setAccount(account);
        authDto.setAccessToken(tokens.get("accessToken"));
        authDto.setRefreshToken(tokens.get("refreshToken"));
        return authDto;
    }

    @Override
    public List<OrgSec> accountOrgSecList(){
        String phone = ThreadLocalUtil.getPhone();
        List<Account> accountList = accountService.getAccountListByUser(phone);
        List<Long> ids = new ArrayList<>();
        accountList.forEach(account -> {
            ids.add(account.getOSecId());
        });
        List<OrgSec> orgSecList = orgSecService.batchGet(ids);
        orgSecList.forEach(orgSec -> {
            orgSec.setOrgTop(orgTopService.getById(orgSec.getPId()));
        });
        return orgSecList;
    }

    @Override
    public AuthDto refreshToken(String refreshToken) {
        // 解析Refresh Token获取基本信息
        Claims claims = jwtUtil.parseToken(refreshToken);
        String phone = (String) claims.get("phone");
        String userType = (String) claims.get("userType");
        String role = (String) claims.get("role");
        Long accountId =  Long.valueOf((Integer)claims.get("accountId"));
        // 验证Refresh Token有效性
        if (!tokenService.validateRefreshToken(userType, phone, refreshToken)) {
            throw new AuthException("刷新令牌无效或已过期");
        }
        // 生成新的Token对
        Map<String, String> newTokens = tokenService.refreshToken(userType,phone,accountId,role);
        Account account = accountService.getAccount(accountId);
        UUser user = userService.getUserByPhone(phone);
        OrgSec orgSec = orgSecService.getById(account.getOSecId());
        OrgTop orgTop = orgTopService.getById(orgSec.getPId());
        orgSec.setOrgTop(orgTop);
        AuthDto authDto = new AuthDto();
        authDto.setLogin(true);
        authDto.setUser(user);
        authDto.setOrgSec(orgSec);
        authDto.setAccount(account);
        authDto.setAccessToken(newTokens.get("accessToken"));
        authDto.setRefreshToken(newTokens.get("refreshToken"));
        return authDto;
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
