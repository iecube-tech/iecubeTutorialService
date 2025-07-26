package com.iecube.iecubetutorial.model.mOutline.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class MOutline {
    private String id;
    private String projectId;
    private String name;
    private String title;
    private String knowledgePoint;
    private String outline;
    private String chatId;
    private int sentToken;
    private int recvToken;
    private Instant createTime;
    private Long creator;
    private boolean show;
}
