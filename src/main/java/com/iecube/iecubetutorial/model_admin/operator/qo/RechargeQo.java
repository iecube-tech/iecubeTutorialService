package com.iecube.iecubetutorial.model_admin.operator.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RechargeQo {
    private String approver;
    @JsonProperty("oSecId")
    private long oSecId;
    private Double rmb;
    private Double pointsComputed;
}
