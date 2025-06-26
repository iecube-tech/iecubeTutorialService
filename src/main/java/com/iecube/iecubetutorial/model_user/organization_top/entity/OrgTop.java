package com.iecube.iecubetutorial.model_user.organization_top.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class OrgTop extends BaseEntity {
    private Long id;
    private String name;
    private String type;  // P O
    private String status;
}
