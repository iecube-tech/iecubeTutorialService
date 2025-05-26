package com.iecube.iecubetutorial.model.ai.consumer;

import com.iecube.iecubetutorial.model.ai.exception.AiAPiResponseException;
import com.iecube.iecubetutorial.model.ai.wsconfig.AiClientWebSocketHandler;
import com.iecube.iecubetutorial.model.materials.entity.MaterialChat;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConnectToW6 implements Runnable {

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    @Autowired
    private AiClientWebSocketHandler w6WebSocketHandler;

    @Autowired
    private MaterialService materialService;

    private final ConcurrentHashMap<String, WebSocketSession> ChatIdToSession;
    private final ConcurrentHashMap<String, String>SessionIdToChatId;
    private final ConcurrentHashMap<String, MaterialEntity> ChatIdToMaterial;
    private final BlockingQueue<MaterialChat> NewConnectTask;

    public ConnectToW6(ConcurrentHashMap<String, WebSocketSession> ChatIdToSession,
                       ConcurrentHashMap<String, String>SessionIdToChatId,
                       ConcurrentHashMap<String, MaterialEntity> ChatIdToMaterial,
                       BlockingQueue<MaterialChat> NewConnectTask
                       ){
        log.info("w6-connect-->start");
        this.ChatIdToSession = ChatIdToSession;
        this.SessionIdToChatId = SessionIdToChatId;
        this.ChatIdToMaterial=ChatIdToMaterial;
        this.NewConnectTask = NewConnectTask;
    }

    @Override
    public void run() {
        log.info("w6-connect-->running");
        //使用异步IO 处理webSocket 连接
        while(true){
            try{
                String chatId = NewConnectTask.take().getChatId();
                MaterialEntity material = materialService.getMaterial(chatId);
                ChatIdToMaterial.put(chatId,material);
                log.info("有 {} 个进行中的w6 webSocket 连接",ChatIdToSession.size());
                log.info("新的 w6 webSocket 连接:{}", chatId);
                webSocketConnect(chatId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void webSocketConnect(String chatId) {
        String url = wssBaseUrl+chatId;
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(headerFiled, headerVal);
        try {
            URI uri = new URI(url);
            WebSocketSession session = client.doHandshake(w6WebSocketHandler, headers, uri).get();
            ChatIdToSession.put(chatId,session);
            SessionIdToChatId.put(session.getId(), chatId);
            session.setTextMessageSizeLimit(10485760);
        } catch (Exception e) {
            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
        }
    }
}
