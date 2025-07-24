package com.iecube.iecubetutorial.model.project.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class Project {
    private String id;
    private Long userId;
    private String name;
    private String title;
    private String knowledgePoint;
    private String source;
    private Long materialId; // 讲义id
    private Long sMaterialId; // 案例Id
    private int removed;
    private Instant createTime;
}
