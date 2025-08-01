package com.iecube.iecubetutorial.model.htmlEditAi.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.model.htmlEditAi.config.WebsocketManager;
import com.iecube.iecubetutorial.model.htmlEditAi.dto.MessageDto;
import com.iecube.iecubetutorial.model.htmlEditAi.entity.ProjectMessage;
import com.iecube.iecubetutorial.model.htmlEditAi.enums.MessageType;
import com.iecube.iecubetutorial.model.htmlEditAi.service.MessageService;
import com.iecube.iecubetutorial.model.htmlEditAi.service.RegisterService;
import com.iecube.iecubetutorial.model.htmlEditAi.service.SocketIOService;
import com.iecube.iecubetutorial.model.project.entity.Project;
import com.iecube.iecubetutorial.model.project.service.ProjectService;
import com.iecube.iecubetutorial.model_user.account.entity.Account;
import com.iecube.iecubetutorial.model_user.account.service.AccountService;
import com.iecube.iecubetutorial.model_user.points.service.PointsService;
import lombok.Data;
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
    private final PointsService pointsService;
    private final ProjectService projectService;
    private final AccountService accountService;

    public WebSocketHandler(MessageService messageService,
                            SocketIOService socketIOService,
                            WebsocketManager websocketManager,
                            ObjectMapper objectMapper,
                            RegisterService registerService,
                            PointsService pointsService,
                            ProjectService projectService,
                            AccountService accountService) {
        this.messageService = messageService;
        this.socketIOService = socketIOService;
        this.websocketManager = websocketManager;
        this.objectMapper = objectMapper;
        this.registerService = registerService;
        this.pointsService = pointsService;
        this.projectService = projectService;
        this.accountService = accountService;
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
            log.info("已返回历史消息");
        }else {
            session.close(CloseStatus.GOING_AWAY);
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = session.getId();
        String payload = message.getPayload();
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            if(jsonNode.get("type").asText().equals("message")){
                log.debug("收到来自客WebSocket户端{}的消息: {}", sessionId, jsonNode);
                log.debug(String.valueOf(payload.getClass()));
                try{
                    Project project = projectService.getById(session.getAttributes().get("projectId").toString());
                    Account account = accountService.getAccount(project.getUserId());
                    if(pointsService.pointsEnough(account)){
                        // 处理并存储消息  json --> messageDto
                        MessageDto MessageDto = messageService.formatWebSocketMessage(sessionId, objectMapper.writeValueAsString(jsonNode.get("message")));
                        messageService.saveMessage(session, MessageDto, "user");
                        // 转发
                        socketIOService.sendMessage(sessionId, MessageDto);
                    }
                }catch (Exception e){
                    ProjectMessage msg = new ProjectMessage();
                    msg.setType(MessageType.error.name());
                    msg.setProjectId(session.getAttributes().get("projectId").toString());
                    msg.setContent(e.getMessage());
                    try {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if(jsonNode.get("type").asText().equals("ping")){
                ProjectMessage msg = new ProjectMessage();
                msg.setType(MessageType.pong.name());
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
