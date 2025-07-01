package com.iecube.iecubetutorial.model_admin.price.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class PriceUnit extends BaseEntity {
    private Long id;
    private String type;
    private String typeCn;
    private double target;
    private String targetUnits;
    private String targetUnitsCn;
    private double need;
    private String needUnits;
    private String needUnitsCn;
    private int active;
}
