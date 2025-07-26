package com.iecube.iecubetutorial.model.mOutline.clientService.impl;

import com.iecube.iecubetutorial.model.ai.exception.AiAPiResponseException;
import com.iecube.iecubetutorial.model.mOutline.clientService.OutlineGenHandler;
import com.iecube.iecubetutorial.model.mOutline.clientService.W6ClientService;
import com.iecube.iecubetutorial.model.mOutline.wsConfig.WsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;

@Slf4j
@Service
public class W6ClientServiceImpl implements W6ClientService {

    @Value("${Ai.wssBaseUrl}")
    private String wssBaseUrl;

    @Value("${Ai.header.auth.field}")
    private String headerFiled;

    @Value("${Ai.header.auth.val}")
    private String headerVal;

    private final WsManager wsManager;
    private final OutlineGenHandler outlineGenHandler;
    public W6ClientServiceImpl(WsManager wsManager, OutlineGenHandler outlineGenHandler) {
        this.wsManager = wsManager;
        this.outlineGenHandler = outlineGenHandler;
    }

    /**
     * 建立和w6的socket连接
     * @param chatId chatId
     */
    @Override
    public WebSocketSession connect(String chatId) throws AiAPiResponseException{
        String url = wssBaseUrl+chatId;
        log.debug("url:{}",url);
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add(headerFiled, headerVal);
        log.info("大纲生成：{}开始前检查，前端连接数量：{}， w6连接数量：{}",chatId, wsManager.OutlineWsMap().size(), wsManager.OutlineW6WSMap().size());
        try {
            URI uri = new URI(url);
            log.debug("uri:{}",uri);
            WebSocketSession session = client.execute(outlineGenHandler,headers, uri).get();
            log.debug("outline w6 session:{}",session);
            session.getAttributes().put("chatId", chatId);
            wsManager.OutlineW6WSMap().put(chatId, session);
            session.setTextMessageSizeLimit(10485760);
            return session;
        } catch (Exception e) {
            throw new AiAPiResponseException("与AI服务建立消息通道错误："+e.getMessage());
        }
    }
}
