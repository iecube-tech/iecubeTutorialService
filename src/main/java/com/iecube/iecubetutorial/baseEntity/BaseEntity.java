package com.iecube.iecubetutorial.baseEntity;

import lombok.Data;

import java.time.Instant;

@Data
public class BaseEntity {
    private Integer removed;
    private String creator;
    private Instant createTime;
    private String lastOperator;
    private Instant lastOperateTime;
}
