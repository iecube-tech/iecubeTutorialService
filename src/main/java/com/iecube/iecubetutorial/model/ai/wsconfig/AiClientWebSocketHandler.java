package com.iecube.iecubetutorial.model.ai.wsconfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.model.ai.dto.ParseArtefactDto;
import com.iecube.iecubetutorial.model.materials.enmus.MaterialStatus;
import com.iecube.iecubetutorial.model.materials.entity.MaterialEntity;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class AiClientWebSocketHandler extends TextWebSocketHandler {

    private final ConcurrentHashMap<String, WebSocketSession> ChatIdToSession;
    private final ConcurrentHashMap<String, String> SessionIdToChatId;
    private final ConcurrentHashMap<String, MaterialEntity>ChatIdToMaterial;
    private final BlockingQueue<ParseArtefactDto> NewParseTask;

    private ScheduledExecutorService scheduler;

    private AtomicLong lastSentTime;

    @Autowired
    private MaterialService materialService;

    @Autowired
    public AiClientWebSocketHandler(ConcurrentHashMap<String, WebSocketSession> ChatIdToSession,
                                    ConcurrentHashMap<String, String> SessionIdToChatId,
                                    BlockingQueue<ParseArtefactDto> NewParseTask,
                                    ConcurrentHashMap<String, MaterialEntity>ChatIdToMaterial){
        this.ChatIdToSession = ChatIdToSession;
        this.SessionIdToChatId = SessionIdToChatId;
        this.ChatIdToMaterial = ChatIdToMaterial;
        this.NewParseTask = NewParseTask;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        URI uri = session.getUri();
        if (uri != null) {
            String path = uri.toString();
            String chatId = path.substring(path.lastIndexOf('/') + 1);
            ChatIdToSession.put(chatId, session);
            SessionIdToChatId.put(session.getId(), chatId);
        }
        long timeout = 10000; // 超时时间（毫秒）3个心跳的时间
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.lastSentTime = new AtomicLong(System.currentTimeMillis());
        // 定时任务，每秒检查一次
        this.scheduler.scheduleAtFixedRate(() -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastSentTime.get() > timeout) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new PingMessage());
                    } catch (IOException e) {
                        log.error("PING w6 异常{}",e.getMessage());
                    }
                } else {
                    this.scheduler.shutdown(); // 关闭调度器
                }
        }
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String text = message.getPayload();
//        log.info("message:{}",text);
        try{
            String chatId = SessionIdToChatId.get(session.getId());
//            log.error("chatId:{}",chatId);
            MaterialEntity material = ChatIdToMaterial.get(chatId);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rec =  objectMapper.readTree(text);
            switch (rec.get("type").asText()){
                case "current":
                    if(!rec.get("current").get("messages").isEmpty()){
                        if(rec.get("current").get("messages").get(0).get("role").asText().equals("assistant")){
                            String artefactId = rec.get("current").get("messages").get(0).get("artefacts").get(0).get("id").asText();
                            ParseArtefactDto dto = new ParseArtefactDto();
                            dto.setArtefactId(artefactId);
                            dto.setStatus(artefactId.isEmpty()?MaterialStatus.FAILED.getStatus():MaterialStatus.DONE.getStatus());
                            dto.setError(artefactId.isEmpty()?"获取AI返回的文件ID错误":null);
                            dto.setMaterialId(material.getId());
                            NewParseTask.put(dto);
                            session.close();
                        }
                    }
                    break;
                case "activity-start":
                    log.info("W6:{}开始输出",chatId);
                    material.setStatus(MaterialStatus.GENERATING.getStatus());
                    material.setUpdateTime(new Date());
                    materialService.handelUpload(material);
                    break;
                case "message-ack" :
                    String artefactId="";
                    if(rec.get("payload").get("role").asText().equals("assistant")){
                        artefactId = rec.get("payload").get("artefacts").get(0).get("id").asText();
                        log.info("W6:{}输出最终结果，artefactId：{}",chatId,artefactId);
                    }
                    // 生产 parseArtefactDto数据
                    ParseArtefactDto dto = new ParseArtefactDto();
                    dto.setArtefactId(artefactId);
                    dto.setStatus(artefactId.isEmpty()?MaterialStatus.FAILED.getStatus():MaterialStatus.DONE.getStatus());
                    dto.setError(artefactId.isEmpty()?"获取AI返回的文件ID错误":null);
                    dto.setMaterialId(material.getId());
                    NewParseTask.put(dto);
                    break;
                case "activity-stop":
                    log.info("W6:{}结束输出，即将断开",chatId);
                    session.close();
                    break;
                default:
                    break;
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        this.scheduler.shutdown(); // 关闭调度器
        String chatId = SessionIdToChatId.get(session.getId());
        log.info("W6:{} 断开连接",chatId);
        if(chatId != null){
            ChatIdToSession.remove(chatId);
            ChatIdToMaterial.remove(chatId);
        }
        SessionIdToChatId.remove(session.getId());
    }
}
