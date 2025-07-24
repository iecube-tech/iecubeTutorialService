package com.iecube.iecubetutorial.model.htmlEditAi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.exception.ServiceException;
import com.iecube.iecubetutorial.model.htmlEditAi.config.WebsocketManager;
import com.iecube.iecubetutorial.model.htmlEditAi.dto.MessageDto;
import com.iecube.iecubetutorial.model.htmlEditAi.entity.ProjectMessage;
import com.iecube.iecubetutorial.model.htmlEditAi.enums.MessageDtoType;
import com.iecube.iecubetutorial.model.htmlEditAi.enums.MessageType;
import com.iecube.iecubetutorial.model.htmlEditAi.service.MessageService;
import com.iecube.iecubetutorial.model.htmlEditAi.service.SocketIOService;
import com.iecube.iecubetutorial.model.projectChild.entity.ProjectChild;
import com.iecube.iecubetutorial.model.projectChild.service.ProjectChildService;
import com.iecube.iecubetutorial.model.projectChild.vo.ProjectChildVo;
import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.resource.service.ResourceService;
import com.iecube.iecubetutorial.util.base64.Base64Util;
import com.iecube.iecubetutorial.util.uuid.UUIDGenerator;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SocketIOServiceImpl implements SocketIOService {

    private final Map<String, Socket> clients = new ConcurrentHashMap<>();
    private final Map<String, ScheduledExecutorService> reconnectExecutors = new ConcurrentHashMap<>();
    private final StringBuilder responseBuffer = new StringBuilder();

    private final MessageService messageService;
    private final WebsocketManager websocketManager;
    private final ObjectMapper objectMapper;
    private final ResourceService resourceService;
    private final ProjectChildService projectChildService;


    @Value("${HtmlEditAI.socketIO.baseUrl}")
    private String baseUrl;

    @Value("${HtmlEditAI.socketIO.reconnect-delay}")
    private long reconnectDelay;

    public SocketIOServiceImpl(MessageService messageService,
                               WebsocketManager websocketManager,
                               ObjectMapper objectMapper,
                               ResourceService resourceService,
                               ProjectChildService projectChildService) {
        this.messageService = messageService;
        this.websocketManager = websocketManager;
        this.objectMapper = objectMapper;
        this.resourceService = resourceService;
        this.projectChildService = projectChildService;
    }


    @Override
    public void connect(String sessionId) {
        if (clients.containsKey(sessionId)) {
            log.info("Socket.IO 已经存在 --> WebSocket {}", sessionId);
            return;
        }
        try {
            WebSocketSession session = websocketManager.existWebSocketSessionMap().get(sessionId);
            String userId = session.getAttributes().get("socketIOUserId").toString();
            log.debug("已注册 SocketIO {} --> WebSocket {}", userId, sessionId);
            String serverUrl = baseUrl+"?user_id="+userId;   // user_id 要作为变量
            IO.Options options = new IO.Options();// 创建 Socket 实例并配置
            options.reconnection = false; // 禁用内置重连，使用自定义逻辑
            options.transports = new String[]{"websocket", "polling"};
            Socket client = IO.socket(serverUrl, options);
            clients.put(sessionId, client);
            registerEvent(client, session);
            client.connect(); // 启动连接
            log.info("初始化 Socket.IO 链接 --> WebSocket {}", sessionId);
            if (!client.connected()) {
                scheduleReconnect(sessionId); // 如果第一次连接失败，安排重连
            }
        } catch (Exception e) {
            log.error("创建 Socket.IO 客户端错误, 执行重连机制 --> WebSocket {}", sessionId, e);
            scheduleReconnect(sessionId);
        }
    }

    private void registerEvent(Socket socket, WebSocketSession session) {
        String projectId = session.getAttributes().get("projectId").toString();
        String usr_id = session.getAttributes().get("socketIOUserId").toString();
        // 连接成功回调
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.info("Socket.IO 成功连接 --> WebSocket {}", session.getId());
                cancelReconnect(session.getId());
            }
        });
        // 断开连接回调
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                log.info("Socket.IO 断开连接 --> WebSocket {}", session.getId());
                clients.remove(session.getId());
                // 如果WebSocket连接仍然存在，计划重连
                if (shouldReconnect(session.getId())) {
                    scheduleReconnect(session.getId());
                }
            }
        });

        // 用户消息复述
