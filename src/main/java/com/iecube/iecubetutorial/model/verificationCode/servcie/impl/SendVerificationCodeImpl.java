package com.iecube.iecubetutorial.model.verificationCode.servcie.impl;

import com.iecube.iecubetutorial.model.invitationCode.exception.InvalidCodeException;
import com.iecube.iecubetutorial.model.sms.service.SmsService;
import com.iecube.iecubetutorial.model.user.entity.User;
import com.iecube.iecubetutorial.model.user.exception.PhoneUnavailableException;
import com.iecube.iecubetutorial.model.user.mapper.UserMapper;
import com.iecube.iecubetutorial.model.verificationCode.servcie.SendVerificationCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SendVerificationCodeImpl implements SendVerificationCode {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserMapper userMapper;

    // 验证码有效期5分钟
    private static final long CODE_EXPIRE_TIME = 5;

    // 验证码长度
    private static final int CODE_LENGTH = 6;

    /**
     * 生成并发送验证码
     * @param phoneNumber 手机号
     * @param usage 用途
     */
    @Override
    public void generateAndSendCode(String phoneNumber, String usage) {
        // 生成6位随机验证码
        String code = generateCode();
        boolean sendResult = switch (usage) {
            case "login" -> genAndSendLoginCode(phoneNumber, code);
            case "register" -> genAndSendRegisterCode(phoneNumber, code);
            default -> throw new InvalidCodeException("不支持的操作");
        };
        // 发送短信
        if (!sendResult) {
            throw new InvalidCodeException("服务异常，发送验证码错误");
        }
        // 存储到Redis，设置过期时间
        String key = usage+"_verification_code:" + phoneNumber;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRE_TIME, TimeUnit.MINUTES);
        // todo删除  运营商审核未通过，短信发送会失败，输出验证码
        log.info("发送短信：{}，{}，{}", phoneNumber, usage, code);
    }

    private boolean genAndSendRegisterCode(String phoneNumber, String code) {
        // 校验手机号是否已经注册
        User user = userMapper.getUserByPhone(phoneNumber);
        if(user!=null){
            throw new PhoneUnavailableException("该手机号已注册，请登录");
        }
        return smsService.sendRegisterSms(phoneNumber, code);
    }

    private boolean genAndSendLoginCode(String phoneNumber, String code) {
        // 校验手机号是否已注册
        User user = userMapper.getUserByPhone(phoneNumber);
        if(user==null){
            throw new PhoneUnavailableException("该手机号尚未注册，请注册");
        }
        return smsService.sendLoginSms(phoneNumber, code);
    }

    /**
     * 验证验证码
     * @param phoneNumber 手机号
     * @param usage 用途
     * @param code 验证码
     * @return 是否验证通过
     */
    @Override
    public boolean verifyCode(String phoneNumber, String usage, String code) {
        String key = usage+"_verification_code:" + phoneNumber;
        String correctCode = redisTemplate.opsForValue().get(key);
        if (correctCode == null) {
            throw new InvalidCodeException("验证码不存在或已过期");
        }
        if (correctCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            return true;
        }
        throw new InvalidCodeException("验证码错误");
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
