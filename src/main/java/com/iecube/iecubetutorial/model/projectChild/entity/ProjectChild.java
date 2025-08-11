package com.iecube.iecubetutorial.model.projectChild.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class ProjectChild {
    private String id;
    private String projectId;
    private Integer version;
    private Integer userVersion;
    @JsonProperty("saved")
    private Boolean saved;
    private Long resource;
    private Instant createTime;
    private int removed;
}
