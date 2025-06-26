package com.iecube.iecubetutorial.model_admin.point.expire.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

@Data
public class ExpireDays extends BaseEntity {
    private Long id;
    private int days;
}
