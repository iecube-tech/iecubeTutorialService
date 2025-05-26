package com.iecube.iecubetutorial.util.xlsx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;


public class CheckHttpResponse {

    public static class CheckResult{
        private boolean isNormal;
        @Getter
        @Setter
        private String errorReason;
        @Getter
        @Setter
        private JsonNode bodyData;

        public boolean isNormal() {
            return isNormal;
        }

        public void setNormal(boolean normal) {
            isNormal = normal;
        }

    }

    public CheckResult responseNormal(ResponseEntity<String> response) {
        if(response.getStatusCode().value() != 200){
            CheckResult result = new CheckResult();
            result.setNormal(false);
            result.setErrorReason("响应码非200");
            return result;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            CheckResult result = new CheckResult();
            result.setNormal(true);
            result.setBodyData(jsonNode);
            return result;
        }catch (Exception e){
            CheckResult result = new CheckResult();
            result.setNormal(false);
            result.setErrorReason("解析Response Body异常");
            return result;
        }
    }
}
