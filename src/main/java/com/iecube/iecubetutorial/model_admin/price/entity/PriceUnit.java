package com.iecube.iecubetutorial.model_admin.price.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class PriceUnit extends BaseEntity {
    private Long id;
    private String type;
    private int source;
    private String sourceUnits;
    private String sourceUnitsCn;
    private double target;
    private String targetUnits;
    private String targetUnitsCn;
    private int active;
}
