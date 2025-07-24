package com.iecube.iecubetutorial.model.htmlEditAi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.exception.DeleteException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model.htmlEditAi.dto.MessageDto;
import com.iecube.iecubetutorial.model.htmlEditAi.entity.ProjectMessage;
import com.iecube.iecubetutorial.model.htmlEditAi.mapper.ProjectMessageMapper;
import com.iecube.iecubetutorial.model.htmlEditAi.service.MessageService;
import com.iecube.iecubetutorial.model.projectChild.entity.ProjectChild;
import com.iecube.iecubetutorial.util.base64.Base64Util;
import com.iecube.iecubetutorial.util.uuid.UUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectMessageMapper messageMapper;

    @Override
    public MessageDto formatWebSocketMessage(String sessionId, String payload) {
        return pressMsg(payload);
    }

    @Override
    public MessageDto formatSocketIOMessage(String sessionId, String payload) {
        return null;
    }

    @Override
    public void saveMessage(WebSocketSession session, MessageDto messageDto, String source) throws JsonProcessingException, InsertException {
        // 实际应用中这里会将消息存储到数据库
        ProjectMessage message = new ProjectMessage();
        message.setId(UUIDGenerator.generateUUID());
        message.setProjectId(session.getAttributes().get("projectId").toString());
        String jsonString = objectMapper.writeValueAsString(messageDto);
        message.setContent(Base64Util.encodeString(jsonString));
        message.setCreateTime(Instant.now());
        message.setSio(session.getAttributes().get("socketIOUserId").toString());
        message.setType(source);

        int res = messageMapper.insert(message);
        if(res!=1){
            throw new InsertException("保存数据异常");
        }
    }

    @Override
    public void saveMessage(ProjectMessage message) throws InsertException{
        int res = messageMapper.insert(message);
        if(res!=1){
            throw new InsertException("保存数据异常");
        }
    }

    @Override
    public List<ProjectMessage> getProjectMessage(String projectId) {
        return messageMapper.getByProject(projectId);
    }

    @Override
    public List<ProjectMessage> deleteProjectMessage(String messageId) {
        ProjectMessage message = messageMapper.getById(messageId);
        if(message==null){
            throw new DeleteException("没有找到想关数据");
        }
        String projectId = message.getProjectId();
        int res = messageMapper.delete(messageId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return this.getProjectMessage(projectId);
    }


    public void sendMessageToClient(WebSocketSession session, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                log.debug("向WebSocket {} 发送消息: {}", session.getId(), message);
            } catch (IOException e) {
                log.error("向WebSocket {} 发送消息错误", session.getId(), e);
            }
        } else {
            log.warn("无法发送消息: WebSocket 没有开启");
        }
    }

    private MessageDto pressMsg(String payload) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        // 手动将 JSON 转换为对象
        return (MessageDto) converter.fromMessage(
                new GenericMessage<>(payload),
                MessageDto.class
        );
    }
}
