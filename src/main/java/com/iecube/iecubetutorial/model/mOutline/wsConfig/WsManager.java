package com.iecube.iecubetutorial.model.mOutline.wsConfig;

import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class WsManager {
    /**
     * chatId -- 与w6连接的websocket session
     * @return ConcurrentHashMap
     */
    @Bean
    public ConcurrentHashMap<String, WebSocketSession> OutlineW6WSMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * chatId--outline前端Session
     * @return ConcurrentHashMap
     */
    @Bean
    public ConcurrentHashMap<String, WebSocketSession> OutlineWsMap() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 一键生成对应关系
     * chatId -- MOutline
     * @return ConcurrentHashMap
     */
    @Bean
    public ConcurrentHashMap<String, MaterialChat> OneClickGen(){
        return new ConcurrentHashMap<>();
    }

    /**
     * 先看大纲列表
     * @return ConcurrentHashMap
     */
    @Bean
    public ConcurrentHashMap<String, MOutline> lookOutline(){
        return new ConcurrentHashMap<>();
    }

    @Bean
    public BlockingQueue<String> NewOutlineConnectTask(){
        return new LinkedBlockingQueue<>();
    }
}
