package com.iecube.iecubetutorial.model.projectChild.vo;

import com.iecube.iecubetutorial.model.resource.entity.Resource;
import lombok.Data;

import java.time.Instant;

@Data
public class ProjectChildVo {
    private String id;
    private Integer version;
    private Resource resource;
    private Instant createTime;
}
