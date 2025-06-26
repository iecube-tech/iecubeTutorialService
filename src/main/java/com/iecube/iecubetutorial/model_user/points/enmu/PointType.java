package com.iecube.iecubetutorial.model_user.points.enmu;

import lombok.Getter;

@Getter
public enum PointType {
    RECHARGE("充值"),
    CONSUME("扣除");

    private final String label;

    PointType(String label) {
        this.label = label;
    }
}
