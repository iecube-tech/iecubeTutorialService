package com.iecube.iecubetutorial.model_user.organization_sec.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import com.iecube.iecubetutorial.model_user.organization_top.entity.OrgTop;
import lombok.Data;

@Data
public class OrgSec extends BaseEntity {
    private Long id;
    private Long pId;
    private String name;
    private String type;
    private int limit;
    private String status;
    private OrgTop orgTop;
}
