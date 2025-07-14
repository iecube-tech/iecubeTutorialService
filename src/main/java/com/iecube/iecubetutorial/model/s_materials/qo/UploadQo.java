package com.iecube.iecubetutorial.model.s_materials.qo;

import com.iecube.iecubetutorial.model.tags.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class UploadQo {
    private long file;
    private long cover;
    private String title;
    private String name;
    private String knowledgePoint;
    private String outline;
    private String instruction;
    private String html;
    private List<Tag> tags;
}
