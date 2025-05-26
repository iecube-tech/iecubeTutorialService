package com.iecube.iecubetutorial.model.materials.entity;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialEntity {
    private Long id;
    private Long userId;
    private String name;
    private String title;
    private String knowledgePoint;
    private String instruction;
    private String status;
    private Integer deleted;
    private String html;
    private Long resource;  // 对象
    private Date createTime;
    private Date updateTime;
}
