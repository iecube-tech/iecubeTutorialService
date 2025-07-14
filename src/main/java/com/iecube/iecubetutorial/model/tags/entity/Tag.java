package com.iecube.iecubetutorial.model.tags.entity;

import lombok.Data;

import java.time.Instant;

@Data
public class Tag {
    private Long id;
    private String name;
    private Instant createTime;
}
