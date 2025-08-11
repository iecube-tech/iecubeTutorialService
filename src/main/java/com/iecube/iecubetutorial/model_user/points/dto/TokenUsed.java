package com.iecube.iecubetutorial.model_user.points.dto;

import lombok.Data;

@Data
public class TokenUsed {
    private int sent;
    private int recv;
    private String projectId;
    private String projectChildId;
    private String projectMessageId;
}
