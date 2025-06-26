package com.iecube.iecubetutorial.model_admin.operator.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddUUserQo {
    private String approver;
    @JsonProperty("oSecId")
    private Long oSecId;
    private List<UUserQo> users;
}
