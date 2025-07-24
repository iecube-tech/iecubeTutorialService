package com.iecube.iecubetutorial.model.htmlEditAi.entity;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ProjectMessage {
    // 当 type 为 user 或 ai 时 Content的内容为json对象
    // 当 type 为 current 时  current 中有值
    private String id;
    private String projectId;
    private String content;  // base64编码的 messageDto
    private Instant createTime;
    private String sio;  // 对话对应的SocketIO的userId 可以查到 对应的socketIO连接 ../url?user_id=sio
    private String type;
    private List<ProjectMessage> current; // 当websocket连接建立时 发送历史消息
}
