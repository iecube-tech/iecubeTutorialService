package com.iecube.iecubetutorial.model.s_materials.vo;

import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class SMaterialVo {
    private long id;
    private String title;
    private String name;
    private String knowledgePoint;
    private String outline;
    private String instruction;
    private String html;
    private Resource file;
    private Resource cover;
    private List<Tag> tags;
}
