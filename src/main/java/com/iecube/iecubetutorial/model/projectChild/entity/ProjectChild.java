package com.iecube.iecubetutorial.model.projectChild.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class ProjectChild {
    private String id;
    private String projectId;
    private Integer version;
    private Long resource;
    private Instant createTime;
    private int removed;
}
