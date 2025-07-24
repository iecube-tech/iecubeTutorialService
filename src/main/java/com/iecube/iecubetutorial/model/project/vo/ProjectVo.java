package com.iecube.iecubetutorial.model.project.vo;

import lombok.Data;

import java.time.Instant;

@Data
public class ProjectVo {
    private String id;
    private String name;
    private String title;
    private String knowledgePoint;
    private String type;
    private String status;
    private Instant createTime;
}
