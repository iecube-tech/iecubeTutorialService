package com.iecube.iecubetutorial.model_admin.operator.vo;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class OrgSecVO extends BaseEntity {
    private long id;
    private String name;
    private String type;
    private int limit;
    private String status;
}
