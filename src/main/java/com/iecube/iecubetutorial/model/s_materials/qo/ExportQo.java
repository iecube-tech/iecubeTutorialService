package com.iecube.iecubetutorial.model.s_materials.qo;

import com.iecube.iecubetutorial.model.resource.entity.Resource;
import com.iecube.iecubetutorial.model.tags.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class ExportQo {
    private long materialId;
    private long cover;
    private List<Tag> tags;
}
