package com.iecube.iecubetutorial.model.htmlEditAi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebsocketManager {

    /**
     * ai对话修改html文本内容的前段websocket
     * @return ConcurrentHashMap
     */
    @Bean
    public ConcurrentHashMap<String, WebSocketSession> existWebSocketSessionMap() {
        return new ConcurrentHashMap<>();
    }
}
