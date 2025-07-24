package com.iecube.iecubetutorial.model.htmlEditAi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.htmlEditAi.service.RegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

    @Value("${HtmlEditAI.server.url}")
    private String url;

    @Value("${HtmlEditAI.server.key}")
    private String key;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String register(){
        // 构建请求体对象
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("key", key);
        RestTemplate restTemplate = new RestTemplate();
        try{
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
            if(response.getStatusCode().value() != 200){
                throw new Exception("注册HtmlEditAI用户异常");
            }
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("data").get("user_id").asText();
        }catch (Exception e){
            throw new ServiceException("服务异常:"+e.getMessage());
        }
    }
}
