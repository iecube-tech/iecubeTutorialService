package com.iecube.iecubetutorial.model.resource.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Resource {
    private Long id;
    private String filename;
    private String md5;
    private String type;
    private Date createTime;
    private Date updateTime;
}