//        socket.on("user_message_saved",args->{
//            System.out.println("user_message_saved");
//            System.out.println(Arrays.toString(args));
//        });

        // 处理开始 1
        socket.on("processing_start",args->{
            responseBuffer.setLength(0); // 清空缓冲区
            ProjectMessage projectMessage = new ProjectMessage();
            projectMessage.setType(MessageType.stream_start.name());
            projectMessage.setSio(usr_id);
            projectMessage.setProjectId(projectId);
            projectMessage.setCreateTime(Instant.now());
            try{
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(projectMessage)));
            }catch (Exception e){
                log.error(e.getMessage());
            }
        });

        // AI输出流 2
        socket.on("llm_output",args->{
            // System.out.println("llm_output");
            // System.out.println(Arrays.toString(args));
            if(args.length>0){
                responseBuffer.append(args[0].toString());
                // 发送消息给websocket
                ProjectMessage projectMessage = new ProjectMessage();
                projectMessage.setType(MessageType.stream.name());
                projectMessage.setContent(args[0].toString());
                projectMessage.setSio(usr_id);
                projectMessage.setProjectId(projectId);
                projectMessage.setCreateTime(Instant.now());
                try{
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(projectMessage)));
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        });

        // 处理完成 3
        socket.on("processing_complete",args->{
            // 处理 完整输出 保存消息
            System.out.println("processing_complete");
            System.out.println(Arrays.toString(args));

            // stream_end
            ProjectMessage streamEnd = new ProjectMessage();
            streamEnd.setType(MessageType.stream_end.name());
            streamEnd.setSio(usr_id);
            streamEnd.setProjectId(projectId);
            streamEnd.setCreateTime(Instant.now());
            try{
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(streamEnd)));
            }catch (Exception e){
                log.error(e.getMessage());
            }

            // complete
            ProjectMessage complete = new ProjectMessage();
            complete.setType(MessageType.complete.name());
            complete.setSio(usr_id);
            complete.setProjectId(projectId);
            complete.setCreateTime(Instant.now());
            complete.setContent(responseBuffer.toString());
            try{
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(complete)));
            }catch (Exception e){
                log.error(e.getMessage());
            }

            // user
            if(args.length>0 && args[0]!=null && args[0] instanceof JSONObject){
                try{
                    JSONObject data = (JSONObject) args[0];
                    if(data.getBoolean("success")){
                        // 处理文件
                        String html = Base64Util.encodeString(data.getString("updated_code"));
                        Resource resource = resourceService.saveResource(resourceService.writeHtmlToFile(html));
                        ProjectChild projectChild = projectChildService.createProjectChild(projectId, resource.getId());
                        ProjectChildVo pc = new ProjectChildVo();
                        pc.setId(projectChild.getId());
                        pc.setVersion(projectChild.getVersion());
                        pc.setCreateTime(projectChild.getCreateTime());
                        pc.setResource(resource);
                        MessageDto messageDto = new MessageDto();
                        messageDto.setProjectChildVo(pc);
                        messageDto.setFullCode(html);
                        messageDto.setFileName(resource.getFilename());
                        messageDto.setType(MessageDtoType.ai_complete.name());
                        messageDto.setProjectId(projectId);
                        messageDto.setMessage(responseBuffer.toString());
                        try{
                            String json = objectMapper.writeValueAsString(messageDto);
                            ProjectMessage projectMessage = new ProjectMessage();
                            projectMessage.setId(UUIDGenerator.generateUUID());
                            projectMessage.setProjectId(projectId);
                            projectMessage.setContent(Base64Util.encodeString(json));
                            projectMessage.setCreateTime(Instant.now());
                            projectMessage.setSio(usr_id);
                            projectMessage.setType(MessageType.ai.name());
                            messageService.saveMessage(projectMessage);
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(projectMessage)));
                            responseBuffer.setLength(0);
                        }catch (Exception e){
                            try {
                                log.error("转发SocketIO消息错误 {} --> Websocket {}",e.getMessage(), session.getId());
                                session.sendMessage(new TextMessage("""
                            {"type":"error","message":"服务错误:%s"}
                            """.formatted(e.getMessage())));
                            } catch (IOException ex) {
                                log.error("转发SocketIO消息错误 --> Websocket {}", session.getId());
                                throw new ServiceException();
                            }
                        }
                    }else {
                        session.sendMessage(new TextMessage("""
                            {"type":"error","content": %s }
                            """.formatted(data.getString("message"))));
                    }
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        });


        // 处理错误
        socket.on("processing_error",args->{
            if(args.length>0 && args[0]!=null && args[0] instanceof JSONObject){
                try {
                    JSONObject data = (JSONObject) args[0];
                    session.sendMessage(new TextMessage("""
                        {"type":"error","content": %s }
                        """.formatted(data.getString("message"))));
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        });

        // 错误事件
        socket.on("error",args->{
            if(args.length>0 && args[0]!=null && args[0] instanceof JSONObject){
                try {
                    JSONObject data = (JSONObject) args[0];
                    session.sendMessage(new TextMessage("""
                        {"type":"error","content": %s }
                        """.formatted(data.getString("message"))));
                }catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        });
    }

    @Override
    public void sendMessage(String sessionId, MessageDto messageDto) throws Exception {
        Socket client = clients.get(sessionId);
        Map<String,Object> data = new HashMap<>();
        data.put("user_id", websocketManager.existWebSocketSessionMap().get(sessionId).getAttributes().get("socketIOUserId"));
        data.put("user_input", messageDto.getMessage());
        List<Map<String,String>> selectedHtml = new ArrayList<>();
        messageDto.getSelectedElements().forEach(element -> {
            String html = Base64Util.decodeString(element.getHtml());
            Map<String, String> selected = new HashMap<>();
            selected.put("html", html);
            selectedHtml.add(selected);
        });
        data.put("selected_elements", selectedHtml);
        data.put("current_code", Base64Util.decodeString(messageDto.getFullCode()));
        if (client != null && client.isActive()) {
            client.emit("process_request",data);
            log.debug("向Socket.IO 发送消息 {} --> WebSocket {}", data, sessionId);
        } else {
            log.warn("发送消息错误: Socket.IO 连接断开 --> WebSocket {} ", sessionId);
            // 如果客户端断开连接，尝试重连
            if (client != null) {
                clients.remove(sessionId);
            }
            if (shouldReconnect(sessionId)) {
                scheduleReconnect(sessionId);
            }
            throw new Exception("AI对话异常断开，消息处理失败");
        }
    }

    @Override
    public void disconnect(String sessionId) {
        Socket client = clients.get(sessionId);
        if (client != null) {
            log.info("即将断开 Socket.IO 连接 --> WebSocket {}", sessionId);
            client.disconnect();
            clients.remove(sessionId);
        }
        cancelReconnect(sessionId);
    }

    private void scheduleReconnect(String sessionId) {
        cancelReconnect(sessionId);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        reconnectExecutors.put(sessionId, executor);
        executor.schedule(() -> {
            log.info("尝试 Socket.IO重连 --> WebSocket {}", sessionId);
            connect(sessionId);
        }, reconnectDelay, TimeUnit.MILLISECONDS);
        log.info("计划 Socket.IO {} ms 后重连 --> WebSocket {} ", sessionId, reconnectDelay);
    }

    private void cancelReconnect(String sessionId) {
        ScheduledExecutorService executor = reconnectExecutors.remove(sessionId);
        if (executor != null) {
            executor.shutdownNow();
            log.info("取消 Socket.IO 重连机制 --> Websocket {}", sessionId);
        }
    }

    private boolean shouldReconnect(String sessionId) {
        // 检查WebSocket连接是否仍然存在, 是否重连
        if(websocketManager.existWebSocketSessionMap().get(sessionId) != null){
            log.info("Socket.IO计划重连 --> WebSocket {}",sessionId);
            return true;
        }
        log.info("Socket.IO不再重连--> WebSocket {}",sessionId);
        return false;
    }

    @PreDestroy
    public void cleanup() {
        log.info("清空 Socket.IO 连接");
        for (String sessionId : clients.keySet()) {
            disconnect(sessionId);
        }
        clients.clear();
        reconnectExecutors.forEach((sessionId, executor) -> executor.shutdownNow());
        reconnectExecutors.clear();
    }
}
