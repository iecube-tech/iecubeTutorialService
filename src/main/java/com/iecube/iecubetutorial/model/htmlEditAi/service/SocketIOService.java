package com.iecube.iecubetutorial.model.htmlEditAi.service;

import com.iecube.iecubetutorial.model.htmlEditAi.dto.MessageDto;

public interface SocketIOService {
    void connect(String sessionId);

    void sendMessage(String sessionId, MessageDto messageDto) throws Exception;

    void disconnect(String sessionId);
}
