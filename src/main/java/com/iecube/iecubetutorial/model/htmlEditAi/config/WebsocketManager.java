package com.iecube.iecubetutorial.model.htmlEditAi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebsocketManager {

    @Bean
    /*
      map<websocketSessionId websocketSession>
     */
    public ConcurrentHashMap<String, WebSocketSession> existWebSocketSessionMap() {
        return new ConcurrentHashMap<>();
    }
}
