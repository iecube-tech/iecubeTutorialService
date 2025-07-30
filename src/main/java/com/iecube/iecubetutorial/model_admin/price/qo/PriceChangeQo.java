package com.iecube.iecubetutorial.model_admin.price.qo;

import lombok.Data;

@Data
public class PriceChangeQo {
    private double howPointsPerRMB;
    private double howTokensPerPoint;
    private int expireDays;
    private String approver;
}
