package com.iecube.iecubetutorial.model.mOutline.clientService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iecube.iecubetutorial.model.mOutline.entity.MOutline;
import com.iecube.iecubetutorial.model.mOutline.service.MOutlineService;
import com.iecube.iecubetutorial.model.mOutline.wsConfig.WsManager;
import com.iecube.iecubetutorial.model.materials.service.MaterialService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@Component
public class OutlineGenHandler extends TextWebSocketHandler {

    private final WsManager wsManager;
    private final MOutlineService mOutlineService;
    private final ObjectMapper objectMapper;
    private final MaterialService materialService;
    public OutlineGenHandler(WsManager wsManager,
                             MOutlineService mOutlineService,
                             ObjectMapper objectMapper,
                             MaterialService materialService) {
        this.wsManager=wsManager;
        this.mOutlineService=mOutlineService;
        this.objectMapper=objectMapper;
        this.materialService=materialService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        String text = message.getPayload();
        JsonNode rec =  objectMapper.readTree(text);
        String chatId = session.getAttributes().get("chatId")!=null?session.getAttributes().get("chatId").toString():"";
        log.debug("W6消息：chatId:{}",chatId);
        switch (rec.get("type").asText()){
            case "activity-start":
                Msg msg = new Msg();
                msg.setType("activity-start");
                sendMessageToOutlineSession(chatId,msg);
                break;
            case "stream":
                Msg msg1 = new Msg();
                msg1.setType("stream");
                msg1.setMessage(rec.get("payload").asText());
                sendMessageToOutlineSession(chatId,msg1);
                break;
            case "message":
                Msg msg2 = new Msg();
                msg2.setType("message");
                String outline = rec.get("payload").get("content").asText();
                msg2.setMessage(outline);
                MOutline mOutline = mOutlineService.getByChatId(chatId);
                mOutline.setOutline(outline);
                MOutline res = mOutlineService.updateMOutline(mOutline);
                msg2.setMoutline(res);
                sendMessageToOutlineSession(chatId,msg2);
                if(wsManager.OneClickGen().get(chatId)!=null){
                    // 去生成讲义
                    materialService.oneClickGen(res, wsManager.OneClickGen().get(chatId));
                    log.info("一键生成：大纲生成完毕，开始生成讲义");
                    wsManager.OneClickGen().remove(chatId);
                }
                break;
            case "activity-stop":
                Msg msg3 = new Msg();
                msg3.setType("activity-stop");
                sendMessageToOutlineSession(chatId,msg3);
                session.close(); //主动关闭和ai服务的连接
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        String chatId = (String) session.getAttributes().get("chatId");
        log.debug("close chatId:{}, status:{}",chatId,status);
        log.info("outline-W6:{} 断开连接,清理缓存",chatId);
        wsManager.OutlineW6WSMap().remove(chatId);
        log.info("大纲生成：{}结束后检查，前端连接数量：{}， w6连接数量：{}",chatId, wsManager.OutlineWsMap().size(), wsManager.OutlineW6WSMap().size());
    }


    private void sendMessageToOutlineSession(String chatId, Msg msg){
        log.debug("sendMessageToOutlineSession : chatId:{}, msg:{}",chatId,msg);
        if(chatId.isEmpty()){
            return;
        }
        WebSocketSession outlineSession = wsManager.OutlineWsMap().get(chatId);
        log.debug("outlineSession: {}",outlineSession);
        if(outlineSession!=null&&outlineSession.isOpen()){
            try {
                outlineSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
            } catch (IOException e) {
                log.error("objectMapper 异常: {}",e.getMessage());
            }
        }
    }

    @Data
    public static class Msg{
        private String type;
        private String message;
        private MOutline moutline;
    }
}
