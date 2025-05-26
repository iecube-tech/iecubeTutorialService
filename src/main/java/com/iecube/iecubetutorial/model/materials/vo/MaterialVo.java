package com.iecube.iecubetutorial.model.materials.vo;

import com.iecube.iecubetutorial.model.resource.entity.Resource;
import lombok.Data;

import java.util.Date;

@Data
public class MaterialVo {
    private Long id;
    private String name;
    private String title;
    private String knowledgePoint;
    private String instruction;
    private String status;
    private String html;
    private Date createTime;
    private Date updateTime;
    private Resource resource;
}
