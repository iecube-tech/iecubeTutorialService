package com.iecube.iecubetutorial.model.mOutline.clientService;

import com.iecube.iecubetutorial.model.ai.exception.AiAPiResponseException;
import org.springframework.web.socket.WebSocketSession;

public interface W6ClientService {
    WebSocketSession connect(String chatId, String projectId, Long materialId) throws AiAPiResponseException;
}
