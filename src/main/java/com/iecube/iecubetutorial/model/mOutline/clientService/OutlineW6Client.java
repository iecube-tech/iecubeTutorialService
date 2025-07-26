package com.iecube.iecubetutorial.model.mOutline.clientService;

import com.iecube.iecubetutorial.model.ai.exception.AiAPiResponseException;
import com.iecube.iecubetutorial.model.mOutline.wsConfig.WsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;

@Slf4j
@Component
public class OutlineW6Client implements Runnable{

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;


    private final OutlineGenHandler outlineGenHandler;
    private final WsManager wsManager;

    public OutlineW6Client(OutlineGenHandler outlineGenHandler, WsManager wsManager) {
        this.outlineGenHandler = outlineGenHandler;
        this.wsManager = wsManager;
    }


    @Override
    public void run() {
        log.info("outline w6-connect-->running");
        while(true){
            try{
                String chatId = wsManager.NewOutlineConnectTask().take();
                webSocketConnect(chatId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void webSocketConnect(String chatId) {
        String url = wssBaseUrl+chatId;
        log.debug("url:{}",url);
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(headerFiled, headerVal);
        try {
            URI uri = new URI(url);
            log.debug("uri:{}",uri);
            WebSocketSession session = client.execute(outlineGenHandler, headers, uri).get(); // 和AI模型建立消息通道
            session.setTextMessageSizeLimit(10485760);
        } catch (Exception e) {
            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
        }
    }
}
