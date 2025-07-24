package com.iecube.iecubetutorial.model.htmlEditAi.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model.htmlEditAi.config.WebsocketManager;
import com.iecube.iecubetutorial.model.htmlEditAi.dto.MessageDto;
import com.iecube.iecubetutorial.model.htmlEditAi.entity.ProjectMessage;
import com.iecube.iecubetutorial.model.htmlEditAi.enums.MessageType;
import com.iecube.iecubetutorial.model.htmlEditAi.service.MessageService;
import com.iecube.iecubetutorial.model.htmlEditAi.service.RegisterService;
import com.iecube.iecubetutorial.model.htmlEditAi.service.SocketIOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final MessageService messageService;
    private final SocketIOService socketIOService;
    private final WebsocketManager websocketManager;
    private final ObjectMapper objectMapper;
    private final RegisterService registerService;

    public WebSocketHandler(MessageService messageService,
                            SocketIOService socketIOService,
                            WebsocketManager websocketManager,
                            ObjectMapper objectMapper,
                            RegisterService registerService) {
        this.messageService = messageService;
        this.socketIOService = socketIOService;
        this.websocketManager = websocketManager;
        this.objectMapper = objectMapper;
        this.registerService = registerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri != null){
            String path = uri.toString();
            String projectId = path.substring(path.lastIndexOf('/') + 1);
            session.setTextMessageSizeLimit(10485760);
            session.getAttributes().put("projectId", projectId);
            String socketIOUserId = registerService.register();// 请求一个socketIO的userId
            session.getAttributes().put("socketIOUserId", socketIOUserId);
            String sessionId = session.getId();
            log.info("WebSocket客户端已连接: {}", sessionId);
            websocketManager.existWebSocketSessionMap().put(sessionId, session);
            socketIOService.connect(sessionId); // 建立与C的Socket.IO连接
            // 返回 历史消息
            List<ProjectMessage> current = messageService.getProjectMessage(projectId);
            ProjectMessage projectMessage = new ProjectMessage();
            projectMessage.setType(MessageType.current.name());
            projectMessage.setProjectId(projectId);
            projectMessage.setCurrent(current);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(projectMessage)));
        }else {
            session.close(CloseStatus.GOING_AWAY);
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = session.getId();
        String payload = message.getPayload();
        log.debug("收到来自客WebSocket户端{}的消息: {}", sessionId, payload);
        log.debug(String.valueOf(payload.getClass()));
        try{
            // 处理并存储消息  json --> messageDto
            MessageDto MessageDto = messageService.formatWebSocketMessage(sessionId, payload);
            messageService.saveMessage(session, MessageDto, "user");
            // 转发
            socketIOService.sendMessage(sessionId, MessageDto);
        }catch (Exception e){
            ProjectMessage msg = new ProjectMessage();
            msg.setType(MessageType.error.name());
            msg.setProjectId(session.getAttributes().get("projectId").toString());
            msg.setContent(e.getMessage());
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            } catch (IOException ex) {
                try{
                    session.close(CloseStatus.SERVER_ERROR);
                }catch (Exception e2){
                    log.error(e2.getMessage());
                }
            }
        }


        // 转发到Socket.IO服务器C
//        socketIOService.sendMessage(sessionId, formattedMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        websocketManager.existWebSocketSessionMap().remove(sessionId);
        log.info("WebSocket：{}连接断开，状态码{}", sessionId, status);
        // 断开与C的Socket.IO连接
        socketIOService.disconnect(sessionId);
    }


}
