package com.iecube.iecubetutorial.model.ai.apiService.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.model.ai.apiService.W6ApiService;
import com.iecube.iecubetutorial.model.ai.exception.AiAPiResponseException;
import com.iecube.iecubetutorial.util.xlsx.CheckHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class W6ApiServiceImpl implements W6ApiService {

    @Value("${Ai.baseUrl}")
    private String baseUrl;

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    @Value("${Ai.model.procedure}")
    private String modelProcedure;

    @Value("${Ai.model.llm}")
    private String modelLlm;

    @Value("${Ai.model.llm_short}")
    private String modelLlmShort;

    @Value("${Ai.module.name}")
    private String moduleName;

    @Override
    public String genChat() {
        String uri;
        if(modelProcedure.equals("default")){
            uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .toUriString();
        }else {
            uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/chat")
                    .queryParam("procedure", modelProcedure)
                    .queryParam("llm", modelLlm)
                    .queryParam("llm_short", modelLlmShort)
                    .toUriString();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                return checkResult.getBodyData().get("chat_id").asText();
            }else {
                throw new AiAPiResponseException("访问AI资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }

    }

    @Override
    public void usePageMaker(String chatId, String title, String knowledgePoints, String instruction) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/agent").toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        // 构建请求体对象
        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("title", title);
        payloadMap.put("knowledge_points", knowledgePoints);
        payloadMap.put("pagemaking_instruction", instruction);
        requestBodyMap.put("payload", payloadMap);
        requestBodyMap.put("agent_name", "pagemaker");
        requestBodyMap.put("chat_id", chatId);
        requestBodyMap.put("llm_model_override", modelLlm);
        requestBodyMap.put("module_name", moduleName);
        requestBodyMap.put("module_source", null);
        RestTemplate restTemplate = new RestTemplate();
        // 创建 ObjectMapper 实例用于将 Java 对象转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody,headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("动态讲义(pagemaker):{},{},{}",chatId, title, knowledgePoints);
            }else {
                log.warn("动态讲义(pagemaker):{};{};{}",chatId, title, knowledgePoints);
                throw new AiAPiResponseException("访问AI资源失败(响应错误)："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("动态讲义(pagemaker):{};{};{}",chatId, title, knowledgePoints);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
       }
    }

    @Override
    public JsonNode getJsonRes(String artefactId) {
        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "/interact/artefact/"+artefactId)
                .queryParam("include_content", true)
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json; charset=utf-8");
        headers.add(headerFiled, headerVal);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            CheckHttpResponse.CheckResult checkResult = new CheckHttpResponse().responseNormal(response);
            if(checkResult.isNormal()){
                log.info("获取ai message json格式 {}", artefactId);
                return checkResult.getBodyData();
            }else {
                log.warn("获取ai message json格式 WARNING {}", artefactId);
                throw new AiAPiResponseException("访问AI资源失败："+checkResult.getErrorReason());
            }
        }catch (Exception e){
            log.error("获取ai message json格式 ERROR {}", artefactId);
            throw new AiAPiResponseException("访问AI资源失败："+e.getMessage());
        }
    }
}
