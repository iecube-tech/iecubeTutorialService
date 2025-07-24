package com.iecube.iecubetutorial.model.htmlEditAi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iecube.iecubetutorial.exception.InsertException;
import com.iecube.iecubetutorial.model.htmlEditAi.dto.MessageDto;
import com.iecube.iecubetutorial.model.htmlEditAi.entity.ProjectMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public interface MessageService {
    MessageDto formatWebSocketMessage(String sessionId, String payload);

    MessageDto formatSocketIOMessage(String sessionId, String data);

    void saveMessage(WebSocketSession session, MessageDto messageDto, String source) throws JsonProcessingException, InsertException;

    void saveMessage(ProjectMessage message) throws InsertException;

    List<ProjectMessage> getProjectMessage(String projectId);

    List<ProjectMessage> deleteProjectMessage(String messageId);
}
