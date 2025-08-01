package com.iecube.iecubetutorial.model_user.points.enmu;

import lombok.Getter;

@Getter
public enum PointType {
    RECHARGE("充值"),
    CONSUME("扣除"),
    CONSUME_OUTLINE_LOOK_FIRST("先看大纲扣除"),
    CONSUME_OUTLINE("大纲扣除"),
    CONSUME_GEN("生成扣除"),
    CONSUME_EDIT("修改扣除");

    private final String label;

    PointType(String label) {
        this.label = label;
    }
}
