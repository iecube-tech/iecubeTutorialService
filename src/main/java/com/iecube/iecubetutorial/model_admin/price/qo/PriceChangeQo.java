package com.iecube.iecubetutorial.model_admin.price.qo;

import lombok.Data;

@Data
public class PriceChangeQo {
    private double howRmbToOnePoint;
    private double howPointsToOneGenerate;
    private int expireDays;
    private String approver;
}
