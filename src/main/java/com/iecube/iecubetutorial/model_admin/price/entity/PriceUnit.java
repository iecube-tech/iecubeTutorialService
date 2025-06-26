package com.iecube.iecubetutorial.model_admin.price.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class PriceUnit extends BaseEntity {
    private Long id;
    private String type;
    private String typeCn;
    private String target;
    private String targetUnits;
    private String targetUnitsCn;
    private String need;
    private String needUnits;
    private String needUnitsCn;
    private int active;
}
