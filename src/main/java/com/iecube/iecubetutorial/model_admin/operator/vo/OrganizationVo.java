package com.iecube.iecubetutorial.model_admin.operator.vo;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrganizationVo extends BaseEntity {
    private long id;
    private String name;
    private String type;
    private String status;
    private List<OrgSecVO> oSecList;
}
