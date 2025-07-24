package com.iecube.iecubetutorial.model.project.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EditHtmlQo {
    @JsonProperty("pChildId")
    private String pChildId; //projectChildId
    private String htmlBase64;
}
