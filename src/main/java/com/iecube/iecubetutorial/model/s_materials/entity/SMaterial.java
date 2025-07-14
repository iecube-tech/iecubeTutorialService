package com.iecube.iecubetutorial.model.s_materials.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

import java.time.Instant;

@Data
public class SMaterial extends BaseEntity {
    private long id;
    private String title;
    private String name;
    private String knowledgePoint;
    private String outline;
    private String instruction;
    private String html;
    private long materialId;
    private long cover;
    private long resource;
}