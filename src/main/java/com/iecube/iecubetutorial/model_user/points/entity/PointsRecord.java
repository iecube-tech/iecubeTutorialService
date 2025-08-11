package com.iecube.iecubetutorial.model_user.points.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class PointsRecord {
    private Long id;
    private Long oSecId;
    private Long accountId;
    private String type;
    private int points;
    private String projectId;
    private String projectChildId;
    private String projectMessageId;
    private Instant createTime;
}
