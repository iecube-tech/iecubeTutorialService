package com.iecube.iecubetutorial.model_user.points.enmu;

import lombok.Getter;

@Getter
public enum PointStatus {
    ACTIVE("有效"),
    EXPIRED("过期");

    private final String label;

    PointStatus(String label) {
        this.label = label;
    }
}
