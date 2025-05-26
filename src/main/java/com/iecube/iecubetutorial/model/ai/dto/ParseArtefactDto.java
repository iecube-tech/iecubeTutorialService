package com.iecube.iecubetutorial.model.ai.dto;

import lombok.Data;

@Data
public class ParseArtefactDto {
    private Long materialId;
    private String artefactId;
    private String status;
    private String error;
}
