package com.iecube.iecubetutorial.model.sms.service.impl;

import com.aliyun.dysmsapi20170525.Client;

import com.aliyun.teaopenapi.models.Config;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import static com.aliyun.teautil.Common.toJSONString;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.model.sms.exception.SendSMSMessageException;
import com.iecube.iecubetutorial.model.sms.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Value("${aliyun.sms.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name}")
    private String signName;  // 短信签名

    @Value("${aliyun.sms.register-template-code}")
    private String registerTemplateCode;  // 你的模板CODE

    @Value("${aliyun.sms.login-template-code}")
    private String loginTemplateCode;  // 你的模板CODE

    private Client createClient() throws Exception {
        Config config = new Config();
        // 配置 AccessKey ID
        config.setAccessKeyId(accessKeyId);
        // 配置 AccessKey Secret
        config.setAccessKeySecret(accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com"; // 配置 Endpoint。中国站使用dysmsapi.aliyuncs.com
        return new Client(config);
    }

    @Override
    public boolean sendRegisterSms(String phoneNumber, String code) {
        try {
            Client client = createClient();
            // 构造API请求对象，请替换请求参数值
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(registerTemplateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}"); // TemplateParam为序列化后的JSON字符串。
            // 获取响应对象
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            // 响应包含服务端响应的 body 和 headers
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode res = objectMapper.readTree(toJSONString(sendSmsResponse));
            return res.get("body").get("code").asText().equals("OK");
        } catch (Exception e) {
            throw new SendSMSMessageException(e.getMessage());
        }
    }

    @Override
    public boolean sendLoginSms(String phoneNumber, String code){
        // 初始化请求客户端
        try {
            Client client = createClient();
            // 构造API请求对象，请替换请求参数值
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(phoneNumber)
                    .setSignName(signName)
                    .setTemplateCode(loginTemplateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}"); // TemplateParam为序列化后的JSON字符串。
            // 获取响应对象
            SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            log.info(sendSmsResponse.toString());
            // 响应包含服务端响应的 body 和 headers
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode res = objectMapper.readTree(toJSONString(sendSmsResponse));
            log.info("111111");
            log.info(res.toString());
            return res.get("body").get("code").asText().equals("OK");
        } catch (Exception e) {
            throw new SendSMSMessageException(e.getMessage());
        }
    }
}


