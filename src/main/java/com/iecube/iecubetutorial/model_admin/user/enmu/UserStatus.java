package com.iecube.iecubetutorial.model_admin.user.enmu;

import lombok.Getter;

@Getter
public enum UserStatus {
    ENABLED("ENABLED","可用"),
    DISABLED("DISABLED","禁用");

    private final String status;
    private final String description;

    UserStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }
}
