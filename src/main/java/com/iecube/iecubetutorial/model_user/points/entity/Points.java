package com.iecube.iecubetutorial.model_user.points.entity;

import com.iecube.iecubetutorial.baseEntity.BaseEntity;
import lombok.Data;

import java.time.Instant;

@Data
public class Points extends BaseEntity {
    private Long id;
    private Long oSecId;
    private Double amount;
    private String status;
    private Instant expireDate;
}
